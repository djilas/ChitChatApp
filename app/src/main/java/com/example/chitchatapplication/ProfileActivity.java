package com.example.chitchatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chitchatapplication.messages.MessageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private final int CAMERA_REQUIRE_CODE = 1;

    private Button  btnLogOut, btnUploadImage, btnBack, btnDelete;
    private TextView txtUsername;
    private ImageView imgProfile;

    private Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogOut = findViewById(R.id.btmLogOut);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnBack = findViewById(R.id.btnBackProfile);
        btnDelete = findViewById(R.id.btnDeletePhoto);
        imgProfile = findViewById(R.id.profile_img);
        txtUsername = findViewById(R.id.usernameInfo);

        btnUploadImage.setEnabled(false);

        // here how to check if it is empty or null ??? the app crashes
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.getValue(String.class);
                if(link.isEmpty()){
                    imgProfile.setImageResource(R.drawable.account_image);
                    btnDelete.setEnabled(false);
                }else{
                    btnDelete.setEnabled(true);
                    Picasso.get().load(link).into(imgProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error loading image", Toast.LENGTH_SHORT);
            }
        });

        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/username").get().addOnCompleteListener(e -> {
            if (e.isSuccessful()) {
                txtUsername.setText(e.getResult().getValue().toString());
            }
        });

        btnUploadImage.setOnClickListener(view -> {
            uploadImage();
        });

        /*btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                //In this case we want to set switch activity to MainActivity and we want to clear everything so the user cannot go back
                startActivity(new Intent(ProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });*/

        btnDelete.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue("");
            btnDelete.setEnabled(false);
            imgProfile.setImageResource(R.drawable.account_image);
        });

        btnLogOut.setOnClickListener( view -> {
            FirebaseAuth.getInstance().signOut();
            //In this case we want to set switch activity to MainActivity and we want to clear everything so the user cannot go back
            startActivity(new Intent(ProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            //I don't know why but this is not working???
            finish();
        });

        imgProfile.setOnClickListener(view -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK);
            photoIntent.setType("image/+");
            startActivityForResult(photoIntent, CAMERA_REQUIRE_CODE);
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, ChatActivity.class));

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUIRE_CODE && resultCode == RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    public void getImageInImageView() {
        Bitmap img = null;
        try {
            img = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgProfile.setImageBitmap(img);
        btnUploadImage.setEnabled(true);
    }

    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.uploading);
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) { //task contains donwload url - image that was uploaded previously
                            if(task.isSuccessful()){
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    Toast.makeText(ProfileActivity.this, R.string.imageUploaded, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                btnDelete.setEnabled(true);
                btnUploadImage.setEnabled(false);
            }
        });

        //NOT WORKING...
       /* .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }
        });*/
    }

    private void updateProfilePicture (String url){
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue(url);
    }
}
