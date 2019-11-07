package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainContent extends AppCompatActivity {
    List <UserData> mUserData = new ArrayList<>();
    List<UserData> DBUserData = new ArrayList<>();
    // Create adapter passing in the user data
    final UsersAdapter adapter = new UsersAdapter(mUserData);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);
        //Create connection to DB
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Create and write Write a user test
        UserData User1 = new UserData("Test User", "From Class", "App Created User");
        db.collection("users").document("User1").set(User1);

        DocumentReference docRef = db.collection("users").document("User1");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserData mydata = documentSnapshot.toObject(UserData.class);
            }
        });

        // Lookup the recycler view in activity layout
        RecyclerView userListView = (RecyclerView)findViewById(R.id.userListView);

        // Set layout manager to position the items
        userListView.setLayoutManager(new LinearLayoutManager(this));
        // Attach the adapter to the recyclerview to populate items
        userListView.setAdapter(adapter);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                UserData Doc_From_DB = document.toObject(UserData.class);
                                mUserData.add(Doc_From_DB);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        // Initialize ListOfUsers
        UserData U1 = new UserData("Trey","Anastasio","Big Red");
        mUserData.add(U1);

        U1 = new UserData("Keith","Moon","Drummer");
        mUserData.add(U1);

        U1 = new UserData("Neil","Armstrong","Mr. Moon");
        mUserData.add(U1);

        U1 = new UserData("Max","Rider","Singer");
        AddToList(U1);


    }
    private void AddToList(UserData UD){
        mUserData.add(UD);
        adapter.notifyDataSetChanged();

    }


    public void viewProfile(View view)
    {
        //goto user profile
        Intent gotoUser = new Intent();
        gotoUser.setClass(this, userProfile.class);
        startActivity(gotoUser);
    }
}

