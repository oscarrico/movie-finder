package com.udacity.android.moviefinder.utilities;

import android.util.Log;

import com.udacity.android.moviefinder.database.MovieEntry;
import com.udacity.android.moviefinder.model.MovieReview;
import com.udacity.android.moviefinder.model.MovieVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String RATING = "vote_average";
    private static final String IMAGE_PATH = "backdrop_path";
    private static final String TITLE = "title";
    private static final String RESULTS = "results";
    private static final String MOVIE_ID = "id";
    private static final String IMAGE_DB_URL = "https://image.tmdb.org/t/p/w500";
    private static final String VIDEO_KEY_PATH = "key";
    private static final String VIDEO_NAME_PATH = "name";
    private static final String YOUTUBE_URL = "vnd.youtube:";
    private static final String AUTHOR_URL = "author";
    private static final String CONTENT_URL = "content";
    private static final String REVIEW_URL = "url";

    public static List<MovieEntry> getMovieDataFromJson(String movieResponseJsonStr)
            throws JSONException {

        JSONObject movieListJson = new JSONObject(movieResponseJsonStr);
        JSONArray movieJsonArray = movieListJson.getJSONArray(RESULTS);
        List<MovieEntry> movieList = new ArrayList<>();

        for(int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject movieJson = movieJsonArray.getJSONObject(i);
            MovieEntry movie = new MovieEntry();
            String imagePath = movieJson.getString(IMAGE_PATH);
            if(imagePath != null && !imagePath.isEmpty()) {
                movie.setMovieId(movieJson.getString(MOVIE_ID));
                movie.setImagePath(IMAGE_DB_URL + imagePath);
                movie.setOverview(movieJson.getString(OVERVIEW));
                movie.setRating(movieJson.getString(RATING));
                movie.setReleaseDate(movieJson.getString(RELEASE_DATE));
                movie.setTitle(movieJson.getString(TITLE));

                movieList.add(movie);
                Log.v(TAG, "Movie created " + movie);
            }

        }

        Log.v(TAG, "Movie List Size " + movieList.size());
        return movieList;
    }

    public static List<MovieVideo> getMovieVideosFromJson(String movieResponseJsonStr)
            throws JSONException {

        JSONObject movieListJson = new JSONObject(movieResponseJsonStr);
        JSONArray movieJsonArray = movieListJson.getJSONArray(RESULTS);
        List<MovieVideo> videoList = new ArrayList<>();

        for(int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject movieJson = movieJsonArray.getJSONObject(i);
            MovieVideo video = new MovieVideo();
            String key = movieJson.getString(VIDEO_KEY_PATH);
            video.setKey(key);
            video.setUrl(YOUTUBE_URL + key);
            video.setName(movieJson.getString(VIDEO_NAME_PATH));
            videoList.add(video);
        }

        Log.v(TAG, "Video List Size " + videoList.size());
        return videoList;
    }

    public static List<MovieReview> getMovieReviewsFromJson(String movieResponseJsonStr)
            throws JSONException {

        JSONObject movieListJson = new JSONObject(movieResponseJsonStr);
        JSONArray movieJsonArray = movieListJson.getJSONArray(RESULTS);
        List<MovieReview> reviews = new ArrayList<>();

        for(int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject reviewJson = movieJsonArray.getJSONObject(i);
            MovieReview review = new MovieReview();
            review.setAuthor(reviewJson.getString(AUTHOR_URL));
            review.setUrl(reviewJson.getString(REVIEW_URL));
            review.setContent(reviewJson.getString(CONTENT_URL));
            reviews.add(review);
        }

        Log.v(TAG, "Review List Size " + reviews.size());
        return reviews;
    }

}
