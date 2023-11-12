package data;

import java.util.*;
public class StarsInMovie {
    private String starName;
    private String movieId;

    public StarsInMovie() {

    }

    public StarsInMovie(String starName, String movieId) {
        this.starName = starName;
        this.movieId = movieId;
    }

    // Getters and Setters for StarsInMovies fields
    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String toString() {
        return "StarsInMovie - Star Name: " + getStarName() + ", Movie ID: " + getMovieId();
    }
}
