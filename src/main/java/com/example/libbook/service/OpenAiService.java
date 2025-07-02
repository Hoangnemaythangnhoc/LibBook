package com.example.libbook.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class OpenAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String getBookSummary(String prompt) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API key không được cấu hình");
            }

            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            content.put("role", "user");
            content.put("parts", new JSONArray().put(new JSONObject().put("text", prompt)));
            contents.put(content);
            requestBody.put("contents", contents);

            String body = requestBody.toString();
            System.out.println("Request body: " + body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("API Error Response: " + response.body());
                return "Lỗi từ Gemini API: " + response.statusCode();
            }

            JSONObject json = new JSONObject(response.body());
            JSONArray candidates = json.getJSONArray("candidates");
            return candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
        
    }

}