package data;

import java.util.*;
public class Star {
    private int id;
    private String name;
    private int birthYear;

    public Star() {

    }

    public Star(int id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    // Getters and Setters for Star fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String toString() {
        return "Star Details - Id: " + getId() + ", Name: " + getName() +
                ", Birth Year: " + getBirthYear();
    }
}
