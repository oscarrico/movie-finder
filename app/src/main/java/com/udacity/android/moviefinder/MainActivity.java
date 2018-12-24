package com.udacity.android.moviefinder;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.udacity.android.moviefinder.adapters.MovieAdapter;
import com.udacity.android.moviefinder.data.MovieFinderPreferences;
import com.udacity.android.moviefinder.database.AppDatabase;
import com.udacity.android.moviefinder.database.MovieEntry;
import com.udacity.android.moviefinder.utilities.MovieJsonUtils;
import com.udacity.android.moviefinder.utilities.NetworkUtils;
import com.udacity.android.moviefinder.adapters.MovieAdapter.MovieAdapterOnClickHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieEntry>>,
        MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_NAME = "api_name";
    private static final String SORT_ORDER = "sort_order";
    private static final String DATABASE_SORT = "FAVORITES";
    private static final int MOVIE_LOADER_ID = 0;

    private TextView mErrorMessageDisplay;
    private ProgressBar pbLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private AppDatabase database;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;

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

        LoaderManager.LoaderCallbacks<List<MovieEntry>> callback = MainActivity.this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferencesEditor = sharedPreferences.edit();

        String sortOrder =
                sharedPreferences.getString(SORT_ORDER, MovieFinderPreferences.getPreferredNowPlaying(this));
        Log.e(TAG, "Sort Order: " + sortOrder);

        if (sortOrder.equals(DATABASE_SORT)) {
            loadFavoriteMovies();
            return;
        } else {
            Bundle bundleForLoader = new Bundle();
            bundleForLoader.putString(API_NAME, sortOrder);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundleForLoader, callback);
        }
    }

    private void updateSharedPreferences(String newSortOrder) {
        Log.e(TAG, "Updating Sort Order: " + newSortOrder);
        preferencesEditor.putString(SORT_ORDER, newSortOrder);
        preferencesEditor.apply();
    }

    private void loadMoviesMovies(String apiName) {

        showMovieListView();
        updateSharedPreferences(apiName);
        Bundle bundleForLoader = new Bundle();
        bundleForLoader.putString(API_NAME, apiName);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, bundleForLoader, this);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, bundleForLoader, this);
        }
    }


    private void loadFavoriteMovies() {
        showMovieListView();
        updateSharedPreferences(DATABASE_SORT);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovieEntries().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                movieAdapter.setMovieList(movieEntries);
            }
        });
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
            loadMoviesMovies( MovieFinderPreferences.getPreferredPopularApi(this));
            return true;
        } else if(itemThatWasClickedId == R.id.action_highest_rated) {
            Toast.makeText(this, "Highest Rated Movies", Toast.LENGTH_SHORT).show();
            loadMoviesMovies( MovieFinderPreferences.getPreferredTopRated(this));
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

    @NonNull
    @Override
    public Loader<List<MovieEntry>> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<List<MovieEntry>>(this) {

            List<MovieEntry> movieEntries;

            @Override
            protected void onStartLoading() {
                if(movieEntries != null) {
                    deliverResult(movieEntries);
                } else {
                    pbLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Nullable
            @Override
            public List<MovieEntry> loadInBackground() {

                String language = MovieFinderPreferences.getPreferredLanguage(getApplicationContext());
                String apiKey = MovieFinderPreferences.getPreferredKey(getApplicationContext());
                String apiName =  bundle.getString(API_NAME);

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
            public void deliverResult(@Nullable List<MovieEntry> data) {
                movieEntries = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieEntry>> loader, List<MovieEntry> movieEntries) {
        Log.v(TAG, "Movies fetched: " + movieEntries.size());
        pbLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieEntries != null && movieEntries.size() > 0) {
            showMovieListView();
            movieAdapter.setMovieList(movieEntries);
        } else {
            showErrorMessage();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String sortOrder =
                sharedPreferences.getString(SORT_ORDER, MovieFinderPreferences.getPreferredNowPlaying(this));
        if (sortOrder.equals(DATABASE_SORT)) {
            Log.e(TAG, "*********AGAIN onSaveInstanceState Sort Order: " + sortOrder);
            loadFavoriteMovies();
        }

        Log.e(TAG, "********* onSaveInstanceState Sort Order: " + sortOrder);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieEntry>> loader) {}
}
