package com.udacity.android.moviefinder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.moviefinder.data.MovieFinderPreferences;
import com.udacity.android.moviefinder.database.AppDatabase;
import com.udacity.android.moviefinder.database.MovieEntry;
import com.udacity.android.moviefinder.utilities.MovieJsonUtils;
import com.udacity.android.moviefinder.utilities.NetworkUtils;
import com.udacity.android.moviefinder.MovieAdapter.MovieAdapterOnClickHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mErrorMessageDisplay;
    private ProgressBar pbLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private AppDatabase database;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = AppDatabase.getInstance(getApplicationContext());

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        ItemView mostPopular = (ItemView) findViewById(R.id.action_most_popular);
        ItemView highestRated = (ItemView) findViewById(R.id.action_highest_rated);


        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(movieAdapter);

        loadMovieList(MovieFinderPreferences.getPreferredNowPlaying(this));
    }

    private void loadFavoriteMovies() {
        showMovieListView();
        final LiveData<List<MovieEntry>> movieList = database.movieDAO().loadAllMovies();
        movieList.observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                movieAdapter.setMovieList(movieEntries);
            }
        });
    }

    private void loadMovieList(String apiName) {
        showMovieListView();
        String language = MovieFinderPreferences.getPreferredLanguage(this);
        String apiKey = MovieFinderPreferences.getPreferredKey(this);
        new FetchMovieListTask().execute(language, apiKey, apiName);
    }

    private void showMovieListView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_most_popular) {
            Toast.makeText(this, "Most Popular Movies", Toast.LENGTH_SHORT).show();
            loadMovieList(MovieFinderPreferences.getPreferredPopularApi(this));
            return true;
        } else if(itemThatWasClickedId == R.id.action_highest_rated) {
            Toast.makeText(this, "Highest Rated Movies", Toast.LENGTH_SHORT).show();
            loadMovieList(MovieFinderPreferences.getPreferredTopRated(this));
            return true;
        } else if(itemThatWasClickedId == R.id.action_favorite) {
            Toast.makeText(this, "Favorite Movies", Toast.LENGTH_SHORT).show();
            loadFavoriteMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MovieEntry selectedMovie) {
        Toast.makeText(this, "Movie: " + selectedMovie.getTitle(), Toast.LENGTH_SHORT).show();
        Context context = this;
        Intent detailActivityIntent = new Intent(context, MovieDetailActivity.class);
        detailActivityIntent.putExtra("title", selectedMovie.getTitle());
        detailActivityIntent.putExtra("overview", selectedMovie.getOverview());
        detailActivityIntent.putExtra("date", selectedMovie.getReleaseDate());
        detailActivityIntent.putExtra("image", selectedMovie.getImagePath());
        detailActivityIntent.putExtra("rating", selectedMovie.getRating());
        detailActivityIntent.putExtra("movieId", selectedMovie.getMovieId());
        startActivity(detailActivityIntent);
    }

    class FetchMovieListTask extends AsyncTask<String, Void, List<MovieEntry>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieEntry> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String language = params[0];
            String apiKey = params[1];
            String apiName = params[2];
            URL movieRequestUrl = NetworkUtils.buildDefaultUrl(language, apiKey, apiName);
            List<MovieEntry> result = new ArrayList<>();

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                result = MovieJsonUtils.getMovieDataFromJson(jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<MovieEntry> movies) {
            Log.v(TAG, "Movies fetched: " + movies.size());
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null && movies.size() > 0) {
                showMovieListView();
                movieAdapter.setMovieList(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
