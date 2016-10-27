package com.rubabuddin.simplechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rubabuddin.simplechat.adapters.ChatListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    EditText etMessage;
    Button btSend;

    ListView lvChat;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;

    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //User Login
        if (ParseUser.getCurrentUser() != null) { //start with existing user
            startWithCurrentUser();
        } else { //if not logged in login as an anonymous user
            login();
        }
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;

        final String userId = ParseUser.getCurrentUser().getObjectId();

        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);
        lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                //ParseObject message = ParseObject.create("Message");
                //message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
                //message.put(BODY_KEY, data);
                Message message = new Message();
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();
                            refreshMessages();
                        } else {
                            Log.e("ERROR", "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    void refreshMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>(){
            public void done (List< Message > messages, ParseException e){
            if (e == null) {
                mMessages.clear();
                Collections.reverse(messages);
                mMessages.addAll(messages);
                mAdapter.notifyDataSetChanged(); // update adapter
                // Scroll to the bottom of the list on initial load
                if (mFirstLoad) {
                    lvChat.setSelection(mAdapter.getCount() - 1);
                    mFirstLoad = false;
                }
            } else {
                Log.e("message", "Error Loading Messages" + e);
            }
        }
        });
    }

    void login(){
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e("ERROR", "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }
}

