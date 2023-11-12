package data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Casts124SAXParser extends DefaultHandler {
    private String currentElement;
    private List<StarsInMovie> starsInMovies;
    private String movieId;
    private String starName;
    public int insertedStarsInMoviesCount = 0;
    public int inconsistentValuesCount = 0;
    public int duplicateStarsInMoviesCount = 0;


    public Casts124SAXParser() {
        starsInMovies = new ArrayList<>();
    }

    public void parseDocument() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse("src/casts124.xml", this);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
        if (qName.equalsIgnoreCase("m")) {
            movieId = "";
            starName = "";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length).trim();
        if (!value.isEmpty()) {
            try {
                if (currentElement.equalsIgnoreCase("f")) {
                    movieId = value;
                } else if (currentElement.equalsIgnoreCase("a") && !"s a".equalsIgnoreCase(value)) {
                    starName = value;
                }
            } catch (Exception e) {
                // Log and handle any inconsistencies in data
                inconsistentValuesCount++;
                // Handle inconsistencies as NULL or skip, based on specific context
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("m")) {
            if (!starName.isEmpty()) {
                StarsInMovie starsInMovie = new StarsInMovie();
                starsInMovie.setMovieId(movieId);
                starsInMovie.setStarName(starName);
                starsInMovies.add(starsInMovie);
            }
        }
    }

    public List<StarsInMovie> getStarsInMovies() {
        return starsInMovies;
    }

    public void printCountSummary() {
        System.out.println("Casts Summary:");
        System.out.println("1. Stars in Movies Inserted: " + insertedStarsInMoviesCount);
        System.out.println("4. Inconsistent Values (Not Inserted): " + inconsistentValuesCount);
        System.out.println("5. Duplicate Stars In Movies: " + duplicateStarsInMoviesCount);
    }
}
