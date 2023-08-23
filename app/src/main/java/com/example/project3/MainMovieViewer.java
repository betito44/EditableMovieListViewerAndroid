package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

public class MainMovieViewer extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_fragment);
        ImageView imageView = findViewById(R.id.large_image);
        Intent intent = getIntent();
        Picasso.get().load(intent.getStringExtra("picture")).into(imageView);
        TextView titleText = findViewById(R.id.title);
        titleText.setText(intent.getStringExtra("title"));
        TextView yearText = findViewById(R.id.year);
        yearText.setText(intent.getStringExtra("year"));
        TextView lengthText = findViewById(R.id.runtime);
        lengthText.setText(intent.getStringExtra("length"));
        TextView directorText = findViewById(R.id.director);
        directorText.setText(intent.getStringExtra("director"));
        TextView starsText = findViewById(R.id.stars);
        starsText.setText(intent.getStringExtra("stars"));
        RatingBar ratingBar = findViewById(R.id.movie_rating);
        ratingBar.setRating(Float.parseFloat(intent.getStringExtra("rating")));
        TextView descriptionText = findViewById(R.id.description);
        descriptionText.setText(intent.getStringExtra("description"));

    }

}
