package com.example.flicks;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.example.flicks.MovieListActivity.API_BASE_URL;
import static com.example.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieInfoActivity extends YouTubeBaseActivity {
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.rbRating) RatingBar rbRating;
    @Nullable @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
    @Nullable @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
    @BindView(R.id.ivPlayButton) ImageView ivPlayButton;

    // Stores the information of the movie we are displaying on this page
    Movie movie;

    // Store the base url of the backdrop image for the movie
    String backdropUrl;

    // Store the base url of the poster image for the movie
    String posterUrl;

    // Stores whether or not the video is playing
    boolean isVideoPlaying;

    // HTTP Client used for calls to the movies api
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        ButterKnife.bind(this);

        isVideoPlaying = false;
        client = new AsyncHttpClient();

        retrieveIntentData();
        displayMovieInfo();
    }

    /**
     * Retrieves movie and url data stored in the intent
     */
    private void retrieveIntentData() {
        movie = new Movie(getIntent());
        backdropUrl = getIntent().getStringExtra("backdropUrl");
        posterUrl = getIntent().getStringExtra("posterUrl");
    }

    /**
     * Override the default back button press behavior to hide the youtube player in landscape
     */
    @Override
    public void onBackPressed() {
        // Get the phone's orientation
        boolean isPortrait = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;

        // Check if the video is playing in landscape
        if(isVideoPlaying && !isPortrait) {
            // If so, hide the video
            YouTubePlayerView playerView = findViewById(R.id.player);
            playerView.setVisibility(View.GONE);
            isVideoPlaying = false;
        } else {
            // Otherwise, do the default behavior
            super.onBackPressed();
        }

    }

    /**
     * Displays the movie info in the layout
     */
    private void displayMovieInfo() {
        // Show the play button overlay
        ivPlayButton.setImageResource(R.drawable.play);

        // Set the text info of the movie
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvDate.setText(movie.getReleaseDate());
        rbRating.setRating((float) movie.getRating() / 2); // Make movie rating out of 5

        // get the device orientation
        boolean isPortrait = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;

        // get the placeholder and image view ids for the current orientation
        String imageUrl;
        if(isPortrait) {
            imageUrl = backdropUrl + movie.getBackdropPath();
        } else {
            imageUrl = posterUrl + movie.getPosterPath();
        }
        int placeholderId = isPortrait ? R.drawable.flicks_backdrop_placeholder :
                R.drawable.flicks_movie_placeholder;
        ImageView imageView = isPortrait ? ivBackdropImage : ivPosterImage;

        // set the movie poster image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

        // darken the poster image so the play button shows better
        imageView.setColorFilter(R.color.asphaltTransparent, PorterDuff.Mode.SRC_ATOP);

    }

    /**
     * Called when the poster/backdrop image is pressed.  Gets the url of the trailer and plays it
     */
    public void getTrailerKey(View view) {
        // Start by getting the URL of the video to play from the movies api
        String url = API_BASE_URL + "/movie/" + movie.getId() + "/videos";

        RequestParams params = new RequestParams();
        params.add(API_KEY_PARAM, getString(R.string.movies_api_key));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Get the youtube video key from the response
                    JSONArray results = response.getJSONArray("results");
                    String trailerKey = results.getJSONObject(0).getString("key");

                    Log.i("MovieTrailerActivity", String.format("Trailer Key", trailerKey));

                    getTrailerKey(trailerKey);
                } catch (JSONException e) {
                    Log.e("MovieTrailerActivity", "Error parsing trailer key", e);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Log.e("MovieTrailerActivity", "Error loading trailer key", throwable);
            }
        });
    }

    /**
     * Start playing a youtube video
     * @param videoKey the key of the video to play
     */
    private void getTrailerKey(String videoKey) {
        // resolve the player view from the layout
        YouTubePlayerView playerView = findViewById(R.id.player);

        // initialize with API key stored in secrets.xml
        playerView.initialize(getString(R.string.youtube_api_key),
        new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean b) {
                // Play the trailer
                playerView.setVisibility(View.VISIBLE);
                player.loadVideo(videoKey);
                isVideoPlaying = true;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult result) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }
}
