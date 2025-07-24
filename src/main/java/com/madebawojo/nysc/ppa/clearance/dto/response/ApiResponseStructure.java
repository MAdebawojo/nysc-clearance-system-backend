package com.madebawojo.nysc.ppa.clearance.dto.response;

import java.time.Instant;
import java.util.List;

public class ApiResponseStructure<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private int statusCode;
    private Instant timestamp;

    // Constructors

    public ApiResponseStructure(boolean success, String message, T data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
    }

    public ApiResponseStructure(boolean success, String message, List<String> errors, int statusCode) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
    }

    // Static factory methods for consistency
    public static <T> ApiResponseStructure<T> success(String message, T data, int statusCode) {
        return new ApiResponseStructure<>(true, message, data, statusCode);
    }

    public static <T> ApiResponseStructure<T> error(String message, List<String> errors, int statusCode) {
        return new ApiResponseStructure<>(false, message, errors, statusCode);
    }

    public static <T> ApiResponseStructure<T> error(String message, int statusCode) {
        return new ApiResponseStructure<>(false, message, null, statusCode);
    }

    // Getters and Setters
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "ApiResponseStructure{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errors=" + errors +
                ", statusCode=" + statusCode +
                ", timestamp=" + timestamp +
                '}';
    }

}