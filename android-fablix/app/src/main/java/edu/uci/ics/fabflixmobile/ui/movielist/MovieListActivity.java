package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.data.model.Star;
import edu.uci.ics.fabflixmobile.ui.main_page.MainPageActivity;
import edu.uci.ics.fabflixmobile.ui.single_movie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {
    ArrayList<Movie> movies;
    String searchText;
    TextView pageNumberView;
    Button nextBtn ;
    Button prevBtn;
    Integer pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);


        Intent intent = getIntent();
        final String responseMovies = intent.getStringExtra("responseMovies");
        searchText = intent.getStringExtra("searchText");

        System.out.println(responseMovies);



        pageNumberView = findViewById(R.id.pageNumber);

        nextBtn = findViewById(R.id.btnNext);
        prevBtn = findViewById(R.id.btnPrevious);


        setListView(responseMovies);
    }

    private void setListView(String responseMovies) {
        movies = parseMovieList(responseMovies);

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

        if (movies.size() < 10){
            nextBtn.setEnabled(false);
        }
        else{
            nextBtn.setEnabled(true);
        }
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



    @SuppressLint("SetTextI18n")
    public void loadNextPage(View view) {
        pageNumber = pageNumber + 1;
        pageNumberView.setText("Page " + pageNumber);
        if (pageNumber > 1){
            prevBtn.setEnabled(true);
        }


        search(pageNumber);


    }

    @SuppressLint("SetTextI18n")
    public void loadPreviousPage(View view) {
        pageNumber = pageNumber - 1;
        pageNumberView.setText("Page " + pageNumber);

        if (pageNumber == 1){
            prevBtn.setEnabled(false);
        }

        search(pageNumber);

    }


    @SuppressLint("SetTextI18n")
    public void search(Integer pageNumber) {
        // initialize the activity(page)/destination
        //call api to get search result and pass it to the next page

//        message.setText("Searching" + NetworkManager.baseURL + "/api/search");

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;


        String baseUrl = NetworkManager.baseURL + "/api/search";
        String title =  searchText;
        String year = "";
        String director = "";
        String star = "";
        int pageSize = 10;
        int sortOption = 7;

        @SuppressLint("DefaultLocale") String constructedUrl = String.format("%s?title=%s&year=%s&director=%s&star=%s&page_number=%d&page_size=%d&sort_option=%d",
                baseUrl, title, year, director, star, pageNumber, pageSize, sortOption);

        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                constructedUrl,
                response -> {
                    setListView(response);
                },
                error -> {
//                        message.setText("Fail to search with error: " + error.toString());
//                        message.setText("Searching" + NetworkManager.baseURL + "/api/search");

                }
        ){
        };

        queue.add(searchRequest);

    }
}