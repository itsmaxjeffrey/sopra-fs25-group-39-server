package ch.uzh.ifi.hase.soprafs24.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;

/**
 * Controller for handling file uploads and retrievals
 * This separates file handling from user registration
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileStorageController {

    private static final Logger log = LoggerFactory.getLogger(FileStorageController.class); // Logger instance


    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "profile", "license", "insurance", "car", "misc"
        );

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Upload a file and return its storage path
     * @param file The file to upload
     * @param fileType The type of file (profile, license, insurance, car)
     * @return The filepath where the file was stored
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String fileType) {
        
        // Validate file is not empty
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please upload a file");
        }
        
        // Validate file type
        if (!ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "File type must be one of: " + String.join(", ", ALLOWED_FILE_TYPES));
        }
        
        String subdirectory;
        subdirectory = switch (fileType) {
            case "profile" -> "profile-pictures";
            case "license" -> "driver-licenses";
            case "insurance" -> "driver-insurances";
            case "car" -> "car-pictures";
            default -> "misc";
        };

        String filePath = fileStorageService.storeFile(file, subdirectory);

        log.info("File uploaded successfully. File path: {}", filePath);
        
        Map<String, String> response = new HashMap<>();
        response.put("filePath", filePath);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Download a file by its filepath
     * @param filePath The path of the file to download
     * @return The file resource
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
        log.info("Attempting to download file: {}", filePath);
    
        Resource resource = fileStorageService.loadFileAsResource(filePath);
        String contentType = "application/octet-stream";
    
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}