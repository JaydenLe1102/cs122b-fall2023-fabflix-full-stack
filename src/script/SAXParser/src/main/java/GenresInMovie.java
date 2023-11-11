


public class GenresInMovie {
    private String genreName;
    private String movieId;

    public GenresInMovie() {

    }

    public GenresInMovie(String genreName, String movieId) {
        this.genreName = genreName;
        this.movieId = movieId;
    }

    // Getters and Setters for GenresInMovies fields
    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String toString() {
        return "GenresInMovie - Genre: " + getGenreName() + ", Movie ID: " + getMovieId();
    }
}
