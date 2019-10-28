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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);


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
        RecyclerView userListView = (RecyclerView)findViewById(R.id.userListView);
        // Lookup the recyclerview in activity layout
        // Create adapter passing in the sample user data
        final UsersAdapter adapter = new UsersAdapter(mUserData);
        // Attach the adapter to the recyclerview to populate items
        // Set layout manager to position the items
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.setAdapter(adapter);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                UserData ME = document.toObject(UserData.class);
                                mUserData.add(ME);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        // Lookup the recyclerview in activity layout
        // Initialize ListOfUsers
        UserData U1 = new UserData("Hey","Joe","It's ME");
        mUserData.add(U1);

        Log.d("fgfg", "onCreate:" + mUserData.size());
        U1 = new UserData("Firstie","LAstie","NickName");
        mUserData.add(U1);

        U1 = new UserData("FirstName","LastName","NickName");
        mUserData.add(U1);

        U1 = new UserData("Max","Rider","Singer");
        AddToList(U1);


    }
    private void AddToList(UserData UD){
        mUserData.add(UD);
    }


    public void viewProfile(View view)
    {
        //goto user profile
        Intent gotoUser = new Intent();
        gotoUser.setClass(this, userProfile.class);
        startActivity(gotoUser);
    }
}

