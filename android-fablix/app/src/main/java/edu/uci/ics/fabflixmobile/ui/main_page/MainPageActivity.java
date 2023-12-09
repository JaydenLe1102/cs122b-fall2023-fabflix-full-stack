package edu.uci.ics.fabflixmobile.ui.main_page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainPageBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;

import static edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding.*;


public class MainPageActivity extends AppCompatActivity {
    private EditText searhBox;
    private TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ActivityMainPageBinding binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        System.out.print("Hello World");

        searhBox = binding.searchBox;
        message = binding.message;

        final Button searchButton = binding.search;


        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());

        searhBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    search();
                    return true;
                }
                return false;
            }

        });

    }


    @SuppressLint("SetTextI18n")
    public void search() {
        // initialize the activity(page)/destination
        //call api to get search result and pass it to the next page

//        message.setText("Searching" + NetworkManager.baseURL + "/api/search");

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//full-search?movie_query=s%20lov

        String baseUrl = NetworkManager.baseURL + "/api/full-search";
        String movie_query = searhBox.getText().toString();
        int pageNumber = 1;
        int pageSize = 10;
        int sortOption = 7;

        String constructedUrl = String.format("%s?movie_query=%s&page_number=%d&page_size=%d&sort_option=%d",
                baseUrl, movie_query, pageNumber, pageSize, sortOption);

        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                constructedUrl,
                response -> {


                    Intent MovieListPage = new Intent(MainPageActivity.this, MovieListActivity.class);


                    MovieListPage.putExtra("responseMovies", response);
                    MovieListPage.putExtra("searchText", searhBox.getText().toString());

                    // activate the list page.


                    startActivity(MovieListPage);
                    message.setText("");


                },
                error -> {
//                        message.setText("Fail to search with error: " + error.toString());
//                        message.setText("Searching" + NetworkManager.baseURL + "/api/search");

                }
        ) {
        };

        queue.add(searchRequest);

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
