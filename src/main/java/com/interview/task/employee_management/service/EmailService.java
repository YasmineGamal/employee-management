package com.interview.task.employee_management.service;

public interface EmailService {

    boolean isValidEmail(String email);

    void sendEmail(String to, String subject, String body);
}
