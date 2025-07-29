package com.example.libbook.controller.Product;

import com.example.libbook.entity.Tag;
import com.example.libbook.repository.TagRepository;
import com.example.libbook.service.TagService;
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
    TagService tagService;

    @GetMapping
    public ResponseEntity<List<Tag>> getTags() {
        List<Tag> tags = tagService.getAllTags();
        if (tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tags);
    }

    @GetMapping("{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable int id) {
        Tag tag = tagService.getTagById(id);

        if (tag == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tag);
    }

    // Other methods...

    @GetMapping("product/{productId}")
    public ResponseEntity<List<Tag>> getTagsByProductId(@PathVariable Long productId) {
        List<Tag> tags = tagService.getTagsByProductId(productId);
        if (tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tags);
    }
}
