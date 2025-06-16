package com.example.libbook.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUser {
    private String sub;
    private String name;
    private String given_name;
    private String email;
    private String picture;
    private String family_name;


    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGiven_name() { return given_name; }
    public void setGiven_name(String given_name) { this.given_name = given_name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
}
