package com.example.libbook.service.impl;

import com.example.libbook.entity.Tag;
import com.example.libbook.repository.TagRepository;
import com.example.libbook.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;


}
