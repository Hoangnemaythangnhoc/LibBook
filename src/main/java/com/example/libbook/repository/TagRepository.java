package com.example.libbook.repository;


import com.example.libbook.entity.Tag;

import java.util.ArrayList;
import java.util.List;

public interface TagRepository  {
    ArrayList<Tag> getAllTags();
    Tag getTagById(int id);
    List<Long> getTagByTagName(String tags);
    List<Tag> getTagsByProductId(Long productId);
}

