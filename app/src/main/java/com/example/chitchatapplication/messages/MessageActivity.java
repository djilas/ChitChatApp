package com.example.chitchatapplication.messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chitchatapplication.ChatActivity;
import com.example.chitchatapplication.R;
import com.example.chitchatapplication.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText edtMessage;
    private TextView txtFriend;
    private ProgressBar progressBar;
    private ImageView imgFriend, imgSend;
    private Button btnBack;

    private MessageAdapter messageAdapter;

    private ArrayList<Message> messages;

    String usernameOfFriend, myPhoto, emailOfFriend, photoOfFriend, chatId;

    private Uri imagePath;

    private final int CAMERA_REQUIRE_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        usernameOfFriend = getIntent().getStringExtra("username_of_friend");
        emailOfFriend = getIntent().getStringExtra("email_of_friend");
        photoOfFriend = getIntent().getStringExtra("photo_of_friend");
        myPhoto = getIntent().getStringExtra("my_photo");

        recyclerView = findViewById(R.id.recyclerMessages);
        edtMessage = findViewById(R.id.edtTex);
        txtFriend = findViewById(R.id.txtFriend);
        progressBar = findViewById(R.id.progressMessages);
        imgFriend = findViewById(R.id.img_toolbar);
        imgSend = findViewById(R.id.sendMessage);

        btnBack = findViewById(R.id.btnBack);

        //not working
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("messages/" + chatId).push().setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(), emailOfFriend, edtMessage.getText().toString()));
                edtMessage.setText("");
            }
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(MessageActivity.this, ChatActivity.class));
        });

        txtFriend.setText(usernameOfFriend);
        if(!photoOfFriend.isEmpty()){
            Picasso.get().load((photoOfFriend)).into((imgFriend));
        }else{
            Picasso.get().load(photoOfFriend).error(R.drawable.account_image).placeholder(R.drawable.account_image);
        }

        setUpChatRoom();

        messages = new ArrayList<>();
        getMessages(photoOfFriend, myPhoto);
    }

    public void setUpChatRoom(){
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUsername = snapshot.getValue(User.class).getUsername();
                if(myUsername.compareTo(usernameOfFriend) <= 0){
                    chatId = myUsername + usernameOfFriend;
                }else {
                    chatId = usernameOfFriend + myUsername;
                }
                attachMessageListener(chatId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void  attachMessageListener(String chatId){
        FirebaseDatabase.getInstance().getReference("messages/" + chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(Message.class));
                }

                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1); // Automatically scroll to button when the new message arrive
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    //Fetch messages
    public void getMessages(String senderImg, String recieverImg){
        messages.clear();
        FirebaseDatabase.getInstance().getReference("messages/" + chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        messages.add(dataSnapshot.getValue(Message.class));
                }

                messageAdapter = new MessageAdapter(messages, senderImg, recieverImg,MessageActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                recyclerView.setAdapter(messageAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
