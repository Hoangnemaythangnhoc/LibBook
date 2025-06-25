package com.example.libbook.service.impl;

import com.example.libbook.service.EmailTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailTokenServiceImpl implements EmailTokenService {

    private final JavaMailSender javaMailSender;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final long EXPIRY_MINUTES = 10;
    // In-memory cache để lưu token
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    // Lớp nội bộ để lưu thông tin token
    private static class TokenInfo {
        String email;
        LocalDateTime expiryDate;

        TokenInfo(String email, LocalDateTime expiryDate) {
            this.email = email;
            this.expiryDate = expiryDate;
        }
    }

    @Autowired
    public EmailTokenServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String generateAndSendToken(String email) throws MessagingException {
        String token = String.valueOf(100000 + secureRandom.nextInt(900000));
        String subject = "Your Verification Token";
        String body = buildEmailContent(token);

        // Lưu token vào cache
        tokenStore.put(token, new TokenInfo(email, LocalDateTime.now().plusMinutes(EXPIRY_MINUTES)));

        // Gửi email
        sendHtmlEmail(email, subject, body);
        return token;
    }

    @Override
    public boolean isValidToken(String token, String email) {
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null || !tokenInfo.email.equals(email)) {
            return false;
        }

        if (tokenInfo.expiryDate.isBefore(LocalDateTime.now())) {
            tokenStore.remove(token); // Xóa token hết hạn
            return false;
        }

        // Xóa token sau khi xác thực thành công
        tokenStore.remove(token);
        return true;
    }

    private void sendHtmlEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        javaMailSender.send(message);
    }

    private String buildEmailContent(String token) {
        // Giữ nguyên phương thức buildEmailContent từ code của bạn
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; }" +
                ".container { padding: 20px; max-width: 500px; margin: auto; border: 1px solid #ddd; border-radius: 10px; }" +
                ".logo { width: 100px; }" +
                ".token { font-size: 24px; font-weight: bold; color: #d9534f; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #666; }" +
                ".social-icons img { width: 30px; margin: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<img style='width:30%;display:block;margin:20px auto' src='https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/16f28dbf-e93f-4f38-a289-c66aa8c4d49a/dj4uyw8-7654a3ba-f78a-44a8-9c9a-85cbe2d25c76.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzE2ZjI4ZGJmLWU5M2YtNGYzOC1hMjg5LWM2NmFhOGM0ZDQ5YVwvZGo0dXl3OC03NjU0YTNiYS1mNzhhLTQ0YTgtOWM5YS04NWNiZTJkMjVjNzYucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.3r7W87EGEmREblkZb8gFK0VF75wqyvfMAiHYLcuyUNA' class='logo' data-bit='iit'>" +
                "<img style='width:100%;display:block;margin:10px 0 0' src='https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/16f28dbf-e93f-4f38-a289-c66aa8c4d49a/dj4uz77-d5f74858-b68f-47d9-b4f5-81acf9803c00.png/v1/fill/w_1278,h_625,q_70,strp/arthubbanner_by_moudeus10_dj4uz77-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NjI2IiwicGF0aCI6IlwvZlwvMTZmMjhkYmYtZTkzZi00ZjM4LWEyODktYzY2YWE4YzRkNDlhXC9kajR1ejc3LWQ1Zjc0ODU4LWI2OGYtNDdkOS1iNGY1LTgxYWNmOTgwM2MwMC5wbmciLCJ3aWR0aCI6Ijw9MTI4MCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.cBIdfh0Hc0xVHnMXljj2gCW8iF8oL--brb1jl3RlKio' class='banner' data-bit='iit' tabindex='0'>" +
                "<h2>Your Verification Token</h2>" +
                "<p>Use the following code to verify your email:</p>" +
                "<p class='token'>" + token + "</p>" +
                "<p>This token is valid for 10 minutes.</p>" +
                "<div class='footer'>" +
                "<p>Follow us on:</p>" +
                "<div class='social-icons'>" +
                "<a href='https://facebook.com/namson03'><img src='https://upload.wikimedia.org/wikipedia/commons/c/cd/Facebook_logo_%28square%29.png' alt='Facebook'></a>" +
                "<a href='https://instagram.com/namson.10'><img src='https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png' alt='Instagram'></a>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredTokens() {
        System.out.println("Cleaning expired tokens. Current size: " + tokenStore.size());
        tokenStore.entrySet().removeIf(entry ->
                entry.getValue().expiryDate.isBefore(LocalDateTime.now()));
        System.out.println("After cleaning. Size: " + tokenStore.size());
    }
}