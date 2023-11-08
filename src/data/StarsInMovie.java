package data;

import java.util.*;
public class StarsInMovie {
    private String starId;
    private String movieId;

    public StarsInMovie() {

    }

    public StarsInMovie(String starId, String movieId) {
        this.starId = starId;
        this.movieId = movieId;
    }

    // Getters and Setters for StarsInMovies fields
    public String getStarId() {
        return starId;
    }

    public void setStarId(String starId) {
        this.starId = starId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String toString() {
        return "StarsInMovie - Star ID: " + getStarId() + ", Movie ID: " + getMovieId();
    }
}
