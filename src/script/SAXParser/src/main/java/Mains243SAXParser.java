;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mains243SAXParser extends DefaultHandler {
    private String filmId;
    private String filmTitle;
    private String year;
    private List<String> categories;
    private List<Movie> movies;
    private Map<String, Genre> genresMap;
    private List<GenresInMovie> genresInMovies;
    private String currentElement;
    private String directorName;

    // Counters
    public int insertedMoviesCount = 0;
    public int insertedGenresCount = 0;
    public int insertedGenresInMoviesCount = 0;
    public int inconsistentValuesCount = 0;
    public int moviesWithNullAttributesCount = 0;
    public int moviesWithInvalidIdCount = 0;

    public Mains243SAXParser() {
        movies = new ArrayList<>();
        genresMap = new HashMap<>();
        genresInMovies = new ArrayList<>();
    }

    public void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("src/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("directorfilms")) {
            directorName = "";
        } else if (qName.equalsIgnoreCase("director")) {
            directorName = "";
        } else if (qName.equalsIgnoreCase("film")) {
            filmId = "";
            filmTitle = "";
            year = "";
            categories = new ArrayList<>();
        }
        currentElement = qName;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (!value.isEmpty()) {
            try {
                if (currentElement.equalsIgnoreCase("fid")) {
                    filmId = value;
                } else if (currentElement.equalsIgnoreCase("t")) {
                    filmTitle = value;
                } else if (currentElement.equalsIgnoreCase("year")) {
                    if (isNumeric(value)) {
                        year = value;
                    } else {
                        inconsistentValuesCount++;
                        // Log the error for inconsistent year data
                        // Handle the inconsistent year by setting it to NULL or as per your application's logic
                        year = null;
                    }
                } else if (currentElement.equalsIgnoreCase("cat")) {
                    categories.add(value);
                } else if (currentElement.equalsIgnoreCase("dirname")) {
                    directorName = value;
                }
            } catch (NumberFormatException e) {
                inconsistentValuesCount++;
                // Log and handle NumberFormatException for non-numeric values
                // Handle the parsing error as NULL or skip, based on specific context
            }
        }
    }

    private boolean isNumeric(String str) {
        // Validate if a string is a number
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("film")) {
            Integer parsedYear = parseYear(year);
            int yearValue = (parsedYear != null) ? parsedYear.intValue() : 0; // Defaulting to 0 if null

            Movie movie = new Movie(parseFilmId(filmId), parseFilmTitle(filmTitle), yearValue, parseDirectorName(directorName));
            movies.add(movie);

            // Handle genres and genres_in_movies
            for (String category : categories) {
                Genre genre = genresMap.get(category);
                if (genre == null) {
                    genre = new Genre(category);
                    genresMap.put(category, genre);
                }
                GenresInMovie genresInMovie = new GenresInMovie(category, filmId);
                genresInMovies.add(genresInMovie);
            }
        }
    }

    private Integer parseYear(String year) {
        if (year != null && year.matches("\\d+")) {
            try {
                return Integer.parseInt(year);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private String parseFilmId(String filmId) {
        if (filmId == null || filmId.isEmpty() || filmId.equals("NULL")) {
            return null; // or handle it as per your application's logic
        }
        return filmId;
    }

    private String parseFilmTitle(String filmTitle) {
        if (filmTitle == null || filmTitle.isEmpty() || filmTitle.equals("NULL")) {
            return null; // or handle it as per your application's logic
        }
        return filmTitle;
    }

    private String parseDirectorName(String directorName) {
        if (directorName == null || directorName.isEmpty() || directorName.equals("NULL")) {
            return null; // or handle it as per your application's logic
        }
        return directorName;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Genre> getGenres() {
        return new ArrayList<>(genresMap.values());
    }

    public List<GenresInMovie> getGenresInMovies() {
        return genresInMovies;
    }

    public void printCountSummary() {
        System.out.println("Mains Summary:");
        System.out.println("1. Movies Inserted: " + insertedMoviesCount);
        System.out.println("2. Genres Inserted: " + insertedGenresCount);
        System.out.println("3. Genres In Movies Inserted: " + insertedGenresInMoviesCount);
        System.out.println("4. Inconsistent Values (Not Inserted): " + inconsistentValuesCount);
        System.out.println("5. Movies with Null Attributes: " + moviesWithNullAttributesCount);
        System.out.println("6. Movies with Invalid ID: " + moviesWithInvalidIdCount);
    }
}