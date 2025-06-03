package com.example.libbook.entity;

public class Tag {
    private int tagId;
    private String tagName;

    public Tag() {}

    public Tag(int tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public int getTagId() { return tagId; }
    public void setTagId(int tagID) { this.tagId = tagID; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    @Override
    public String toString() {
        return "Tag{" + "tagId=" + tagId + ", tagName='" + tagName + "'}";
    }
}
