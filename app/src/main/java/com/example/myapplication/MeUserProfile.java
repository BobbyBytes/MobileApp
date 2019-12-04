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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
//This is the user profile for the CURRENT user.. so the information here should be editable, via an edit button.
public class MeUserProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private Bitmap bitmap;
    private FirebaseUser User;
    private File localFile = null;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    ImageView mImage;
    TextView FirstNameTextView;
    TextView LastNameTextView;
    TextView BioTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         mImage = findViewById(R.id.me_profile_pic);
        // Initialize Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();

        Log.d("GETUSER TAG",User.getEmail());

        //Create storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userProfilePicRef = mStorageRef.child(User.getEmail() + ".jpg");

        //Create a local temporary file
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
                        ImageView mImage = findViewById(R.id.me_profile_pic);
                        mImage.setImageBitmap(bitmap);
                        Log.d("MeUserProfile", "Download image succeeded");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // TODO Set Default profile pic
                Log.d("MeUserProfile", "Downloading image failed");
            }
        });



        //Set content of this activity
        setContentView(R.layout.activity_me_user_profile);
        FirstNameTextView = findViewById(R.id.me_profile_textViewFirstName);
        LastNameTextView = findViewById(R.id.me_profile_textViewLastName);
        BioTextView = findViewById(R.id.me_bioTextView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

/*        //Set on click to open file chooser for a profile pic.
//        mImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openFileChooser(view);
////            }
////        });*/

        Intent caller = getIntent();
        boolean isArtist = caller.getBooleanExtra("IDisArtist", true);
        String dataBaseCollectionPath;
        if (isArtist){
            dataBaseCollectionPath = "users";
        }
        else dataBaseCollectionPath = "venues";

        DocumentReference docRef = FirebaseFirestore.getInstance().collection(dataBaseCollectionPath).document(User.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        UserData ud = task.getResult().toObject(UserData.class);
                        PopulateData(ud);
                    } else {
                        Log.d("Me USer Class", "No such document");
                    }
                } else {
                    Log.d("me user class", "get failed with ", task.getException());
                }
            }
        });




        //Set that floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                goInbox();
            }
        });
    }
    //End OnCreate

    private void PopulateData(UserData ud){
        String str = ud.getDisplayName();
        if (str != null){
            FirstNameTextView.setText(str);
        }
        str = ud.getGenre();
        if(str != null){
            LastNameTextView.setText(str);
        }
        str = ud.getbio();
        if (str != null){
            BioTextView.setText(str);
        }
    }
    //After choosing a picture from the file chooser.
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

    public void goInbox()
    {
        Intent gotoInbox = new Intent();
        gotoInbox.setClass(this, MessengerActivity.class);
        startActivity(gotoInbox);
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
