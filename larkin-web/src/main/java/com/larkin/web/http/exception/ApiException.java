package com.larkin.web.http.exception;

public class ApiException extends RuntimeException {
    private final ApiError apiError;
    private boolean warningOnly = false;

    public ApiException(final ApiError apiError) {
        this.apiError = apiError;
    }
    public ApiException(final ApiError apiError, final boolean warningOnly) {
        this.apiError = apiError;
        this.warningOnly = warningOnly;
    }
    public ApiException(final ApiError apiError, final String message) {
        super(message);
        this.apiError = apiError;
    }

    public boolean isWarningOnly() {
        return warningOnly;
    }

    public ApiError getApiError() {
        return apiError;
    }
}

