package com.udacity.android.moviefinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.udacity.android.moviefinder.database.MovieEntry;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<MovieEntry> movieList;
    private Context context;
    private final MovieAdapterOnClickHandler clickLister;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieEntry selectedMovie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickLister){
        this.clickLister = clickLister;
    }


    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView movieTitle;
        ImageView movieIv;

        private MovieAdapterViewHolder(View itemView) {
            super(itemView);
            movieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            movieIv = itemView.findViewById(R.id.image_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieEntry movie = movieList.get(position);
            clickLister.onClick(movie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        MovieEntry movie = this.movieList.get(position);
        String movieTitle = movie.getTitle();
        holder.movieTitle.setText(movieTitle);
        Picasso.with(context)
                .load(movie.getImagePath())
                .into(holder.movieIv);
    }

    @Override
    public int getItemCount() {
        if(this.movieList == null) {
            return 0;
        } else {
            return this.movieList.size();
        }
    }


    public void setMovieList(List<MovieEntry> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }
}
