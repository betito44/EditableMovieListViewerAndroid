package com.example.project3;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class movie {
    public String description;
    public String director;
    public String length;
    public String picture;
    public String stars;
    public String rating;
    public String title;
    public String year;

    public movie(String title, String picture, String description, String length, String year,
                 String rating, String director, String stars){
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.length = length;
        this.year = year;
        this.rating = rating;
        this.director = director;
        this.stars = stars;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("picture", picture);
        result.put("description", description);
        result.put("length", length);
        result.put("year", year);
        result.put("rating", rating);
        result.put("director", director);
        result.put("stars", stars);

        return result;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getDirector() {
        return director;
    }

    public String getImage() {
        return picture;
    }

    public String getLength() {
        return length;
    }

    public String getName() {
        return title;
    }

    public String getStars() {
        return stars;
    }


    public String getYear() {
        return year;
    }
}