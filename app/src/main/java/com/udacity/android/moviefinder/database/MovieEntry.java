package com.udacity.android.moviefinder.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie")
public class MovieEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String releaseDate;
    private String overview;
    private String rating;
    private String imagePath;
    private String title;
    private String movieId;

    public MovieEntry(int id, String title, String overview, String imagePath, String rating, String movieId, String releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.imagePath = imagePath;
        this.rating = rating;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
    }

    @Ignore
    public MovieEntry(String title, String overview, String imagePath, String rating, String movieId, String releaseDate) {
        this.title = title;
        this.overview = overview;
        this.imagePath = imagePath;
        this.rating = rating;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
    }

    @Ignore
    public MovieEntry(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
