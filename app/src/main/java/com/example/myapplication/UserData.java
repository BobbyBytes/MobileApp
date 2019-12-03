package com.example.myapplication;

import android.graphics.Bitmap;
import android.location.Location;
import java.io.File;

public class UserData {

    public UserData() {
    }

    public UserData(String DisplayNameIn, String genre, String bio) {
        DisplayName = DisplayNameIn;
        Genre = genre;
        Bio = bio;
    }

    private String DisplayName;
    private String Genre;
    private String Bio;
    private String FirstName;
    private String LastName;
    private String Nickname;
    private String Description;
    private String ImagePath;
    private String EmailAddress;
    File localFile = null;
    private boolean isArtist;
    private double sumOfAllRatings;
    private int NumRatings;
    private double AvgRating;
    private Location location;
    private String ImageString;
    private String locationString;



    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }


    public String getImageString() {
        return ImageString;
    }

    public void setImageString(String imageString) {
        ImageString = imageString;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

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

    public boolean getisArtist() {
        return isArtist;
    }

    public double getRatingSum(){
        return sumOfAllRatings;
    }

    public int getNumRatings(){
        return NumRatings;
    }

    public double getAvgRating() {
        return AvgRating;
    }

    public String getDisplayName(){ return DisplayName;}

    public String getGenre(){ return Genre;}

    public  String getbio() {return Bio;}

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

    public void setNumRatings(int numRatings) {
        NumRatings = numRatings;
    }

    public void setRatingSum (double ratingSum) {
        sumOfAllRatings = ratingSum;
    }

    public void setAvgRating(double avgRating) {
        AvgRating = avgRating;
    }

    public void setIsArtist(boolean Artist) {
        isArtist = Artist;
    }

    public void setDisplayName(String displayName) {DisplayName= displayName;}

    public void setGenre(String genre){Genre = genre;}

    public void setBio(String bio){Bio = bio;}

    public void AddRating(double rating){
    }

}
