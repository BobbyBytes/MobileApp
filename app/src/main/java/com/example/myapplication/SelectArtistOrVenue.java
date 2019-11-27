package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectArtistOrVenue extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_artist_or_venue);
    }

    public void btnArtistClick(View view){
        goToNewArtistActivity();
    }

    public void btnVenueClick(View view){
        goToNewVenueActivity();
    }

    private void goToNewArtistActivity(){
        Intent intent = new Intent(this, CreateArtistProfile.class);
        startActivity(intent);
    }
    private void goToNewVenueActivity(){
        Intent intent = new Intent(this, CreateVenueProfile.class);
        startActivity(intent);
    }
}
