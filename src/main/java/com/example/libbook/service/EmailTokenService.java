package com.example.libbook.service;

import jakarta.mail.MessagingException;

public interface EmailTokenService {

    String generateAndSendToken(String email) throws MessagingException;

    boolean isValidToken(String token, String email);
}
