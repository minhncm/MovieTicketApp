package com.example.ticketapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketapp.view.Movie.MovieReviewFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieReviewActivity extends AppCompatActivity {
    
    public static final String EXTRA_MOVIE_ID = "movie_id";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);
        
        String movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        
        if (savedInstanceState == null && movieId != null) {
            MovieReviewFragment fragment = MovieReviewFragment.newInstance(movieId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.review_container, fragment)
                    .commit();
        }
    }
}
