package com.monocept.chatbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


import lombok.Getter;

@Slf4j
public class RdsConnection {

    @Getter
    private Properties dbProperties = new Properties();

    public void createRdsConnection() {
        String secretARN = "arn:aws:secretsmanager:ap-south-1:025603691351:secret:superapp-prod-lead-u53r-secret-43HenM";

        try (SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder().region(Region.AP_SOUTH_1).build()) {
            GetSecretValueRequest secretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretARN)
                    .build();

            GetSecretValueResponse secretValueResponse = secretsManagerClient.getSecretValue(secretValueRequest);
            Map<String, String> mapping = new ObjectMapper().readValue(secretValueResponse.secretString(), HashMap.class);
            dbProperties.put("spring.datasource.username", mapping.get("Username"));
            dbProperties.put("spring.datasource.password", mapping.get("Password"));

            log.info("RDS connection properties loaded successfully.");
        } catch (Exception e) {
            log.error("Error fetching RDS credentials from Secrets Manager:", e);
        }
    }
}

