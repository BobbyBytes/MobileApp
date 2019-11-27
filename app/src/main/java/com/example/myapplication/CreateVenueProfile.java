package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class CreateVenueProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private FirebaseUser User;
    private FirebaseAuth mAuth;
    ImageView mImage;
    EditText mDisplayName;
    EditText mLoacation;
    EditText mVenueAbout;
    Button mVenueBtnUpload;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_venue_profile);
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mImage = findViewById(R.id.venueUploadPic);
        mDisplayName = findViewById(R.id.venueDisplayNameUpload);
        mLoacation = findViewById(R.id.venueLocationUpload);
        mVenueAbout = findViewById(R.id.venueAboutUpload);
        mVenueBtnUpload = findViewById(R.id.btnVenueCreateProfile);
        //Set on click to open file chooser for a profile pic.
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser(view);
            }
        });

        mVenueBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfile();
            }
        });
    }

    //After choosing a picture from the file chooser.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImage.setImageURI(mImageUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                    // ... Maybe get some data here?
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

    private void uploadProfile() {
        String eMailAddress = User.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String DisplayName;
        String Genre;
        String Bio;
        DisplayName = mDisplayName.getText().toString();
        Genre = mLoacation.getText().toString();
        Bio = mVenueAbout.getText().toString();
        UserData mUserArtist = new UserData(DisplayName, Genre, Bio);
        mUserArtist.setEmailAddress(eMailAddress);
        db.collection("venues").document(eMailAddress).set(mUserArtist);
        goToMainContentActivity();
    }

    private void goToMainContentActivity(){
        Intent intent = new Intent();
        intent.setClass(this, MainContent.class);
        intent.putExtra("idIsArtist", false);
        startActivity(intent);
    }

}

