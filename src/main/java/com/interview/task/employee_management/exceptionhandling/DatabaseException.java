package com.interview.task.employee_management.exceptionhandling;

public class DatabaseException extends ApplicationException{
    public DatabaseException(String message) {
        super(message,ErrorCode.DB_ERROR.getCode());
    }

}
