package com.example.flicks.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flicks.MovieInfoActivity;
import com.example.flicks.R;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    // List of movies
    private ArrayList<Movie> movies;
    private Config config;
    private Context context;
    private RecyclerView recyclerView; // reference to the recycler view this adapter is attached to

    public void setConfig(Config config) {
        this.config = config;
    }

    // Initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    // Create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;
        @Nullable @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
        @Nullable @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    // Creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Create the view using the item_movie layout
        View movieItemView = inflater.inflate(R.layout.item_movie, parent, false);
        movieItemView.setOnClickListener((final View view) -> {
            int index = recyclerView.getChildLayoutPosition(view);
            Movie movie = movies.get(index);
            Intent i = new Intent(context, MovieInfoActivity.class);
            movie.putIntent(i);
            i.putExtra("backdropUrl", config.getImageUrl(config.getBackdropSize(), ""));
            context.startActivity(i);
        });

        return new ViewHolder(movieItemView);
    }

    // Binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the movie data at the specified position
        Movie movie = movies.get(position);

        // populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getAbbreviatedOverview());

        // get the device orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // get the correct url based on the device orientation
        String imageUrl;
        if(isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        // get the placeholder and image view ids for the current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView =   isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        // set the movie poster image
        Log.i("PosterImage", imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
