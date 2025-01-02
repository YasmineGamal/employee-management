package com.interview.task.employee_management.exceptionhandling;

public class InvalidInputException extends ApplicationException{

    public InvalidInputException(String message) {
        super(message,ErrorCode.VALIDATION_ERROR.getCode());
    }
}
