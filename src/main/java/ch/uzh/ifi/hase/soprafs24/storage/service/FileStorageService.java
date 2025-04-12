package ch.uzh.ifi.hase.soprafs24.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;



@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Could not create the directory where the uploaded files will be stored", 
                ex);
        }
    }

    public String storeFile(MultipartFile file, String subdirectory) {
        // Normalize file name
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (originalFilename.contains("..")) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Sorry! Filename contains invalid path sequence " + originalFilename);
            }

            // Create subdirectory if needed
            Path subDirPath = this.fileStorageLocation.resolve(subdirectory);
            if (!Files.exists(subDirPath)) {
                Files.createDirectories(subDirPath);
            }

            // Generate a unique filename to prevent collisions
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = subDirPath.resolve(uniqueFilename);

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
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
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
}