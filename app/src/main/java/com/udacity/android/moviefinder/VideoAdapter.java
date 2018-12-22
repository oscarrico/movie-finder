package com.udacity.android.moviefinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.android.moviefinder.model.MovieVideo;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private List<MovieVideo> movieVideoList;
    private Context context;
    private final VideoAdapterOnClickHandler clickListener;

    public VideoAdapter(VideoAdapterOnClickHandler clickListner) {
        this.clickListener = clickListner;
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(MovieVideo selectedMovie);
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView videoLink;
        TextView videoName;

        public VideoAdapterViewHolder(View view) {
            super(view);
            videoLink = view.findViewById(R.id.movie_link);
            videoName = view.findViewById(R.id.video_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieVideo movie = movieVideoList.get(position);
            clickListener.onClick(movie);
        }
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder viewHolder, int position) {
        MovieVideo video = this.movieVideoList.get(position);
        viewHolder.videoName.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        if(this.movieVideoList == null) {
            return 0;
        } else {
            return this.movieVideoList.size();
        }
    }


    public void setMovieList(List<MovieVideo> movieVideoList) {
        this.movieVideoList = movieVideoList;
        notifyDataSetChanged();
    }
}
