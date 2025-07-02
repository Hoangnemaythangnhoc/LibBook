package com.example.libbook.controller.Product;


import com.example.libbook.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/summarize")
    public ResponseEntity<Map<String, Object>> summarizeBook(@RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tiêu đề sách không được để trống"));
            }

            String prompt = String.format(
                    "Tóm tắt ngắn gọn nội dung quyển sách \"%s\" bằng tiếng Việt, tối đa 150 từ, tập trung vào ý chính và thông điệp cốt lõi.",
                    title
            );

            String summary = openAiService.getBookSummary(prompt);

            Map<String, Object> response = new HashMap<>();
            response.put("title", title);
            response.put("summary", summary);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
}