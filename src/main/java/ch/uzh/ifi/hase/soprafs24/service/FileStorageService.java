package ch.uzh.ifi.hase.soprafs24.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    // Helper method to validate and safely resolve user-supplied path components
    private Path safeResolve(Path root, String userInput) {
        // Only allow safe characters (alphanumeric, dash, underscore, dot, slash for subdirs)
        if (!userInput.matches("^[\\w\\-./]+$") || userInput.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path component: " + userInput);
        }
        Path resolved = root.resolve(userInput).normalize();
        if (!resolved.startsWith(root)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path traversal attempt detected: " + userInput);
        }
        return resolved;
    }

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            // Directory already exists, ignore
            log.warn("Directory already exists: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not create the directory where the uploaded files will be stored",
                    ex);
        }
    }

    public String storeFile(MultipartFile file, String subdirectory) {
        // Normalize file name
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must have a name");
        }
        originalFilename = StringUtils.cleanPath(originalFilename);

        try {
            // Check if the file is empty
            if (file.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "File is empty. Please upload a non-empty file.");
            }

            // Check if the file's name contains invalid characters
            if (originalFilename.contains("..")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            // Validate and resolve subdirectory safely
            Path subDirPath = safeResolve(this.fileStorageLocation, subdirectory);
            if (!Files.exists(subDirPath)) {
                Files.createDirectories(subDirPath);
            }

            // Generate a unique filename to prevent collisions
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            // Validate and resolve the target file path safely
            Path targetLocation = safeResolve(subDirPath, uniqueFilename);

            // Copy file to the target location (replacing existing file with the same name)
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return subdirectory + "/" + uniqueFilename;
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store file " + originalFilename + ". Please try again!",
                    ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            // Validate and resolve the file path safely
            Path filePath = safeResolve(this.fileStorageLocation, fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "File not found " + fileName,
                    ex);
        }
    }

    public void deleteFile(String fileName) {
        try {
            // Validate and resolve the file path safely
            Path filePath = safeResolve(this.fileStorageLocation, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File not found, could not delete: {}", fileName);
                // Optionally, you could throw a specific exception here if you want to inform
                // the caller
                // For example: throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File
                // not found " + fileName);
                // However, for a delete operation, if the file is already gone, it might be
                // acceptable to not error out.
            }
        } catch (IOException ex) {
            log.error("Could not delete file: {}. Error: {}", fileName, ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not delete file " + fileName + ". Please try again!",
                    ex);
        }
    }
}