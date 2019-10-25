package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    //Viewholder Definition
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView firstNameTextView;
        public TextView lastNameTextView;
        public TextView nickNameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            firstNameTextView = (TextView) itemView.findViewById(R.id.myTextView1);
            lastNameTextView = (TextView) itemView.findViewById((R.id.textView2));
            nickNameTextView = (TextView) itemView.findViewById(R.id.textView3);
        }
    }


    // Store a member variable for the contacts
    private List<UserData> mUserData;
    // Pass in the contact array into the constructor
    public UsersAdapter(List<UserData> userdata) {
        mUserData = userdata;
    }
    //On creation of viewholder
        @Override
        public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View UserView = inflater.inflate(R.layout.recycler_view_item, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(UserView);
            return viewHolder;
        }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        UserData myUser = mUserData.get(position);

        // Set item views based on your views and data model
        TextView firstNameTextView = viewHolder.firstNameTextView;
        TextView lastNameTextView = viewHolder.lastNameTextView;
        TextView nickNameTextView = viewHolder.nickNameTextView;

        firstNameTextView.setText(myUser.getFirstName());
        lastNameTextView.setText((myUser.getLastName()));
        nickNameTextView.setText((myUser.getNickname()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mUserData.size();
    }

}
