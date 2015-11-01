package com.example.firdause.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Firdause on 10/27/15.
 */
public class Review {

    // Instance variables for Review Class
    private String id;
    private String author;
    private String content;

    public Review() {

    }

    public Review(JSONObject review) throws JSONException {
        this.id = review.getString("id");
        this.author = review.getString("author");
        this.content = review.getString("content");

    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
