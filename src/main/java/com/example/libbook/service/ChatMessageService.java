package com.example.libbook.service;

import com.example.libbook.entity.ChatMessage;
import com.example.libbook.entity.User;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> getUsersChat(int userId);
    List<User> relatedUsersChat(int userId);
    void sendChatMessage(ChatMessage chatMessage);
}
