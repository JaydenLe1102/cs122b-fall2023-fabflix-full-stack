package edu.uci.ics.fabflixmobile.ui.single_movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;

import java.util.Objects;

public class SingleMovieActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        Intent intent = getIntent();
        final String singleMovieResponse = intent.getStringExtra("singleMovieResponse");

        Movie movie = parseMovie(singleMovieResponse);
        if (movie != null) {
            updateUI(movie);
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }

    private Movie parseMovie(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            String movieId = jsonObject.getString("movie_id");
            String movieTitle = jsonObject.getString("movie_title");
            short movieYear = Short.parseShort(jsonObject.getString("movie_year"));
            String movieDirector = jsonObject.getString("movie_director");
            String movieRating = jsonObject.getString("movie_rating");

            // Parse genres
            JSONArray genresArray = jsonObject.getJSONArray("genres");
            String[] genres = new String[genresArray.length()];
            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genreObject = genresArray.getJSONObject(i);
                genres[i] = genreObject.getString("genre_name");
            }

            // Parse stars
            JSONArray starsArray = jsonObject.getJSONArray("stars");
            Star[] stars = new Star[starsArray.length()];
            for (int i = 0; i < starsArray.length(); i++) {
                JSONObject starObject = starsArray.getJSONObject(i);
                String starId = starObject.getString("star_id");
                String starName = starObject.getString("star_name");
                String starDob = starObject.isNull("star_dob") ? null : starObject.getString("star_dob");
                int movieCount = Integer.parseInt(starObject.getString("movie_count"));

                stars[i] = new Star(starId, starName, starDob, movieCount);
            }

            // Create and return the Movie object
            return new Movie(movieTitle, movieId, movieYear, movieDirector, genres, stars);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void updateUI(Movie movie) {
        TextView titleTextView = findViewById(R.id.movieTitle);
        TextView yearTextView = findViewById(R.id.movieYear);
        TextView directorTextView = findViewById(R.id.movieDirector);
        TextView genresTextView = findViewById(R.id.movieGenres);
        TableLayout starTable = findViewById(R.id.starTable);

        // Set values to TextViews
        titleTextView.setText(movie.getTitle());
        yearTextView.setText("Year: " + movie.getYear());
        directorTextView.setText("Director: " + movie.getDirector());
        genresTextView.setText("Genres: " + String.join(", ", movie.getGenres()));

        // Populate starTable dynamically

        View lineView = new View(this);
        lineView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        lineView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
        starTable.addView(lineView);

        for (Star star : movie.getStars()) {
            TableRow row = new TableRow(this);

            row.addView(createTextView(star.getName()));

            row.addView(Objects.equals(star.getDob(), "")? createTextView("N/A") : createTextView(star.getDob()));

            row.addView(createTextView(String.valueOf(star.getMovieCount())));

            starTable.addView(row);

            // Add a line (separator) after each row

            View lineView2 = new View(this);
            lineView2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            lineView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
            starTable.addView(lineView2);
        }
    }

    private TextView createTextView(String text){
        TextView tv = new TextView(this);
        tv.setText(text);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                0,  // Width 0dp
                TableRow.LayoutParams.WRAP_CONTENT,  // Height WRAP_CONTENT
                1.0f  // Weight 1
        );

        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

}
