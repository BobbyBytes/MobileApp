package com.example.myapplication;

public class UserData {

     public UserData(){ }

     public UserData(String firstName, String lastName, String nickname){
         FirstName = firstName;
         LastName = lastName;
         Nickname = nickname;
     }
        private String FirstName;
        private  String LastName;
        private String Nickname;

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getNickname() {
        return Nickname;
    }
}