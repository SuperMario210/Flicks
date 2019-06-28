package com.example.flicks;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieInfoActivity extends AppCompatActivity {
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.rbRating) RatingBar rbRating;
    @Nullable @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
    @Nullable @BindView(R.id.ivPosterImage) ImageView ivPosterImage;

    Movie movie;
    String backdropImageBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        ButterKnife.bind(this);

        movie = new Movie(getIntent());
        backdropImageBaseUrl = getIntent().getStringExtra("backdropUrl");

        displayMovieInfo();
    }

    private void displayMovieInfo() {
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvDate.setText(movie.getReleaseDate());
        rbRating.setRating((float) movie.getRating() / 2);

        // get the device orientation
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // get the placeholder and image view ids for the current orientation
        String imageUrl;
        if(isPortrait) {
            imageUrl = backdropImageBaseUrl + movie.getBackdropPath();
        } else {
            imageUrl = backdropImageBaseUrl + movie.getPosterPath();
        }
        int placeholderId = isPortrait ? R.drawable.flicks_backdrop_placeholder : R.drawable.flicks_movie_placeholder;
        ImageView imageView = isPortrait ? ivBackdropImage : ivPosterImage;

        // set the movie poster image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

    }
}
