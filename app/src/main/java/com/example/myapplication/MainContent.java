package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainContent extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_scroll_view);
        List <UserData> mUserData = new ArrayList<>();
        Log.w("TAG: ","HOLYSHIT");
        // ...
        // Lookup the recyclerview in activity layout
        // Initialize ListOfUsers
        UserData U1 = new UserData("Hey","Joe","It's ME");
        mUserData.add(U1);

        U1 = new UserData("Firstie","LAstie","NickName");
        mUserData.add(U1);

        RecyclerView userListView = (RecyclerView)findViewById(R.id.userListView);


        // Create adapter passing in the sample user data
        UsersAdapter adapter = new UsersAdapter(mUserData);
        // Attach the adapter to the recyclerview to populate items
        // Set layout manager to position the items
        userListView.setLayoutManager(new LinearLayoutManager(this));
        userListView.setAdapter(adapter);

        // That's all!
        Log.w("TAG: ",mUserData.toString());

    }
}
