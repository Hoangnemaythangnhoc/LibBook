package com.example.libbook.repository;


import com.example.libbook.entity.Tag;

import java.util.ArrayList;

public interface TagRepository  {
    ArrayList<Tag> getAllTags();
    Tag getTagById(int id);

}

