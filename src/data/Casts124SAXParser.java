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
            if (currentElement.equalsIgnoreCase("f")) {
                movieId = value;
            } else if (currentElement.equalsIgnoreCase("a") && !"s a".equalsIgnoreCase(value)) {
                starName = value;
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

    public static void main(String[] args) {
        Casts124SAXParser parser = new Casts124SAXParser();
        parser.parseDocument();

        List<StarsInMovie> starsInMovies = parser.getStarsInMovies();

        DatabaseHandler databaseHandler = new DatabaseHandler();
        databaseHandler.insertStarsInMovies(starsInMovies);
    }
}
