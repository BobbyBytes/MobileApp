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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class otherUserProfile extends AppCompatActivity {

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

        // Initialize FireBase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        //Get the current FireBase User
        User = mAuth.getCurrentUser();
        Log.d("GETUSER TAG",User.getEmail());
        Intent caller = getIntent();
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
                // ...
                Log.d("MyTAg", "Downloading image failed");
                //Set a default profile pic

            }
        });

        //Set content of this activity
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView mImage = findViewById(R.id.profile_pic);


        //Grab the name string from the calling intent

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


        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(v);
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
