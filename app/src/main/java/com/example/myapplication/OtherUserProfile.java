package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class OtherUserProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private Bitmap bitmap;
    private FirebaseUser User;
    private File localFile = null;
    private Uri mImageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Connect to firebase

        // Initialize FireBase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        //Get the current FireBase User
        User = mAuth.getCurrentUser();
        Log.d("GETUSER TAG",User.getEmail());
        final Intent caller = getIntent();
        String eMailAddr = caller.getStringExtra("idEmail");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userProfilePicRef = mStorageRef.child(eMailAddr + ".jpg");

        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        userProfilePicRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // Now set the image(bitmap) to the view
                        String filePath = localFile.getAbsolutePath();
                        bitmap = BitmapFactory.decodeFile(filePath);
                        ImageView mImage = findViewById(R.id.profile_pic);
                        mImage.setImageBitmap(bitmap);
                        Log.d("MyTAg", "Holy shit it works");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // TODO Create Default User Profile Pic
                Log.d("MyTAg", "Downloading image failed");

            }
        });

        //Set content of this activity
        setContentView(R.layout.activity_other_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView mImage = findViewById(R.id.profile_pic);

        //Grab the name string from the calling intent

        String DisplayName = caller.getStringExtra("idDisplayName");
        TextView FirstNameTextView = findViewById(R.id.textViewFirstName);
        FirstNameTextView.setText(DisplayName);
        String genre = caller.getStringExtra("idGenre");
        TextView LastNameTextView = findViewById(R.id.textViewLastName);
        LastNameTextView.setText(genre);
        String bio = caller.getStringExtra("idBio");
        TextView bioTextview = findViewById(R.id.bioTextView);
        LastNameTextView.setText(bio);





        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //This sets the avg rating to the currently clicked value
        //Need to make this do stuff with values from the database using various getters and setters
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                double ratingSum = caller.getDoubleExtra("idSum", 0.0);
                int numRatings = caller.getIntExtra("idNumOfRates", 1);
                String emailAddr = caller.getStringExtra("idEmail");
                setAvg(rating, ratingSum, numRatings, emailAddr);

            }
        });

    }
    //End OnCreate

    public void setAvg(float rating, double ratingSum, int numVotes, String emailAddr)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final TextView avgRatingVal = findViewById(R.id.avgRatingVal);

        numVotes++;
        ratingSum += rating;

        double avg = ratingSum / numVotes;

        avgRatingVal.setText(String.valueOf(avg));

        DocumentReference sum = db.collection("users").document(emailAddr);
        DocumentReference voteCount = db.collection("users").document(emailAddr);

        sum.update("avgRating", ratingSum);
        voteCount.update("numRatings", numVotes);


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

            StorageReference imageRef = storageRef.child(User.getEmail() + "." + getFileExtension(mImageUri));
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

    public void openFileChooser(View view) {
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
}
