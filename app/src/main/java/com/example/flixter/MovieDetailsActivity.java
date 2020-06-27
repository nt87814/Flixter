package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityMovieDetailsBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivBackdrop;
    public static final String TAG = "MovieDetailsActivity";
    String youtubeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Retrieve and unwrap
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
//        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        ivBackdrop = binding.ivBackdrop;

        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        binding.rbVoteAverage.setRating((float) (movie.getVoteAverage() / 2));
//        Glide.with(this).load(movie.getBackdropPath()).transform(new RoundedCorners(30)).into(ivBackdrop);
        Glide.with(this).load(movie.getBackdropPath()).into(ivBackdrop);

        // Get the YouTube ID of the video
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {

                    if (jsonObject.getJSONArray("results").length() > 0) {

                        JSONArray results = jsonObject.getJSONArray("results");
                        Log.i(TAG, "Results: " + results.toString());

                        youtubeID = results.getJSONObject(0).getString("key");
                    }

                    // Put setOnClickListener here because it is asynchronous and only want it to be clickable if retrieve a key
                    ivBackdrop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Use an intent and pass the video id
                            Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);

                            intent.putExtra("youtubeID", youtubeID);

                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

    }
}