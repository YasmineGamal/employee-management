package com.interview.task.employee_management.exceptionhandling;

public class EmployeeNotFoundException extends ApplicationException {

    public EmployeeNotFoundException(String message) {
        super(message,ErrorCode.EMP_NOT_FOUND.getCode());
    }

}
