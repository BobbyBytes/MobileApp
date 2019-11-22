package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainContent extends AppCompatActivity {
    List<UserData> mUserData = new ArrayList<>();
    Context context;
    private FirebaseAuth mAuth;
    // Create adapter and pass in the user data list
    final UsersAdapter adapter = new UsersAdapter(mUserData);
    Bitmap bitmap = null;
    boolean isArtist;
    String dataBaseCollectionPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);
        final Context mContext = this.context;

        mAuth = FirebaseAuth.getInstance();
        //Create connection to DB
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        intent.getBooleanExtra("idIsArtist", isArtist);
        if (isArtist == true){
            dataBaseCollectionPath = "venues";
        }
        else {
            dataBaseCollectionPath = "users";
        }
        //Create and write Write a user test
        UserData User1 = new UserData("User3", "From Code Behind", "App Created User3");
        User1.setEmailAddress("Neil_Armstrong");
        db.collection("users").document("User4").set(User1);
        Log.d("MainContent", "set user to DB ");
        DocumentReference docRef = db.collection("users").document("User3");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserData mydata = documentSnapshot.toObject(UserData.class);
            }
        });

        // Lookup the recycler view in activity layout
        RecyclerView userListView = (RecyclerView) findViewById(R.id.userListView);


        // Set layout manager to position the items
        userListView.setLayoutManager(new LinearLayoutManager(this));
        // Attach the adapter to the recyclerview to populate items
        userListView.setAdapter(adapter);

        //Get the entire collection called "users" from firebase.
        db.collection(dataBaseCollectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                UserData Doc_From_DB = document.toObject(UserData.class);
                                mUserData.add(Doc_From_DB);
                            }
                            getImagesForProfilesFromList(mUserData);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        

        //Add the on click listener to the recycler view.
        userListView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, userListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        UserData mUser = mUserData.get(position);
                        CreateAndViewUserProfile(view, mUser);
                        ;
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }
    //End OnCreate

    private void AddToList(UserData UD) {
        mUserData.add(UD);
        adapter.notifyDataSetChanged();

    }

    public void CreateAndViewUserProfile(View view, UserData userData) {
        //goto user profile


        String firstName = userData.getFirstName();
        String lastName = userData.getLastName();
        String nickName = userData.getNickname();
        String eMailAddr = userData.getEmailAddress();

        Intent gotoUserIntent = new Intent();
        gotoUserIntent.putExtra("idFirstName", firstName);
        gotoUserIntent.putExtra("idLastName", lastName);
        gotoUserIntent.putExtra("idNickName", nickName);
        gotoUserIntent.putExtra("idEmail", eMailAddr);

        gotoUserIntent.setClass(this, otherUserProfile.class);
        startActivity(gotoUserIntent);

    }

    public void goToMapScreen(View view) {
        Intent goToMap = new Intent();
        goToMap.setClass(this, MapsActivity.class);
        startActivity(goToMap);
    }

    public void viewInbox(View view) {
        Intent gotoInbox = new Intent();
        gotoInbox.setClass(this, messengerActivity.class);
        startActivity(gotoInbox);
    }

    public void viewMyProfile(View view) {
        Intent gotoMyProfile = new Intent();
        gotoMyProfile.setClass(this, meUserProfile.class);
        startActivity(gotoMyProfile);
    }

    void getImagesForProfilesFromList(List<UserData> usersList) {
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        int i = 0;

        for (final UserData user : usersList)
        {
            try {
               user.localFile  = File.createTempFile("image" + i, "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageReference userProfilePicRef = mStorageRef.child(user.getEmailAddress() + ".jpg");
            userProfilePicRef.getFile(user.localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // Now set the image(bitmap) to the view


                            String filePath = user.localFile.getAbsolutePath();
                            bitmap = BitmapFactory.decodeFile(filePath);
                            user.setmBitmap(bitmap);
                            Log.d("MainContent", "User bitmap added");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                    Log.d("MainContent", "Downloading image to main Content failed");
                    //Set a default profile pic

                }
            });
            i++;
        }
    }


}

