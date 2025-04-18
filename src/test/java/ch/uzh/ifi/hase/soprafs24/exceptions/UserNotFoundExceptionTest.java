package ch.uzh.ifi.hase.soprafs24.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserNotFoundExceptionTest {

    @Test
    public void testUserNotFoundExceptionWithUserId() {
        // Given
        Long userId = 123L;
        String expectedMessage = String.format("User with ID %d was not found", userId);

        // When
        UserNotFoundException exception = new UserNotFoundException(userId);

        // Then
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    public void testUserNotFoundExceptionWithCustomMessage() {
        // Given
        String customMessage = "Custom error message";

        // When
        UserNotFoundException exception = new UserNotFoundException(customMessage);

        // Then
        assertEquals(customMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getClass().getAnnotation(ResponseStatus.class).value());
    }

    @Test
    public void testExceptionThrowing() {
        // Given
        Long userId = 123L;

        // When/Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> { throw new UserNotFoundException(userId); }
        );

        assertEquals(String.format("User with ID %d was not found", userId), exception.getMessage());
    }
} 