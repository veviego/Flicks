package com.example.veviego.flicks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.veviego.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.veviego.flicks.MovieListActivity.API_BASE_URL;
import static com.example.veviego.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity {

    // instance fields
    AsyncHttpClient client;
    // tag for logging from this activity
    public final static String TAG = "MovieDetailActivity";

    // the movie to display
    Movie movie;
    String youTubeKey = null;
    Context context = this;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivTrailer = (ImageView) findViewById(R.id.ivTrailer);

        //unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f: voteAverage);

        // initialize client
        client = new AsyncHttpClient();

        // get the trailer
        getTrailer();

        //load backdrop image using glide
        Glide.with(context)
                .load(getIntent().getStringExtra("Backdrop url"))
                .bitmapTransform(new RoundedCornersTransformation(context, 35, 0))
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .error(R.drawable.flicks_backdrop_placeholder)
                .into(ivTrailer);
    }


    private void getTrailer() {
        // get movie trailer info
        String url = API_BASE_URL + "/movie/" + movie.getVideoID().toString() + "/videos";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key, always required
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get the YouTube video key
                try {
                    JSONArray results = response.getJSONArray("results");
                    JSONObject obj = results.getJSONObject(0);
                    youTubeKey = obj.getString("key");
                    Log.i(TAG, String.format("YouTube key %s for %s obtained", youTubeKey, movie.getTitle()));

                    // add listener to image
                    addTrailerListener();

                } catch (JSONException e) {
                    logError("Failed to parse movie video", e, true);

                }
            }
        });

    }

    private void addTrailerListener() {
        ivTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (youTubeKey != null) {
                    // create intent for the new activity
                    Intent intent = new Intent(context, MovieTrailerActivity.class);
                    // pass along the video string
                    intent.putExtra("Youtube Key", youTubeKey);
                    // show the activity
                    context.startActivity(intent);
                }
            }
        });
    }


    // handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser) {
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

    }
}
