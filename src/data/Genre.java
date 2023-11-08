package data;

import java.util.*;
public class Genre {
    private String name;

    public Genre() {

    }

    public Genre(String name) {
        this.name = name;
    }

    // Getter and Setter for Genre name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Genre: " + getName();
    }
}
