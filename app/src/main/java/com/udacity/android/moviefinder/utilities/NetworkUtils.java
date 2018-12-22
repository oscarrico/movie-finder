package com.udacity.android.moviefinder.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie database api.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_DB_API_URL =
            "https://api.themoviedb.org/3/movie/";

    private final static String API_PARAM = "api_key";
    private final static String LANGUAGE_PARAM = "language";
    final static String PAGE_PARAM = "page";

    public static URL buildDefaultUrl(String language, String apiKey, String apiName) {

        Uri builtUri = buildUrl(language, apiKey, apiName);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildMovieDetailUrl(String apiName, String apiKey, String movieId) {
        Uri builtUri = buildMovieUrl(apiKey, apiName, movieId);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    private static Uri buildMovieUrl(String apiKey, String apiName, String movieId) {
        return Uri.parse(BASE_DB_API_URL + movieId + "/" + apiName).buildUpon()
                .appendQueryParameter(API_PARAM, apiKey)
                .build();
    }


    private static Uri buildUrl(String language, String apiKey, String apiName) {
        return Uri.parse(BASE_DB_API_URL + apiName).buildUpon()
                .appendQueryParameter(API_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, "1")
                .build();
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}