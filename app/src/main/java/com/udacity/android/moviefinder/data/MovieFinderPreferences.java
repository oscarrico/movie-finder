package com.udacity.android.moviefinder.data;

import android.content.Context;
import android.support.constraint.ConstraintLayout;

public class MovieFinderPreferences {

    private static final String LANGUAGE_PARAM = "en-US";
    // TODO Add the API KEY
    private static final String API_KEY = "<API KEY>";
    private static final String POPULAR_DB_API_URL = "popular";
    private static final String TOP_RATED_DB_API_URL = "top_rated";
    private static final String NOW_PLAYING_DB_API_URL = "now_playing";
    private static final String VIDEOS_DB_API_URL = "videos";
    private static final String REVIEWS_DB_API_URL = "reviews";

    public static String getPreferredNowPlaying(Context context) {
        return getDefaultNowPlaying();
    }

    private static String getDefaultNowPlaying() {
        return NOW_PLAYING_DB_API_URL;
    }


    public static String getPreferredTopRated(Context context) {
        return getDefaultTopRated();
    }

    private static String getDefaultTopRated() {
        return TOP_RATED_DB_API_URL;
    }

    public static String getPreferredPopularApi(Context context) {
        return getDefaultPopularApi();
    }

    public static String getPreferredVideosApi() {
        return getDefaultVideosApi();
    }

    public static String getPrefferedReviewsApi() {
        return getDefaultReviewsApi();
    }

    private static String getDefaultVideosApi() { return VIDEOS_DB_API_URL; }

    private static String getDefaultReviewsApi() {return REVIEWS_DB_API_URL; }

    private static String getDefaultPopularApi() {
        return POPULAR_DB_API_URL;
    }

    public static String getPreferredLanguage(Context context) {
        return getDefaultLanguage();
    }

    private static String getDefaultLanguage() {
        return LANGUAGE_PARAM;
    }

    public static String getPreferredKey(Context context) {
        return getDefaultApiKey();
    }

    private static String getDefaultApiKey() {
        return API_KEY;
    }
}
