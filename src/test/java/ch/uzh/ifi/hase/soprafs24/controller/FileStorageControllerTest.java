package ch.uzh.ifi.hase.soprafs24.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs24.service.FileStorageService;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileStorageControllerTest {

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileStorageController fileStorageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFile_success() {
        // given
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt", "text/plain", "Test content".getBytes());
        String fileType = "profile";
        String expectedPath = "profile-pictures/test.txt";
        
        when(fileStorageService.storeFile(any(), anyString())).thenReturn(expectedPath);

        // when
        ResponseEntity<Map<String, String>> response = fileStorageController.uploadFile(file, fileType);

        // then
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedPath, body.get("filePath"));
        verify(fileStorageService, times(1)).storeFile(file, "profile-pictures");
    }

    @Test
    void uploadFile_emptyFile_throwsException() {
        // given
        MultipartFile file = new MockMultipartFile("empty.txt", new byte[0]);
        String fileType = "profile";

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageController.uploadFile(file, fileType));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("Please upload a file", reason);
    }

    @Test
    void uploadFile_invalidFileType_throwsException() {
        // given
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt", "text/plain", "Test content".getBytes());
        String fileType = "invalid";

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageController.uploadFile(file, fileType));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("File type must be one of"));
    }

    @Test
    void downloadFile_success() {
        // given
        String filePath = "profile-pictures/test.txt";
        Resource mockResource = mock(Resource.class);
        when(fileStorageService.loadFileAsResource(filePath)).thenReturn(mockResource);
        when(mockResource.getFilename()).thenReturn("test.txt");

        // when
        ResponseEntity<Resource> response = fileStorageController.downloadFile(filePath);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResource, response.getBody());
        verify(fileStorageService, times(1)).loadFileAsResource(filePath);
    }

    @Test
    void downloadFile_fileNotFound_throwsException() {
        // given
        String filePath = "nonexistent.txt";
        when(fileStorageService.loadFileAsResource(filePath))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageController.downloadFile(filePath));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertEquals("File not found", reason);
    }

    @Test
    void uploadFile_allFileTypes_success() {
        // given
        String[] fileTypes = {"profile", "license", "insurance", "car", "misc"};
        String[] expectedSubdirectories = {
            "profile-pictures", "driver-licenses", "driver-insurances", "car-pictures", "misc"
        };
        
        for (int i = 0; i < fileTypes.length; i++) {
            MultipartFile file = new MockMultipartFile(
                "test.txt", "test.txt", "text/plain", "Test content".getBytes());
            String expectedPath = expectedSubdirectories[i] + "/test.txt";
            
            when(fileStorageService.storeFile(any(), anyString())).thenReturn(expectedPath);

            // when
            ResponseEntity<Map<String, String>> response = fileStorageController.uploadFile(file, fileTypes[i]);

            // then
            assertEquals(200, response.getStatusCodeValue());
            Map<String, String> body = response.getBody();
            assertNotNull(body);
            assertEquals(expectedPath, body.get("filePath"));
            verify(fileStorageService, times(1)).storeFile(file, expectedSubdirectories[i]);
            
            // Reset mock for next iteration
            reset(fileStorageService);
        }
    }

    @Test
    void uploadFile_largeFile_success() {
        // given
        byte[] content = new byte[1024 * 1024]; // 1MB file
        new Random().nextBytes(content);
        MultipartFile file = new MockMultipartFile("large.txt", "large.txt", "text/plain", content);
        String fileType = "profile";
        String expectedPath = "profile-pictures/large.txt";
        
        when(fileStorageService.storeFile(any(), anyString())).thenReturn(expectedPath);

        // when
        ResponseEntity<Map<String, String>> response = fileStorageController.uploadFile(file, fileType);

        // then
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedPath, body.get("filePath"));
        verify(fileStorageService, times(1)).storeFile(file, "profile-pictures");
    }

    @Test
    void uploadFile_specialCharacters_success() {
        // given
        String filename = "test-äöüß@#$%^&().txt";
        MultipartFile file = new MockMultipartFile(filename, filename, "text/plain", "Test content".getBytes());
        String fileType = "profile";
        String expectedPath = "profile-pictures/" + filename;
        
        when(fileStorageService.storeFile(any(), anyString())).thenReturn(expectedPath);

        // when
        ResponseEntity<Map<String, String>> response = fileStorageController.uploadFile(file, fileType);

        // then
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedPath, body.get("filePath"));
        verify(fileStorageService, times(1)).storeFile(file, "profile-pictures");
    }

    @Test
    void downloadFile_specialCharacters_success() {
        // given
        String filePath = "profile-pictures/test-äöüß@#$%^&().txt";
        Resource mockResource = mock(Resource.class);
        when(fileStorageService.loadFileAsResource(filePath)).thenReturn(mockResource);
        when(mockResource.getFilename()).thenReturn("test-äöüß@#$%^&().txt");

        // when
        ResponseEntity<Resource> response = fileStorageController.downloadFile(filePath);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResource, response.getBody());
        verify(fileStorageService, times(1)).loadFileAsResource(filePath);
    }
} 