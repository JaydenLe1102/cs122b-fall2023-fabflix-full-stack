package edu.uci.ics.fabflixmobile.ui.main_page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainPageBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import static edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding.*;


public class MainPageActivity extends AppCompatActivity {
    private EditText searhBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ActivityMainPageBinding binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        System.out.print("Hello World");

        searhBox = binding.searchBox;
        final Button searchButton = binding.search;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        // initialize the activity(page)/destination
        //call api to get search result and pass it to the next page


        Intent MovieListPage = new Intent(MainPageActivity.this, MovieListActivity.class);
        // activate the list page.


        startActivity(MovieListPage);
    }

}
