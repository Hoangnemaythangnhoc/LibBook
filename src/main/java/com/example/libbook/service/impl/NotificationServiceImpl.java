package com.example.libbook.service.impl;

import com.example.libbook.entity.Product;
import com.example.libbook.service.NotificationService;
import com.example.libbook.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendNewProductNotification(Product product) {
        List<String> emails = userService.getSubscribedEmails();
        if (emails.isEmpty()) {
            logger.info("Không có email nào đăng ký nhận thông báo.");
            return;
        }

        logger.info("Gửi thông báo sản phẩm mới tới {} email", emails.size());
        String subject = "Sản Phẩm Mới: " + (product.getProductName() != null ? product.getProductName() : "Sản phẩm mới");
        String body = buildEmailContent(product);

        for (String email : emails) {
            try {
                sendHtmlEmail(email, subject, body);
                logger.info("Đã gửi email thông báo tới {}", email);
            } catch (MessagingException e) {
                logger.error("Lỗi gửi email tới {}: {}", email, e.getMessage(), e);
            }
        }
    }

    private void sendHtmlEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);
        javaMailSender.send(message);
    }

    private String buildEmailContent(Product product) {
        String productid = product.getProductId() != null ? product.getProductId().toString() : "0";
        String productName = product.getProductName() != null ? product.getProductName() : "Sản phẩm mới";
        String description = product.getDescription() != null ? product.getDescription() : "Không có mô tả";
        String price = product.getPrice() != 0.0 ? String.format("%.2f", product.getPrice()) : "0.00";
        String imageUrl = product.getImageFile() != null ? product.getImageFile() : "https://res.cloudinary.com/dbz1uew0a/image/upload/v1751220775/Copilot_20250630_003020_wthlak.png";

        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; }" +
                ".container { padding: 20px; max-width: 500px; margin: auto; border: 1px solid #ddd; border-radius: 10px; }" +
                ".logo { width: 100px; }" +
                ".product-image { width: 100%; max-width: 200px; margin: 10px auto; }" +
                ".product-name { font-size: 24px; font-weight: bold; color: #d9534f; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #666; }" +
                ".social-icons img { width: 30px; margin: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<img src='https://res.cloudinary.com/dbz1uew0a/image/upload/v1751220681/black_text_logo_cskmy0.jpg' class='logo' alt='Logo'>" +
                "<h2>Thông Báo Sản Phẩm Mới</h2>" +
                "<p>Chúng tôi vừa thêm một sản phẩm mới vào cửa hàng:</p>" +
                "<img src='" + imageUrl + "' class='product-image' alt='Product Image'>" +
                "<p class='product-name'>" + productName + "</p>" +
                "<p><b>Mô tả:</b> " + description + "</p>" +
                "<p><b>Giá:</b> $" + price + "</p>" +
                "<p>Hãy xem ngay trên <a href='http://localhost:8080/product/"+ productid +"'>website của chúng tôi</a>!</p>" +
                "<div class='footer'>" +
                "<p>Follow us on:</p>" +
                "<div class='social-icons'>" +
                "<a href=''><img src='https://upload.wikimedia.org/wikipedia/commons/c/cd/Facebook_logo_%28square%29.png' alt='Facebook'></a>" +
                "<a href=''><img src='https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png' alt='Instagram'></a>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}