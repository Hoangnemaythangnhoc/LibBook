package com.example.libbook.service.impl;


import com.example.libbook.entity.ChatMessage;
import com.example.libbook.entity.User;
import com.example.libbook.repository.ChatMessageRepository;
import com.example.libbook.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatMessage> getUsersChat(int userId) {
        return chatMessageRepository.getUsersChat(userId);
    }

    @Override
    public List<User> relatedUsersChat(int userId) {
        return chatMessageRepository.relatedUsersChat(userId);
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.sendChatMessage(chatMessage);
    }
}
