package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Star {
    private final String star_id;
    private final String name;

    private final String dob;
    private final Integer movieCount;

    public Star(String star_id, String name) {
        this.name = name;
        this.star_id = star_id;
        this.dob = "";
        this.movieCount = 0;
    }

    public Star(String star_id, String name, String dob, Integer movieCount) {
        this.name = name;
        this.star_id = star_id;
        if (dob == null){
            this.dob = "";
        }
        else{
            this.dob = dob;
        }
        this.movieCount = movieCount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return star_id;
    }

    public String getDob() { return dob;}
    public Integer getMovieCount() {return movieCount;}
}