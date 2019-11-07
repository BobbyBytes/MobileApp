package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainContent extends AppCompatActivity {
    List <UserData> mUserData = new ArrayList<>();
    List<UserData> DBUserData = new ArrayList<>();
    // Create adapter passing in the user data
    Context context;
    final UsersAdapter adapter = new UsersAdapter(mUserData);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);
        //Create connection to DB
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Context mContext = this.context;
        //Create and write Write a user test
        UserData User1 = new UserData("Test User2", "From Code Behind", "App Created User2");
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

////
//        // Create a storage reference from our app
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//
//// Create a reference to "mountains.jpg"
//        StorageReference mountainsRef = storageRef.child("rob2.jpg");
//
//// Create a reference to 'images/mountains.jpg'
//        StorageReference mountainImagesRef = storageRef.child("rob2.jpg");
//
//// While the file names are the same, the references point to different files
//        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
//        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
//
//        String imageUri = "drawable://" + R.drawable.border;
//        Uri path = Uri.parse("android.resource://com.example.myapplication.test/drawable/rob2");
//        Uri file = Uri.fromFile(new File(imageUri));
//        StorageReference riversRef = storageRef.child(file.getLastPathSegment());
//        UploadTask uploadTask = riversRef.putFile(path);
//
//// Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                 taskSnapshot.getMetadata();
//                // ...
//            }
//        });
//        File testFile = new File(this.getExternalFilesDir(null), "TestFile.txt");
//        if (!testFile.exists()) {
//            try {
//                testFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        File localFile = null;
//        try {
//            localFile = File.createTempFile("images", "jpg");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        riversRef.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
        userListView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, userListView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        UserData mUser = mUserData.get(position);
                        CreateAndViewUserProfile(view, mUser);


                        ;                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }
    private void AddToList(UserData UD){
        mUserData.add(UD);
        adapter.notifyDataSetChanged();

    }
    public void CreateAndViewUserProfile(View view, UserData userData)
    {
        //goto user profile
        String firstName = userData.getFirstName();
        String lastName = userData.getLastName();
        String nickName = userData.getNickname();

        Intent gotoUserIntent = new Intent();
        gotoUserIntent.putExtra("idFirstName",firstName);
        gotoUserIntent.putExtra("idLastName",lastName);
        gotoUserIntent.putExtra("idNickName", nickName);

        gotoUserIntent.setClass(this, userProfile.class);
        startActivity(gotoUserIntent);
    }
}

