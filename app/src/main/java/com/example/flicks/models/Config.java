package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    // base url for loading images
    String imageBaseUrl;
    // poster size of the image to fetch
    String posterSize;
    // backdrop size of the image to fetch in landscape
    String backdropSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject imageConfiguration = object.getJSONObject("images");

        // Get the image base URL
        imageBaseUrl = imageConfiguration.getString("base_url")
                .replace("http://", "https://"); // make sure we use HTTPS

        JSONArray posterSizes = imageConfiguration.getJSONArray("poster_sizes");
        // Use the poster size at index 3, or default to w342 if that doesn't exist
        posterSize = posterSizes.optString(3, "w342");

        JSONArray backdropSizes = imageConfiguration.getJSONArray("backdrop_sizes");
        // Use the backdrop size at index 1, or default to w780 if that doesn't exist
        backdropSize = backdropSizes.optString(1, "w780");
    }

    // helper method to construct url
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
