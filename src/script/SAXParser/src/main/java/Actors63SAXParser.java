

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Actors63SAXParser extends DefaultHandler {
    private String stagename;
    private String dob;
    private List<Star> stars;
    private String currentElement;

    public Actors63SAXParser() {
        stars = new ArrayList<>();
    }

    public void parseDocument() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse("src/actors63.xml", this);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentElement = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length).trim();
        if (!value.isEmpty()) {
            if (currentElement.equalsIgnoreCase("stagename")) {
                stagename = value;
            } else if (currentElement.equalsIgnoreCase("dob")) {
                dob = value;
            }
        }
    }

    private Integer parseBirthYear(String dob) {
        if (dob == null || dob.isEmpty() || !isNumeric(dob)) {
            return null; // For null, empty, or non-integer values, return null
        } else {
            try {
                return Integer.parseInt(dob);
            } catch (NumberFormatException e) {
                return null; // For parsing errors, return null
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equalsIgnoreCase("actor")) {
            Integer dobValue = parseBirthYear(dob);
            Integer yearValue = (dobValue != null) ? dobValue : null;

            // Check if yearValue is null before accessing its intValue
            if (yearValue != null) {
                // Use yearValue as an integer if not null
                Star star = new Star(generateUniqueID(stars), stagename, yearValue);
                stars.add(star);
            } else {
                // If yearValue is null, add the Star with a null birth year
                Star star = new Star(generateUniqueID(stars), stagename);
                stars.add(star);
            }
        }
    }

    private boolean isNumeric(String str) {
        // Validate if a string is a number
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private String generateUniqueID(List<Star> stars) {
        String id;
        boolean uniqueId;
        do {
            id = generateRandomID();
            String finalId = id;
            uniqueId = stars.stream().noneMatch(s -> s.getId().equals(finalId));
        } while (!uniqueId);
        return id;
    }

    private String generateRandomID() {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 9; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Actors63SAXParser parser = new Actors63SAXParser();
        parser.parseDocument();

        List<Star> stars = parser.stars;

        DatabaseHandler databaseHandler = new DatabaseHandler();
        databaseHandler.insertStarsBatch(stars);
    }
}

