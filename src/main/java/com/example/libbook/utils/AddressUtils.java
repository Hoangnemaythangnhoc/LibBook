package com.example.libbook.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AddressUtils {
    private static final String API_URL = "https://provinces.open-api.vn/api";
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
            JsonNode provincesData = mapper.readTree(response.body());
            String provinceName = provincesData.get("name").asText();
            return provinceName;
        } catch (IOException | InterruptedException e) {
            System.err.println("Không thể tải danh sách tỉnh thành: " + e.getMessage());
        }
        return null;
    }

    // Load danh sách quận/huyện theo mã tỉnh
    public static String getDistrictName(String provinceID, String districtID) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://provinces.open-api.vn/api/p/" + provinceID + "?depth=2"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode province = mapper.readTree(response.body());
            JsonNode districts = province.get("districts");

            for (JsonNode district : districts) {
                if (district.get("code").asText().equals(districtID)) {
                    return district.get("name").asText();
                }
            }

            // Nếu không tìm thấy
            return "Không tìm thấy quận/huyện với mã: " + districtID;
        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi khi lấy tỉnh: " + e.getMessage());
            return null;
        }
    }


}
