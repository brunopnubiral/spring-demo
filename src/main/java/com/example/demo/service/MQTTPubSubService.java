package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class MQTTPubSubService {

    @Autowired
    private AwsIotMqttConnectionBuilder mqttConnectionBuilder;

    public void publishMessage(String topic, String message) throws ExecutionException, InterruptedException {
        MqttClientConnection connection = mqttConnectionBuilder.build();

        try {
            CompletableFuture<Boolean> connected = connection.connect();
            boolean sessionPresent = connected.get();
            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            CompletableFuture<Integer> published = connection.publish(
                    new MqttMessage(topic, message.getBytes(StandardCharsets.UTF_8), QualityOfService.AT_LEAST_ONCE, false));
            published.get();
            System.out.println("Message published to topic: " + topic);

        } finally {
            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();
            connection.close();
        }
    }
}