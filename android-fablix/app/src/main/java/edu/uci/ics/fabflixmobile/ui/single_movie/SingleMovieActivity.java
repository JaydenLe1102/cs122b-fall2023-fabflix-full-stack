package edu.uci.ics.fabflixmobile.ui.single_movie;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.databinding.ActivitySingleMovieBinding;

public class SingleMovieActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);

        ActivitySingleMovieBinding binding = ActivitySingleMovieBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        final Button backButton = binding.btnGoBack;
        backButton.setOnClickListener(view -> goBack());
    }

    private void goBack() {
        onBackPressed();
    }

}
