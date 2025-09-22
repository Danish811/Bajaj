package com.github.Danish811.Bajaj_API_Test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 🔹 Your personal details
        String name = "Mohammad Danish Sheikh";
        String regNo = "0101CS221081";   // Odd → Question 1
        String email = "sheikhd811@gmail.com";

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // 🔹 Step 1: Generate webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format(
                "{\"name\":\"%s\",\"regNo\":\"%s\",\"email\":\"%s\"}", name, regNo, email);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(generateUrl, requestEntity, String.class);

        JsonNode root = mapper.readTree(response.getBody());
        String webhookUrl = root.path("webhook").asText();
        String accessToken = root.path("accessToken").asText();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // 🔹 Step 2: Pick SQL query based on regNo last 2 digits
        String lastTwoStr = regNo.substring(regNo.length() - 2);
        int lastTwo = Integer.parseInt(lastTwoStr);

        String finalQuery;
        if (lastTwo % 2 == 1) {
            // Question 1: Highest salary not on 1st of month
            finalQuery =
                    "SELECT p.AMOUNT AS SALARY, " +
                            "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                            "d.DEPARTMENT_NAME " +
                            "FROM PAYMENTS p " +
                            "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                            "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                            "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                            "ORDER BY p.AMOUNT DESC LIMIT 1;";
        } else {
            // Question 2: Replace with actual SQL from Google Drive link
            finalQuery =
                    "SELECT d.DEPARTMENT_NAME, COUNT(e.EMP_ID) AS TOTAL_EMPLOYEES " +
                            "FROM DEPARTMENT d " +
                            "LEFT JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                            "GROUP BY d.DEPARTMENT_NAME " +
                            "HAVING COUNT(e.EMP_ID) > 5;";
        }

        // 🔹 Step 3: Submit solution
        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.setBearerAuth(accessToken);

        String submitBody = String.format("{\"finalQuery\":\"%s\"}", finalQuery.replace("\"", "\\\""));
        HttpEntity<String> submitEntity = new HttpEntity<>(submitBody, submitHeaders);

        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, submitEntity, String.class);

        System.out.println("Submission Status: " + submitResponse.getStatusCode());
        System.out.println("Response: " + submitResponse.getBody());
    }
}
