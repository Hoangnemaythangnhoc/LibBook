package com.example.libbook.controller.user;


import com.example.libbook.entity.ChatMessage;
import com.example.libbook.entity.Order;
import com.example.libbook.entity.User;
import com.example.libbook.service.ChatMessageService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/chat/")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;




    @GetMapping("/user")
    public ResponseEntity<List<ChatMessage>> getChatMessages(HttpSession session) {
        User _u = (User) session.getAttribute("USER");

        List<ChatMessage> messages = chatMessageService.getUsersChat(_u.getUserId());
        if (messages == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }


    @GetMapping("/related")
    public ResponseEntity<List<User>> getRelatedChatMessages(HttpSession session) {
        User _u = (User) session.getAttribute("USER");

        List<User> messages = chatMessageService.relatedUsersChat(_u.getUserId());
        if (messages == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
