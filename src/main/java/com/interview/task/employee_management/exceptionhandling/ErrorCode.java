package com.interview.task.employee_management.exceptionhandling;

public enum ErrorCode {
    DB_ERROR("DB_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    EMP_NOT_FOUND("EMP_NOT_FOUND"),
    DEPT_NOT_FOUND("DEPT_NOT_FOUND"),
    APPLICATION_ERROR("APP_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
