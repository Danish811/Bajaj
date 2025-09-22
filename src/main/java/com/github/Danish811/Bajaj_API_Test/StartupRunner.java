package com.github.Danish811.Bajaj_API_Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Component
public class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.name}")
    private String name;

    @Value("${app.regNo}")
    private String regNo;

    @Value("${app.email}")
    private String email;

    @Override
    public void run(String... args) {
        try {

            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> generateRequest = Map.of(
                    "name", name,
                    "regNo", regNo,
                    "email", email
            );

            ResponseEntity<Map> generateResponse = restTemplate.postForEntity(generateUrl, generateRequest, Map.class);

            String webhookUrl = (String) generateResponse.getBody().get("webhook");
            String accessToken = (String) generateResponse.getBody().get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            if (webhookUrl == null || accessToken == null) {
                System.err.println("Failed to get webhook URL or access token.");
                return;
            }
            String finalQuery = getFinalQuery(regNo);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken); // <-- raw token, NO 'Bearer'

            Map<String, String> answer = Map.of("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(answer, headers);

            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity, String.class);
            System.out.println("Submission response: " + submitResponse.getBody());

        } catch (HttpClientErrorException.Unauthorized ex) {
            System.err.println("Error 401: Unauthorized. Check your access token or webhook URL.");
        } catch (HttpClientErrorException.BadRequest ex) {
            System.err.println("Error 400: Bad Request. Check your payload format.");
            System.err.println("Response body: " + ex.getResponseBodyAsString());
        } catch (HttpClientErrorException ex) {
            System.err.println("HTTP Error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getFinalQuery(String regNo) {
        int lastTwo = Integer.parseInt(regNo.substring(regNo.length() - 2));

        String finalQuery;
        if (lastTwo % 2 == 1) {
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
            finalQuery =
                    "SELECT d.DEPARTMENT_NAME, COUNT(e.EMP_ID) AS TOTAL_EMPLOYEES " +
                            "FROM DEPARTMENT d " +
                            "LEFT JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                            "GROUP BY d.DEPARTMENT_NAME " +
                            "HAVING COUNT(e.EMP_ID) > 5;";
        }
        return finalQuery;
    }
}