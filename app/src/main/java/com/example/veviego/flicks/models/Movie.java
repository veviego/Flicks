package com.example.veviego.flicks.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by veviego on 6/22/17.
 */

@Parcel // annotation indicates class is Parcelable
public class Movie {

    // values from API
    String title;
    String overview;
    String posterPath; // only the path
    String backdropPath; // only the path
    Double voteAverage;
    Integer videoId;

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVideoId() {
        return videoId;
    }

    // no-arg, empty constructor required for Parceler
    public Movie() {}


    // initialize from JSON data
    public Movie(JSONObject object) throws JSONException {
        title = object.getString("title");
        overview = object.getString("overview");
        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        voteAverage = object.getDouble("vote_average");
        videoId = object.getInt("id");

    }
}
