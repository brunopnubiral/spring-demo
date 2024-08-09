package com.example.demo.controller;

import com.example.demo.service.MQTTPubSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class MQTTController {

    @Autowired
    private MQTTPubSubService mqttPubSubService;

    @PostMapping("/publish")
    public String publishMessage(@RequestParam String topic, @RequestParam String message) {
        try {
            mqttPubSubService.publishMessage(topic, message);
            return "Message sent successfully";
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return "Error sending message: " + e.getMessage();
        }
    }
}