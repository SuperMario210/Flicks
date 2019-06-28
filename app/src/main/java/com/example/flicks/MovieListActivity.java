package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.adapters.MovieAdapter;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {
    // Base URL for movies API
    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    // Parameter name for the API key
    public static final String API_KEY_PARAM = "api_key";

    // tag for logging from this activity
    public static final String TAG = "MovieListActivity";

    AsyncHttpClient client;

    // List of currently playing movies
    ArrayList<Movie> movies;

    // recycler view
    @BindView(R.id.rvMovies) RecyclerView rvMovies;

    // Adapter that connects the movies list to the recycler view
    MovieAdapter adapter;

    // Image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ButterKnife.bind(this);
        client = new AsyncHttpClient();
        movies = new ArrayList<>();

        // initialize the adapter
        adapter = new MovieAdapter(movies);

        // connect the adapter and the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        getConfiguration();
    }

    private void getNowPlaying() {
        String url = API_BASE_URL + "/movie/now_playing";

        RequestParams params = new RequestParams();
        params.add(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    // Iterate through results and add them to the movies list
                    for (int i = 0; i < results.length(); i++) {
                        movies.add(new Movie(results.getJSONObject(i)));
                        // notify the adapter to update the view
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse data from now_playing endpoint", e, true);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    private void getConfiguration() {
        String url = API_BASE_URL + "/configuration";

        RequestParams params = new RequestParams();
        params.add(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded config with base_url %s and poster_size %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));

                    adapter.setConfig(config);

                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed to parse configuration", e, true);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get API configuration", throwable, true);
            }
        });
    }

    private void logError(String message, Throwable error, boolean alertUser) {
        // Always log the error
        Log.e(TAG, message, error);

        // Alert the user
        if(alertUser) {
            Toast.makeText(MovieListActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}