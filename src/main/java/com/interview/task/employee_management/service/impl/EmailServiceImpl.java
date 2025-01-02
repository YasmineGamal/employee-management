package com.interview.task.employee_management.service.impl;


import com.interview.task.employee_management.exceptionhandling.ApplicationException;
import com.interview.task.employee_management.exceptionhandling.InvalidInputException;
import com.interview.task.employee_management.model.EmailValidationResponse;
import com.interview.task.employee_management.service.EmailService;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    @Autowired
    RestTemplate restTemplate;
    @Value("${mailjet.api-key}")
    private String apiKey;
    @Value("${mailjet.secret-key}")
    private String secretKey;
    @Value("${email.new-emp-template.sender.mail}")
    private String senderEmail;
    @Value("${email.new-emp-template.sender.name}")
    private String senderName;

    @Value("${zero-bounce.api-key}")
    private String validationApiKey;
    @Value("${zero-bounce.validation-api-url}")
    private String validationApiUrl;

    @Override
    public void sendEmail(String recipientEmail, String subject, String body) {
        try {
            // Configure Mailjet client
            ClientOptions options = ClientOptions.builder()
                    .apiKey(apiKey)
                    .apiSecretKey(secretKey)
                    .build();
            MailjetClient client = new MailjetClient(options);

            // Create email payload
            JSONObject message = new JSONObject();
            message.put("From", new JSONObject()
                    .put("Email", senderEmail)
                    .put("Name", senderName));

            message.put("To", new JSONArray()
                    .put(new JSONObject()
                            .put("Email", recipientEmail)
                            .put("Name", "Recipient Name")));

            message.put("Subject", subject); // Email subject
            message.put("TextPart", body); // Email body

            // Create request and send email
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray().put(message));

            MailjetResponse response = client.post(request);
            // Handle response
            if (response.getStatus() == 200) {
                logger.info("Email sent successfully!");
            } else {
                throw new ApplicationException("Failed to send email: " + response.getData());
            }
        } catch (Exception e) {
            throw new ApplicationException("Error sending email: " + e);
        }
    }

    @Override
    public boolean isValidEmail(String email) {
    try{
        String url = validationApiUrl+ validationApiKey + "&email=" + email;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            return responseBody.contains("\"status\":\"valid\"");
        }
    } catch (HttpClientErrorException e) {
        // Handle error, invalid email or other issues
        logger.error("Error while validating email: " + e.getMessage());
        throw new ApplicationException("Error while validating email: "+e.getMessage());
    }

        return false;

    }

}