package com.example.libbook.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties
@lombok.Setter
@lombok.Getter
public class ChatMessage {
    private int MessageId;
    private int SenderId;
    private int ReceiverId;
    private LocalDateTime DateTime;
    private String MessageText;

    public ChatMessage(int messageId, int senderId, int receiverId, LocalDateTime dateTime, String messageText) {
        MessageId = messageId;
        SenderId = senderId;
        ReceiverId = receiverId;
        DateTime = dateTime;
        MessageText = messageText;
    }
    public ChatMessage() {
    }
}
