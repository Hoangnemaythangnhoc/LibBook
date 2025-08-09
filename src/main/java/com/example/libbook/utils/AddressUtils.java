package com.example.libbook.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AddressUtils {
    private static final String API_URL = "https://provinces.open-api.vn/api/v1";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Load tất cả tỉnh/thành
    public static String loadProvinces(String provinceID) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/p/" + provinceID))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // In để kiểm tra phản hồi
            System.out.println("Province API Response: " + response.body());

            JsonNode provinceData = mapper.readTree(response.body());
            System.out.println("Province json: " + provinceData);
            return provinceData.get("name").asText(); // Lấy tên tỉnh
        } catch (IOException | InterruptedException e) {
            System.err.println("Không thể tải tỉnh thành: " + e.getMessage());
        }
        return null;
    }


    // Load danh sách quận/huyện theo mã tỉnh
    public static String getDistrictName(String provinceID, String districtID) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/p/" + provinceID + "?depth=2"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Debug phản hồi từ API
            System.out.println("Districts API Response: " + response.body());

            JsonNode province = mapper.readTree(response.body());
            JsonNode districts = province.get("districts");

            if (districts != null) {
                for (JsonNode district : districts) {
                    if (district.get("code").asText().equals(districtID)) {
                        return district.get("name").asText();
                    }
                }
            }

            return "Không tìm thấy quận/huyện với mã: " + districtID;
        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi khi lấy quận/huyện: " + e.getMessage());
            return null;
        }
    }


}
