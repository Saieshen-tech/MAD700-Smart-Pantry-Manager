package com.sai.smartpantry;

public class Recipe {
    private long id;
    private String title;

    public Recipe(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
}