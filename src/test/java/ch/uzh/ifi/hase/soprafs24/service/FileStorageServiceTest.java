package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;
    private FileStorageService fileStorageService;
    private final String testSubdirectory = "test-files";

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void storeFile_success() {
        // given
        String originalFilename = "test.txt";
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // when
        String storedPath = fileStorageService.storeFile(file, testSubdirectory);

        // then
        assertNotNull(storedPath);
        assertTrue(storedPath.startsWith(testSubdirectory + "/"));
        assertTrue(storedPath.endsWith(".txt"));
        
        Path storedFile = tempDir.resolve(storedPath);
        assertTrue(Files.exists(storedFile));
        assertArrayEquals(content, readFileContent(storedFile));
    }

    @Test
    void storeFile_invalidPath_throwsException() {
        // given
        String originalFilename = "../test.txt";
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageService.storeFile(file, testSubdirectory));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("invalid path sequence"));
    }

    @Test
    void storeFile_emptyFile_throwsException() {
        // given
        String originalFilename = "empty.txt";
        byte[] content = new byte[0];
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageService.storeFile(file, testSubdirectory));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("empty"));
    }

    @Test
    void loadFileAsResource_success() {
        // given
        String filename = "test.txt";
        String content = "Test content";
        Path testFile = tempDir.resolve(testSubdirectory).resolve(filename);
        try {
            Files.createDirectories(testFile.getParent());
            Files.write(testFile, content.getBytes());
        } catch (IOException e) {
            fail("Failed to create test file");
        }

        // when
        Resource resource = fileStorageService.loadFileAsResource(testSubdirectory + "/" + filename);

        // then
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(filename, resource.getFilename());
    }

    @Test
    void loadFileAsResource_fileNotFound_throwsException() {
        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> fileStorageService.loadFileAsResource("nonexistent.txt"));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void storeFile_differentFileTypes_success() {
        // given
        String[] fileTypes = {"jpg", "png", "pdf", "txt"};
        for (String type : fileTypes) {
            String filename = "test." + type;
            byte[] content = ("Test content for " + type).getBytes();
            MultipartFile file = new MockMultipartFile(filename, filename, "application/" + type, content);

            // when
            String storedPath = fileStorageService.storeFile(file, testSubdirectory);

            // then
            assertNotNull(storedPath);
            assertTrue(storedPath.startsWith(testSubdirectory + "/"));
            assertTrue(storedPath.endsWith("." + type));
            
            Path storedFile = tempDir.resolve(storedPath);
            assertTrue(Files.exists(storedFile));
            assertArrayEquals(content, readFileContent(storedFile));
        }
    }

    @Test
    void storeFile_largeFile_success() {
        // given
        String originalFilename = "large.txt";
        byte[] content = new byte[1024 * 1024]; // 1MB file
        new Random().nextBytes(content);
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // when
        String storedPath = fileStorageService.storeFile(file, testSubdirectory);

        // then
        assertNotNull(storedPath);
        assertTrue(storedPath.startsWith(testSubdirectory + "/"));
        assertTrue(storedPath.endsWith(".txt"));
        
        Path storedFile = tempDir.resolve(storedPath);
        assertTrue(Files.exists(storedFile));
        assertArrayEquals(content, readFileContent(storedFile));
    }

    @Test
    void storeFile_specialCharacters_success() {
        // given
        String originalFilename = "test-äöüß@#$%^&().txt";
        byte[] content = "Test content with special chars".getBytes();
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // when
        String storedPath = fileStorageService.storeFile(file, testSubdirectory);

        // then
        assertNotNull(storedPath);
        assertTrue(storedPath.startsWith(testSubdirectory + "/"));
        assertTrue(storedPath.endsWith(".txt"));
        
        Path storedFile = tempDir.resolve(storedPath);
        assertTrue(Files.exists(storedFile));
        assertArrayEquals(content, readFileContent(storedFile));
    }

    @Test
    void storeFile_concurrentOperations_success() throws InterruptedException, ExecutionException {
        // given
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<String>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                String filename = "test-" + threadId + ".txt";
                byte[] content = ("Content from thread " + threadId).getBytes();
                MultipartFile file = new MockMultipartFile(filename, filename, "text/plain", content);
                return fileStorageService.storeFile(file, testSubdirectory);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // then
        for (Future<String> future : futures) {
            String storedPath = future.get();
            assertNotNull(storedPath);
            assertTrue(storedPath.startsWith(testSubdirectory + "/"));
            assertTrue(storedPath.endsWith(".txt"));
            
            Path storedFile = tempDir.resolve(storedPath);
            assertTrue(Files.exists(storedFile));
        }
    }

    @Test
    void storeFile_permissionError_throwsException() {
        // given
        String originalFilename = "test.txt";
        byte[] content = "Test content".getBytes();
        MultipartFile file = new MockMultipartFile(originalFilename, originalFilename, "text/plain", content);

        // Create a FileStorageService that will throw an IOException when trying to store a file
        FileStorageService serviceWithError = new FileStorageService(tempDir.toString()) {
            @Override
            public String storeFile(MultipartFile file, String subdirectory) {
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store file " + file.getOriginalFilename() + ". Please try again!",
                    new IOException("Simulated permission error"));
            }
        };

        // when/then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> serviceWithError.storeFile(file, testSubdirectory));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        String reason = exception.getReason();
        assertNotNull(reason);
        assertTrue(reason.contains("Could not store file"));
    }

    private byte[] readFileContent(Path filePath) {
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            fail("Failed to read file content");
            return new byte[0];
        }
    }
} 