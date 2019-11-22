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
    private Bitmap mBitmap;
    private boolean isArtist;
    private double averageRating;
    private long numRatings;


    //Theese methods must remain public for firebase to identify them and add them to the database.
    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getNickname() {
        return Nickname;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public String getDescription() {
        return Description;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public boolean getIsArtist() {
        return isArtist;
    }

    public double getAvgRating(){
        return averageRating;
    }

    public long getNumRatings(){
        return numRatings;
    }
    //Setters
    public void setfirstName(String firstName) {
        FirstName = firstName;
    }

    public void setEmailAddress(String emailAddress) {
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

    public void setmBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public void setIsArtist(boolean Artist) {
        isArtist = Artist;
    }

    public void AddRating(double rating){


    }

}
