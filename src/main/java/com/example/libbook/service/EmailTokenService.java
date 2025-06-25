package com.example.libbook.service;

public interface EmailTokenService {

    String generateAndSendToken(String email);
}