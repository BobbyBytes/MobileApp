package com.example.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Chat_Room extends AppCompatActivity {


    //Used this youtube tutorial for the chat_room class //https://www.youtube.com/watch?v=wVCz1a3ogqk

    //XML Vars
    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;
    //Vars
    private String user_name, room_name;
    //Realtime database root
    private DatabaseReference root;
    private String temp_key;
    private String chat_msg, chat_user_name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        btn_send_msg = (Button) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        chat_conversation = (TextView)findViewById(R.id.textView);

        //Get extras from previous activities
        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room -"+room_name);

        root = FirebaseDatabase.getInstance().getReference().child(room_name);


        //Button event that sends text msgs from the user.
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hash maps used to represent the structure and logic the database
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());
                message_root.updateChildren(map2);
            }
        });

        //Event Listener that adds messages to the database.
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

  //Method that adds text converstion
    private void append_chat_conversation(DataSnapshot dataSnapshot){
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            chat_conversation.append(chat_user_name + " : " +chat_msg + "\n");
        }

    }

}
