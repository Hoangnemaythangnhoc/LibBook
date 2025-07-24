package com.example.libbook.repository.impl;

import com.example.libbook.entity.ChatMessage;
import com.example.libbook.entity.User;
import com.example.libbook.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ChatMessage> getUsersChat(int userId) {
        String sql = "SELECT * FROM ChatMessage WHERE ReceiverId = ? OR SenderId = ?";
        return jdbcTemplate.query(sql, new Object[]{userId,userId}, new RowMapper<ChatMessage>() {
            @Override
            public ChatMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
                ChatMessage item = new ChatMessage();
                item.setMessageId(rs.getInt("MessageId"));
                item.setSenderId(rs.getInt("SenderId"));
                item.setReceiverId(rs.getInt("ReceiverId"));
                item.setDateTime(rs.getTimestamp("SentAt").toLocalDateTime());
                item.setMessageText(rs.getString("MessageText"));
                return item;
            }
        });
    }

    @Override
    public List<User> relatedUsersChat(int userId) {
        return jdbcTemplate.query(
                "EXEC GetUsersChattedWith @userId = ?",
                new Object[]{userId},
                (rs, rowNum) -> {
                    User user = new User();
                    user.setUserId(rs.getInt("UserId"));
                    user.setFirstName(rs.getString("FirstName"));
                    user.setLastName(rs.getString("LastName"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhoneNumber(rs.getString("Phonenumber"));
                    user.setProfilePicture(rs.getString("ProfilePicture"));
                    return user;
                }
        );
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        String sql = "INSERT INTO ChatMessage (SenderId, ReceiverId, MessageText) VALUES (?, ?, N'?')";
        jdbcTemplate.update(sql, chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getMessageText());
    }
}
