package com.example.firdause.popularmovies.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Firdause on 10/27/15.
 */
public class Trailer {

    // Instance variables for Trailer Class
    private String id;
    private String key;
    private String name;

    public Trailer() {

    }

    public Trailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");

        this.name = trailer.getString("name");
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
