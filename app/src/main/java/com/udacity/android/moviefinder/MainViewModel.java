package com.udacity.android.moviefinder;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.android.moviefinder.database.AppDatabase;
import com.udacity.android.moviefinder.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LiveData<List<MovieEntry>> movieEntries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.v(TAG, "Actively fetching movies from the database");
        movieEntries = database.movieDAO().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovieEntries() {
        return movieEntries;
    }
}
