package com.interview.task.employee_management.exceptionhandling;

public class ApplicationException extends RuntimeException{
    private String errorCode;

    public ApplicationException(String message) {
        super(message);
        this.errorCode = ErrorCode.APPLICATION_ERROR.getCode();
    }

    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
