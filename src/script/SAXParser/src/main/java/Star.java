;

import com.google.protobuf.NullValue;

import java.util.*;
public class Star {
    private String id;
    private String name;
    private int birthYear;

    public Star() {

    }

    public Star(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Star(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    // Getters and Setters for Star fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
