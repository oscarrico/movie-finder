package com.udacity.android.moviefinder;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.android.moviefinder.data.MovieFinderPreferences;
import com.udacity.android.moviefinder.database.AppDatabase;
import com.udacity.android.moviefinder.database.AppExecutors;
import com.udacity.android.moviefinder.database.MovieEntry;
import com.udacity.android.moviefinder.model.MovieReview;
import com.udacity.android.moviefinder.model.MovieVideo;
import com.udacity.android.moviefinder.utilities.MovieJsonUtils;
import com.udacity.android.moviefinder.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements OnClickListener, VideoAdapter.VideoAdapterOnClickHandler {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private Intent intent;
    private AppDatabase database;
    private MovieEntry movie;
    private ImageView movieIv;
    private TextView detailTitle;
    private TextView detailReleaseDate;
    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private RecyclerView reviewRecycler;
    private Switch saveMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(getApplicationContext());
        movie = new MovieEntry();
        setContentView(R.layout.activity_movie_detail);

        movieIv = (ImageView) findViewById(R.id.detail_movie_image);
        detailTitle = (TextView) findViewById(R.id.tv_detail_movie_title);
        detailReleaseDate = (TextView) findViewById(R.id.detail_release_date);
        RatingBar detailRating = (RatingBar) findViewById(R.id.detail_rating);
        TextView detailOverview = (TextView) findViewById(R.id.tv_detail_overview);
        saveMovie = (Switch) findViewById(R.id.save_movie);
        saveMovie.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        videoAdapter = new VideoAdapter(this);
        recyclerView.setAdapter(videoAdapter);

        reviewRecycler = (RecyclerView) findViewById(R.id.rv_reviews);
        LinearLayoutManager reviewLayout = new LinearLayoutManager(this);
        reviewRecycler.setLayoutManager(reviewLayout);
        reviewRecycler.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter();
        reviewRecycler.setAdapter(reviewAdapter);


        intent = getIntent();

        String title = getMovieValue("title");
        String date = getMovieValue("date");
        String image = getMovieValue("image");
        String overview = getMovieValue("overview");
        String rating = getMovieValue("rating");
        String movieId = getMovieValue("movieId");

        detailTitle.setText(title);
        detailReleaseDate.setText(date);
        detailRating.setRating(Float.parseFloat(rating));
        detailRating.setIsIndicator(true);
        detailOverview.setText(overview);
        Picasso.with(this.getBaseContext())
                .load(image)
                .into(movieIv);

        movie.setTitle(title);
        movie.setReleaseDate(date);
        movie.setImagePath(image);
        movie.setOverview(overview);
        movie.setRating(rating);
        movie.setMovieId(movieId);
        checkIfMovieSaved();
        loadMovieVideoList(MovieFinderPreferences.getPreferredVideosApi());
        loadMovieReviewList(MovieFinderPreferences.getPrefferedReviewsApi());
    }

    private String getMovieValue(String key) {
        return intent.getExtras().getString(key);
    }

    public void checkIfMovieSaved() {
        Log.v(TAG, "***** checkIfMovieSaved ***** movie:" +movie.getMovieId());
        final LiveData<MovieEntry> movieEntryLiveData = database.movieDAO().getMovieByMovieID(movie.getMovieId());
        movieEntryLiveData.observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                movieEntryLiveData.removeObserver(this);
                if(movieEntry != null) {
                    saveMovie.setChecked(true);
                } else {
                    saveMovie.setChecked(false);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        final LiveData<MovieEntry> movieEntryLiveData = database.movieDAO().getMovieByMovieID(movie.getMovieId());
        movieEntryLiveData.observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                movieEntryLiveData.removeObserver(this);
                if(movieEntry == null) {
                    saveMovie.setChecked(true);
                    addFavorite();
                } else {
                    saveMovie.setChecked(false);
                    removeFavorite(movieEntry);
                }
            }
        });
    }

    private void addFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDAO().insertMovie(movie);
            }
        });
    }

    private void removeFavorite(final MovieEntry movieEntry) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.movieDAO().deleteMovie(movieEntry);
            }
        });
    }

    private void loadMovieVideoList(String apiName) {
        String apiKey = MovieFinderPreferences.getPreferredKey(this);
        new FetchMovieVideos().execute(apiName, apiKey, movie.getMovieId());
    }

    private void loadMovieReviewList(String apiName) {
        String apiKey = MovieFinderPreferences.getPreferredKey(this);
        new FetchMovieReviews().execute(apiName, apiKey, movie.getMovieId());
    }

    @Override
    public void onClick(MovieVideo video) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl()));
        try {
            startActivity(videoIntent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Cant play video: " + video.getName());
        }
    }

    class FetchMovieVideos extends AsyncTask<String, Void, List<MovieVideo>>{

        @Override
        protected List<MovieVideo> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String apiName = params[0];
            String apiKey = params[1];
            String movieId = params[2];

            URL movieRequestUrl = NetworkUtils.buildMovieDetailUrl(apiName, apiKey, movieId);
            List<MovieVideo> result = new ArrayList<>();

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                result = MovieJsonUtils.getMovieVideosFromJson(jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> videos) {
            Log.v(TAG, "Videos fetched: " + videos.size());
            if(videos.size() > 0) {
                videoAdapter.setMovieList(videos);
            } else {
                Toast.makeText(getApplicationContext(), "Not Videos Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class FetchMovieReviews extends AsyncTask<String, Void, List<MovieReview>> {

        @Override
        protected List<MovieReview> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String apiName = params[0];
            String apiKey = params[1];
            String movieId = params[2];

            URL movieRequestUrl = NetworkUtils.buildMovieDetailUrl(apiName, apiKey, movieId);
            List<MovieReview> result = new ArrayList<>();
            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                result = MovieJsonUtils.getMovieReviewsFromJson(jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            Log.v(TAG, "Reviews fetched: " + reviews.size());
            if(reviews.size() > 0) {
                reviewAdapter.setMovieReviewList(reviews);
            } else {
                Toast.makeText(getApplicationContext(), "Not Reviews Found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
