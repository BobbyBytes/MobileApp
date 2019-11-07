package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.UserData;
import com.example.myapplication.mapActivity;
import com.example.myapplication.messengerActivity;
import com.example.myapplication.userProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScrollingMultipleItems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);

        ImageView iv  = findViewById(R.id.mapButton);

        View.OnClickListener on = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMap = new Intent();
                goToMap.setClass(getApplicationContext(), mapActivity.class);
                startActivity(goToMap);
            }
        };
        iv.setOnClickListener(on);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserData User1 = new UserData("Tony", "Balogna", "BigTB");
        db.collection("users").document("User1").set(User1);

        DocumentReference docRef = db.collection("users").document("User1");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserData mydata = documentSnapshot.toObject(UserData.class);
            }


        });

    }

    public void goToMapScreen(View view)
    {
        Intent goToMap = new Intent();
        goToMap.setClass(this, mapActivity.class);
        startActivity(goToMap);
    }

    public void viewProfile(View view)
    {
        //goto user profile
        Intent goToUser = new Intent();
        goToUser.setClass(this, userProfile.class);
        startActivity(goToUser);
    }

    public void viewInbox(View view)
    {
        Intent gotoInbox = new Intent();
        gotoInbox.setClass(this, messengerActivity.class);
        startActivity(gotoInbox);
    }


}

