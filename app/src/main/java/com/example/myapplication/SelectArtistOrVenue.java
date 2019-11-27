package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectArtistOrVenue extends AppCompatActivity {
    private String eMailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_artist_or_venue);
        Intent caller = getIntent();
        eMailAddress = caller.getStringExtra("idUserEmail");
    }

    public void btnArtistClick(View view){
        goToNewArtistActivity();
    }

    public void btnVenueClick(View view){
        goToMainContent( false);
    }

    private void goToMainContent(boolean isArtist){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserData mUser = new UserData();
        mUser.setEmailAddress(eMailAddress);
        db.collection("users").document(eMailAddress).set(mUser);
        Intent intent = new Intent(this, MainContent.class);
        intent.putExtra("idIsArtist", isArtist);
        startActivity(intent);
    }
    private void goToNewArtistActivity(){
        Intent intent = new Intent(this, CreateArtistProfile.class);
        startActivity(intent);
    }
}
