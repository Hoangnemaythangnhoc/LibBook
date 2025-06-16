package com.example.libbook.controller.Product;

import com.example.libbook.entity.Tag;
import com.example.libbook.repository.TagRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/Tag/")
public class TagController {

    @Autowired
    TagRepository tagRepository;

    @GetMapping
    public ResponseEntity<List<Tag>> getTags() {
        List<Tag> tags = tagRepository.getAllTags();
        if (tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tags);
    }

    @GetMapping("{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable int id) {
        Tag tag = tagRepository.getTagById(id);

        if (tag == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tag);
    }

    // Other methods...
}
