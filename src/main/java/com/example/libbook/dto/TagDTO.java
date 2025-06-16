package com.example.libbook.dto;

public class TagDTO {
    private int tagId;
    private String tagName;

    public TagDTO(int tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public  TagDTO(){

    }
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
