package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    //View holder Definition
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView DisplayNameTextView;
        public TextView GenreTextView;
        public TextView BioTextView;
        public ImageView profilePicView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            DisplayNameTextView = itemView.findViewById(R.id.myTextView1);
            GenreTextView = itemView.findViewById((R.id.textView2));
            BioTextView = itemView.findViewById(R.id.textView3);
            profilePicView = itemView.findViewById(R.id.MyProfilePic);
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
        final UserData myUser = mUserData.get(position);

        // Set item views based on your views and data model
        TextView DisplayNameTextView = viewHolder.DisplayNameTextView;
        TextView GenreTextView = viewHolder.GenreTextView;
        TextView BioTextView = viewHolder.BioTextView;
        ImageView picImageView = viewHolder.profilePicView;

        DisplayNameTextView.setText(myUser.getDisplayName());
        GenreTextView.setText((myUser.getGenre()));
        BioTextView.setText((myUser.getbio()));
        Bitmap bitmap = myUser.getmBitmap();

        picImageView.setImageBitmap(bitmap);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mUserData.size();
    }

}
