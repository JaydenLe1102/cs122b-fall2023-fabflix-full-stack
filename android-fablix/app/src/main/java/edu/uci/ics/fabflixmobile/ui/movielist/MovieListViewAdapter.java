package edu.uci.ics.fabflixmobile.ui.movielist;

import android.text.TextUtils;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView year;
        TextView director;
        TextView stars;
        TextView genres;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
//            viewHolder.year = convertView.findViewById(R.id.year);
//            viewHolder.director = convertView.findViewById(R.id.director);
//            viewHolder.stars = convertView.findViewById(R.id.stars);
//            viewHolder.genres = convertView.findViewById(R.id.genres);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getTitle());
//        viewHolder.year.setText("(" + movie.getYear() + ")");
//        viewHolder.director.setText("Director: " + movie.getDirector());
//
//        // Assuming there is a method to convert an array to a comma-separated string
//        viewHolder.stars.setText("Stars: " + arrayToString(movie.getStars()));
//        viewHolder.genres.setText("Genres: " + arrayToString(movie.getGenres()));
        return convertView;
    }

    private String arrayToString(Object[] array) {
        return array != null ? TextUtils.join(", ", array) : "";
    }
}