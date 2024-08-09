package com.example.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class MQTTConfig {

    @Bean
    public AwsIotMqttConnectionBuilder mqttConnectionBuilder() throws IOException {
        ClassPathResource certResource = new ClassPathResource("device.pem.crt");
        ClassPathResource privateKeyResource = new ClassPathResource("private.pem.key");
        ClassPathResource caResource = new ClassPathResource("AmazonRootCA1.pem");

        Path certPath = Files.createTempFile("cert", ".pem");
        Path keyPath = Files.createTempFile("key", ".pem");
        Path caPath = Files.createTempFile("ca", ".pem");

        Files.copy(certResource.getInputStream(), certPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.copy(privateKeyResource.getInputStream(), keyPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.copy(caResource.getInputStream(), caPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(
                certPath.toString(), keyPath.toString());

        builder.withCertificateAuthorityFromPath(null, caPath.toString())
                .withClientId("924533059682")
                .withEndpoint("a2jsnrqfcpkakm-ats.iot.us-east-1.amazonaws.com")
                .withPort(8883)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);

        builder.withConnectionEventCallbacks(new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                System.out.println("Connection interrupted: " + errorCode);
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        });

        return builder;
    }
}