package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import edu.uci.ics.fabflixmobile.ui.single_movie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);


        Intent intent = getIntent();
        final String responseMovies = intent.getStringExtra("responseMovies");

        System.out.println(responseMovies);

        final ArrayList<Movie> movies = parseMovieList(responseMovies);

        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);

        ListView listView = findViewById(R.id.list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            // activate the list page.


            startActivity(SingleMoviePage);
        });
    }

    public void goBack(View view) {
        onBackPressed();
    }

    private ArrayList<Movie> parseMovieList(String jsonResponse) {
        ArrayList<Movie> movieList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movieObject = jsonArray.getJSONObject(i);

                String movieId = movieObject.getString("movie_id");
                String title = movieObject.getString("title");
                short year = Short.parseShort(movieObject.getString("year"));
                String director = movieObject.getString("director");

                JSONArray genresArray = movieObject.getJSONArray("genres");
                String[] genres = new String[genresArray.length()];
                for (int j = 0; j < genresArray.length(); j++) {
                    genres[j] = genresArray.getString(j);
                }

                JSONArray starsArray = movieObject.getJSONArray("stars");
                Star[] stars = new Star[starsArray.length()];
                for (int j = 0; j < starsArray.length(); j++) {
                    JSONObject starObject = starsArray.getJSONObject(j);
                    String starId = starObject.getString("star_id");
                    String starName = starObject.getString("name");
                    stars[j] = new Star(starId, starName);
                }

                Movie movie = new Movie(title, movieId, year, director, genres, stars);
                movieList.add(movie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieList;
    }
}