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
                "<img style='width:30%;display:block;margin:20px auto' src='https://res.cloudinary.com/dbz1uew0a/image/upload/v1751220681/black_text_logo_cskmy0.jpg' class='logo' data-bit='iit'>" +
                "<img style='width:100%;display:block;margin:10px 0 0' src='https://res.cloudinary.com/dbz1uew0a/image/upload/v1751220556/LibBookBanner_vp3qqx.png' class='banner' data-bit='iit' tabindex='0'>" +
                "<h2>Your Verification Token</h2>" +
                "<p>Use the following code to verify your email:</p>" +
                "<p class='token'>" + token + "</p>" +
                "<p>This token is valid for 10 minutes.</p>" +
                "<div class='footer'>" +
                "<p>Follow us on:</p>" +
                "<div class='social-icons'>" +
                "<a href=''><img src='https://upload.wikimedia.org/wikipedia/commons/c/cd/Facebook_logo_%28square%29.png' alt='Facebook'></a>" +
                "<a href=''><img src='https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png' alt='Instagram'></a>" +
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