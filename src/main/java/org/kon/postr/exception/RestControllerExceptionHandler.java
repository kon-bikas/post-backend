package org.kon.postr.exception;

import org.kon.postr.response.ApiResponse;
import org.kon.postr.response.SimpleBodyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.kon.postr.response.ApiResponse.Status;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        ApiResponse response = new ApiResponse(
                Status.ERROR,
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<SimpleBodyResponse<Map<String, String>>> handleException(
                                                            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        SimpleBodyResponse<Map<String, String>> response = new SimpleBodyResponse<>(
                Status.ERROR,
                "Values passed are not valid.",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = ObjectStorageException.class)
    public ResponseEntity<SimpleBodyResponse<Map<String, String>>> handleException(ObjectStorageException ex) {
        Map<String, String> errors = Map.ofEntries(
                Map.entry("message", ex.getMessage()),
                Map.entry("cause", ex.getCause().getMessage())
        );

        SimpleBodyResponse<Map<String, String>> response = new SimpleBodyResponse<>(
                Status.ERROR,
                "Error occurred from storage service",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleException(ResourceNotFoundException ex) {
        ApiResponse response = new ApiResponse(
                Status.ERROR,
                "Error occurred from storage service"
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleException(MaxUploadSizeExceededException ex) {
        ApiResponse response = new ApiResponse(
                Status.ERROR,
                "File provided should be smaller in size."
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

}
