package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {

    private final String movie_id;
    private final String title;
    private final short year;
    private final String director;

    private final String[] genres;

    private final Star[] stars;

    public Movie(String title, String movieId, short year, String director, String[] genres, Star[] stars) {
        movie_id = movieId;
        this.title = title;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public short getYear() {
        return year;
    }

    public String getMovie_id(){return movie_id;}
    
    public String getDirector() {
        return director;
    }
    
    public String[] getGenres() {
        return genres;
    }
    
    public Star[] getStars() {
        return stars;
    }
    
}