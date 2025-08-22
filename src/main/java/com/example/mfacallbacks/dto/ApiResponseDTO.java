package com.example.mfacallbacks.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic API response wrapper that follows a consistent structure across all endpoints.
 * 
 * <p>This class provides a standardized way to return success/error responses with optional data.
 * It includes utility methods for common response patterns.
 * 
 * @param <T> The type of the data payload included in the response
 */
@Data
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
@lombok.Generated // Prevent warnings about generated code
public class ApiResponseDTO<T> {
    
    /**
     * Indicates whether the operation was successful.
     */
    @Schema(
        description = "Indicates if the operation was successful",
        example = "true"
    )
    private boolean success;
    
    /**
     * A human-readable message describing the result of the operation.
     */
    @Schema(
        description = "Human-readable message describing the operation result",
        example = "Operation completed successfully"
    )
    private String message;
    
    /**
     * The response data payload. The structure depends on the specific API endpoint.
     * This field is omitted from the response when null.
     */
    @Schema(
        description = "Response data payload (structure varies by endpoint)",
        nullable = true
    )
    private T data;
    
    /**
     * Creates a new ApiResponse instance.
     * 
     * @param success whether the operation was successful
     * @param message the response message
     * @param data the response data (can be null)
     */
    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Creates a successful response with the default success message.
     * 
     * @param <T> The type of the data payload
     * @param data The response data
     * @return A successful ApiResponse instance
     */
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(true, "Operation successful", data);
    }
    
    /**
     * Creates a successful response with a custom message.
     * 
     * @param <T> The type of the data payload
     * @param message A custom success message
     * @param data The response data
     * @return A successful ApiResponse instance with custom message
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data);
    }
    
    /**
     * Creates an error response with the specified error message.
     * 
     * @param <T> The type of the data payload (usually Void for errors)
     * @param message The error message
     * @return An error ApiResponse instance
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return new ApiResponseDTO<>(false, message, null);
    }
}
