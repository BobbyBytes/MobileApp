package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class userProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;

    private Uri mImageUri;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword("fakeUser@MyFakeUser7.com", "password")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MyTag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MyTag", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
        FirebaseUser User = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("Neil_Armstrong.jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                Log.d("MyTAg", "onFailure: It didnt work");
            }
        });

        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Grab the name string from the calling intent
        Intent caller = getIntent();
        String firstName = caller.getStringExtra("idFirstName");
        TextView FirstNameTextView = findViewById(R.id.textViewFirstName);
        FirstNameTextView.setText(firstName);
        String lastName = caller.getStringExtra("idLastName");
        TextView LastNameTextView = findViewById(R.id.textViewLastName);
        LastNameTextView.setText(lastName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageView mImage = findViewById(R.id.profile_pic);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        String filePath = localFile.getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        mImage.setImageBitmap(bitmap);

        float sum = 0;

        for(int i = 0; i < 15; i++)
        {
            sum += generateRatingVal();
        }
        sum /= 15;
        setRateVal();
        setAvgRating(sum);

    }
    //End OnCreate

    private double generateRatingVal()
    {
        //generates a random float between 0 and 5 to display as a rating
        float minRating = (float)0.0;
        float maxRating = (float)5.0;
        Random rand = new Random();
        float randNum = minRating + rand.nextFloat() * (maxRating - minRating);
        return randNum;
    }

    private void setRateVal()
    {
        float minRating = (float)0.0;
        float maxRating = (float)5.0;
        Random rand = new Random();
        float randNum = minRating + rand.nextFloat() * (maxRating - minRating);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(randNum);
    }

    private void setAvgRating(float sum)
    {
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView avgRating = findViewById(R.id.avgRating);

        //float num = ratingBar.getRating();

        avgRating.setText(String.format("%.2f", sum));
    }

    //After choosing a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            ImageView imageView = findViewById(R.id.profile_pic);
            imageView.setImageURI(mImageUri);

            //Upload the image to FBase here...
            // Create a storage reference from our app
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference imageRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            UploadTask uploadTask = imageRef.putFile(mImageUri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata();
                    // ... MAybe get some data here?
                }
            });

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void goBack(View view)
    {
        //goto main scroll page

        Intent gotoMain = new Intent();
        gotoMain.setClass(this, MainContent.class);
        startActivity(gotoMain);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("MyTAg", "signInAnonymously:FAILURE", exception);
                    }
                });
    }
}
