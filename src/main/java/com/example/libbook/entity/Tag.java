package com.example.libbook.entity;

public class Tag {
    private Long tagId;
    private String tagName;

    public Tag() {}

    public Tag(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagID) { this.tagId = tagID; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    @Override
    public String toString() {
        return "Tag{" + "tagId=" + tagId + ", tagName='" + tagName + "'}";
    }
}