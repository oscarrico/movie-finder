package com.udacity.android.moviefinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.android.moviefinder.model.MovieReview;
import com.udacity.android.moviefinder.model.MovieVideo;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private List<MovieReview> reviewList;
    private Context context;

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView review;

        public ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            review = itemView.findViewById(R.id.review_id);
        }
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder reviewAdapterViewHolder, int i) {
        MovieReview review = this.reviewList.get(i);
        reviewAdapterViewHolder.review.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(this.reviewList == null) {
            return 0;
        } else {
            return this.reviewList.size();
        }
    }

    public void setMovieReviewList(List<MovieReview> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }
}
