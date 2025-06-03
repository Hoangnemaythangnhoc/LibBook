package com.example.libbook.controller.DynamicHtml;


import com.example.libbook.dto.GoogleUser;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.GoogleConstants;
import com.example.libbook.entity.User;
import com.example.libbook.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Controller
public class DynamicHtml {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/fragment/footer")
    public String getFooterFragment() {
        return "components/footer :: footer";
    }

    @GetMapping("/fragment/nav")
    public String getNavFragment() {
        return "components/navbar :: navbar";
    }

    @GetMapping("/oauth2/callback")
    public String handleGoogleCallback(@RequestParam("code") String code, HttpSession session) throws IOException {
        String accessToken = getAccessToken(code);
        GoogleUser user = getUserInfo(accessToken);
        UserDTO userDTO = userRepository.getUserByEmail(user.getEmail());
        if (userDTO != null) {
            session.setAttribute("USER", userDTO);
            return "redirect:/home";
        }else {
            UserDTO _u = new UserDTO();
            _u.setEmail(user.getEmail());
            _u.setUserName(user.getEmail().split("@")[0]);
            _u.setPassword("");
            userRepository.createAccount(_u);
            session.setAttribute("USER", _u);
            return "redirect:/home";
        }
    }

    private String getAccessToken(String code) throws IOException {
        URL url = new URL(GoogleConstants.TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String params = "code=" + URLEncoder.encode(code, "UTF-8") +
                "&client_id=" + URLEncoder.encode(GoogleConstants.CLIENT_ID, "UTF-8") +
                "&client_secret=" + URLEncoder.encode(GoogleConstants.CLIENT_SECRET, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(GoogleConstants.REDIRECT_URI, "UTF-8") +
                "&grant_type=authorization_code";

        OutputStream os = conn.getOutputStream();
        os.write(params.getBytes());
        os.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // Parse JSON
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.toString()).get("access_token").asText();
    }

    private GoogleUser getUserInfo(String accessToken) throws IOException {
        URL url = new URL(GoogleConstants.USER_INFO_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, GoogleUser.class);
    }
}
