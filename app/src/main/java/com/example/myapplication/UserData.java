package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class UserData {

    public UserData() {
    }

    public UserData(String firstName, String lastName, String nickname) {
        FirstName = firstName;
        LastName = lastName;
        Nickname = nickname;
    }

    private String FirstName;
    private String LastName;
    private String Nickname;
    private String Description;
    private String ImagePath;
    private String EmailAddress;
    File localFile = null;
    private Uri mImageUri;
    private Bitmap mBitmap;
    private StorageReference mStorageRef;
    private Bitmap bitmap;
    private Boolean isWorking;
    private Boolean dataRetrevialSuccess;
    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getNickname() {
        return Nickname;
    }
    public String getEmailAddress(){
        return EmailAddress;
    }
    public String getImagePath() {
        return ImagePath;
    }

    public String getDescription() {
        return Description;
    }

    public void setfirstName(String firstName) {
        FirstName = firstName;
    }

    public void setEmailAddress(String emailAddress){
        EmailAddress = emailAddress;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setnickname(String nickname) {
        Nickname = nickname;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }



    public synchronized Bitmap waitToGetBitmap() throws InterruptedException {

        while (isWorking = true){
            wait(100);
        }

        return bitmap;
    }
}
