package com.example.libbook.controller.Product;

import com.example.libbook.entity.Tag;
import com.example.libbook.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
