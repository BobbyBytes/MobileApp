package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class MainContent extends AppCompatActivity {
    List<UserData> mUserData = new ArrayList<>();
    FirebaseFirestore db;
    Context context;
    private FirebaseAuth mAuth;
    // Create adapter and pass in the user data list
    final UsersAdapter adapter = new UsersAdapter(mUserData);
    Bitmap bitmap = null;
    String dataBaseCollectionPath;
    Boolean isArtist = null;
    private Object lock = new Object();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);
        mUserData.clear();
        final Context mContext = this.context;

        mAuth = FirebaseAuth.getInstance();
        //Create connection to DB
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();


        searchDBforUser(db);


        //Create and write Write a user test
        UserData User1 = new UserData("User3", "From Code Behind", "App Created User3");
        User1.setEmailAddress("Neil_Armstrong");
        db.collection("users").document("User4").set(User1);
        Log.d("MainContent", "set user to DB ");

        // Lookup the recycler view in activity layout
        RecyclerView userListView = findViewById(R.id.userListView);

        // Set layout manager to position the items
        userListView.setLayoutManager(new LinearLayoutManager(this));
        // Attach the adapter to the recyclerview to populate items
        userListView.setAdapter(adapter);

        //Add the on click listener to the recycler view.
        userListView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, userListView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        UserData mUser = mUserData.get(position);
                        CreateAndViewUserProfile(view, mUser);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever on looongg click
                    }
                })
        );
    }
    //End OnCreate

    public void CreateAndViewUserProfile(View view, UserData userData) {

        String displayName = userData.getDisplayName();
        String genre = userData.getGenre();
        String bio = userData.getbio();
        String eMailAddr = userData.getEmailAddress();

        Intent gotoUserIntent = new Intent();
        gotoUserIntent.putExtra("idDisplayName", displayName);
        gotoUserIntent.putExtra("idGenre", genre);
        gotoUserIntent.putExtra("idBio", bio);
        gotoUserIntent.putExtra("idEmail", eMailAddr);
        //goto user profile
        gotoUserIntent.setClass(this, OtherUserProfile.class);
        startActivity(gotoUserIntent);
    }

    public void goToMapScreen(View view) {
        Intent goToMap = new Intent();
        goToMap.putExtra("idIsArtist", isArtist);
        goToMap.setClass(this, MapsActivity.class);
        startActivity(goToMap);
    }

    public void viewInbox(View view) {
        Intent gotoInbox = new Intent();
        gotoInbox.setClass(this, MessengerActivity.class);
        startActivity(gotoInbox);
    }

    public void viewMyProfile(View view) {
        Intent gotoMyProfile = new Intent();
        gotoMyProfile.setClass(this, MeUserProfile.class);
        startActivity(gotoMyProfile);
    }

    void getImagesForProfilesFromList(List<UserData> usersList) {
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        int i = 0;
        //For each user in the users list
        for (final UserData user : usersList) {
            try {
                user.localFile = File.createTempFile("image" + i, "jpg");
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

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                            user.setImageString(encodedImage);
                            Log.d("MainContent", "User bitmap added");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    Log.d("MainContent", "Downloading image to main Content failed");
                    // TODO Set a default profile pic or something
                }
            });
            i++;
        }//End for loop
    }

    private void searchDBforUser(FirebaseFirestore db){
        FirebaseUser user = mAuth.getCurrentUser();
        final String email = user.getEmail();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String tmpEmail = new String();
                                tmpEmail = document.getString("emailAddress");
                                if(tmpEmail != null){
                                    if(tmpEmail.equals(email)){
                                        initializePage("venues");
                                        return;
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("venues")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                String tmpEmail = "";
                                tmpEmail = document.getString("emailAddress");
                                if(tmpEmail != null){
                                    if(tmpEmail.equals(email)){
                                        initializePage("users" );
                                        return;
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void setIsArtist(boolean misArtist){

        synchronized (lock){
            isArtist = misArtist;
            lock.notify();
        }

    }

    private void initializePage( String dataBaseCollectionPath){
        //Get the entire collection called "users" from firebase.
        db.collection(dataBaseCollectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                UserData userDataFromDB = document.toObject(UserData.class);

                                mUserData.add(userDataFromDB);
                            }
                            getImagesForProfilesFromList(mUserData);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

