package com.example.libbook.service;

import com.example.libbook.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(int id);
}