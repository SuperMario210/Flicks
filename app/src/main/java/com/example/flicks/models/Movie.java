package com.example.flicks.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Parcel
public class Movie {
    // Values from the API
    String title;
    String overview;
    String posterPath; // not the full URL
    String backdropPath; // not the full URL
    String releaseDate;
    double rating;
    int id;

    public Movie() {
    }

    // Initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        releaseDate = parseDate(object.getString("release_date"));
        rating = object.getDouble("vote_average");
        id = object.getInt("id");
    }

    private String parseDate(String inputDate) {
        SimpleDateFormat parseInputDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = parseInputDate.parse(inputDate);
            SimpleDateFormat formateOutputDate = new SimpleDateFormat("MMMM dd, yyyy");
            return formateOutputDate.format(date);
        } catch (ParseException e) {
            Log.e("Movie.java", "could not parse date", e);
            return inputDate;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getAbbreviatedOverview() {
        String[] splitOverview = overview.split(" ");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < splitOverview.length; i++) {
            if(i > 32) {
                sb.append("...");
                break;
            }
            if(i > 0) {
                sb.append(" ");
            }
            sb.append(splitOverview[i]);
        }
        return sb.toString();
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }
}
