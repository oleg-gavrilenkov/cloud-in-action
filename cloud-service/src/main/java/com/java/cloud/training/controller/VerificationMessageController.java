package com.java.cloud.training.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationMessageController {

    @Value("${verification.message}")
    private String verificationMessage;

    @GetMapping("/verification-message")
    public ResponseEntity<String> getVerificationMessage() {
        return new ResponseEntity<>(verificationMessage, HttpStatus.OK);
    }
}
