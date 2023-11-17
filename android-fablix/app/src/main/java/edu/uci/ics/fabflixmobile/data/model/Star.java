package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Star {
    private final String star_id;
    private final String name;

    public Star(String star_id, String name) {
        this.name = name;
        this.star_id = star_id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return star_id;
    }
}