package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;        //Needed to update the build gradle for this libary to work:
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreateArtistProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private FirebaseUser User;
    private FirebaseAuth mAuth;
    ImageView mImage;
    EditText mDisplayName;
    EditText mGenre;
    EditText mBio;
    Button mBtnUpload;
    Bitmap bitmap;
    private double longitude;
    private double latittude;
    //Reused class vars from Maps activity
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLOcationPermissionGranted = false;
    public String temp;
    public String temp1;
    private static final String TAG = "createprofile";
    //Class Vars

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLocationPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_artist_profile);
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        mImage = findViewById(R.id.artistUploadPic);
        mDisplayName = findViewById(R.id.artistDisplayNameUpload);
        mGenre = findViewById(R.id.artistGenreUpload);
        mBio = findViewById(R.id.artistBioUpload);
        mBtnUpload = findViewById(R.id.btnCreateProfile);

        ////brute force getting the location.
        for(int i = 0; i < 10; i++) {
            temp1 = get_addr_String_wrapper();
        }

        //Set on click to open file chooser for a profile pic.
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser(view);
            }
        });


        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfile();
            }
        });

    }


//Source used to get location permission from users. https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    private void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLOcationPermissionGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
//Source used to get location permission from users. https://www.youtube.com/watch?v=Vt6H9TOmsuo&list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt&index=4
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLOcationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
                            mLOcationPermissionGranted = false;
                        return;
                    }
                }
                mLOcationPermissionGranted = true;
            }
        }
    }



    //Modified method from the maps activity. This method gets permission before a string with
    //the users information
    public String get_addr_String_wrapper(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLOcationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location curentLocation = (Location) task.getResult();
                            temp = getCompleteAddressString(curentLocation.getLatitude(), curentLocation.getLongitude());
                            latittude = curentLocation.getLatitude();
                            longitude = curentLocation.getLongitude();

                        } else {
                            Log.d(TAG, "onComp  lete: current location is null");
                            Toast.makeText(CreateArtistProfile.this, "unbal to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
        return temp;

    }


    //https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
   //
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                String city = addresses.get(0).getLocality();
                strReturnedAddress.append(city).append("\n");
                String state = addresses.get(0).getAdminArea();
                strReturnedAddress.append(state).append("\n");

                /*
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                */

                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
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
        Genre = mGenre.getText().toString();
        Bio = mBio.getText().toString();
        UserData mUserArtist = new UserData(DisplayName, Genre, Bio);
        mUserArtist.setLongitude(longitude);
        mUserArtist.setLatitude(latittude);
        mUserArtist.setEmailAddress(eMailAddress);
        mUserArtist.setIsArtist(true);
        mUserArtist.setLocationString(get_addr_String_wrapper());
        db.collection("users").document(eMailAddress).set(mUserArtist);
        goToMainContentActivity(mUserArtist);

    }

    private void goToMainContentActivity(UserData user){
        Intent intent = new Intent();
        intent.setClass(this, MainContent.class);
        intent.putExtra("idIsArtist", true);
        startActivity(intent);
    }

}

