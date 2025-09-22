package com.github.Danish811.Bajaj_API_Test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Your personal details
        String name = "Mohammad Danish Sheikh";
        String regNo = "0101CS221081";
        String email = "sheikhd811@gmail.com";

        // Determine if regNo last two digits are odd or even
        String lastTwoStr = regNo.substring(regNo.length() - 2);
        int lastTwo = Integer.parseInt(lastTwoStr);
        boolean isOdd = (lastTwo % 2 == 1);

        // Select the appropriate SQL query based on regNo
        String finalQuery;
        if (isOdd) {
            // Question 1 SQL (highest salary not on 1st of month)
            finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1;";
        } else {
            // Question 2 SQL (for even, but not needed here)
            finalQuery = "SELECT e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME, COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE e1 JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID LEFT JOIN EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e2.DOB > e1.DOB GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME ORDER BY e1.EMP_ID DESC;";
        }

        // Step 1: Send POST to generate webhook
        RestTemplate restTemplate = new RestTemplate();
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"name\": \"" + name + "\", \"regNo\": \"" + regNo + "\", \"email\": \"" + email + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(generateUrl, requestEntity, String.class);

        // Parse response for webhook and accessToken
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String webhookUrl = root.path("webhook").asText();
        String accessToken = root.path("accessToken").asText();

        // Step 2: Submit the solution to the webhook
        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.set("Authorization", "Bearer " + accessToken);

        String submitBody = "{\"finalQuery\": \"" + finalQuery.replace("\"", "\\\"") + "\"}";
        HttpEntity<String> submitEntity = new HttpEntity<>(submitBody, submitHeaders);

        restTemplate.postForEntity(webhookUrl, submitEntity, String.class);

        // Optional: Print confirmation (for local testing)
        System.out.println("Submitted SQL query for " + (isOdd ? "Question 1" : "Question 2") + " to webhook.");
    }
}