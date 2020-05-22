package com.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import javax.xml.transform.Result;

public class RegisterActivites extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int  PReqCode=1;
    static int  REQUESCODE=1;
    Uri pickedImageUri;

    private EditText userEmail,userPassword,userPassword2,userName;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    //inu Views

    userEmail =findViewById(R.id.regMail);
    userPassword = findViewById(R.id.regPassword);
    userPassword2 = findViewById(R.id.regPassword2);
    userName = findViewById(R.id.regName);
    loadingProgress = findViewById(R.id.progressBar);
    regBtn = findViewById(R.id.regButton);
    loadingProgress.setVisibility(View.INVISIBLE);

    mAuth=FirebaseAuth.getInstance();

    regBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            regBtn.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);
            final String email= userEmail.getText().toString();
            final String password= userPassword.getText().toString();
            final String password2=userPassword2.getText().toString();
            final String name=userName.getText().toString();

            if(email.isEmpty() || name.isEmpty() || password.isEmpty() ||  ! password.equals(password2)){


                //Error messeges displayed
                showMessege("pleas verify all fields");
                regBtn.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(view.INVISIBLE);
            }
            else{
                //all data is valied then created account user
                //create acount method try to create to email is valied


                CreateUserAccount(email,name,password);



            }









        }
    });

        ImgUserPhoto=findViewById(R.id.regUserPhoto);
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>=22)
                {
                    checkAndRquestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });


    }

    private void CreateUserAccount(String email, final String name, String password) {
        //this create account with specific email and password
    mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //user account create successfully
                        showMessege("Account Created");
                        //after we created user account we needed profile pic
                        updateUserInfo(name,pickedImageUri,mAuth.getCurrentUser());

                    }
                    else
                    {
                        showMessege("Account Creation Failed"+task.getException().getMessage());
                        regBtn.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
//user profle update name and photo
    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {
        //first we need upload user photo
        StorageReference mStorege = FirebaseStorage .getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorege.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded succesfully
                //now we can get out image url
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //user info update succesfully
                                            showMessege("Register Complete");
                                            updateUI();
                                        }
                                        else {

                                        }


                                    }


                                });
                    }
                });
            }
        });
    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    //simple msg show Tost meg
    private void showMessege(String messege) {

        Toast.makeText(getApplicationContext(), messege,Toast.LENGTH_LONG).show();


    }

    private void openGallery() {

        //ToDo: Open Gallery intent and wait for user to pick and image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);

    }

    private void checkAndRquestForPermission() {

        if(ContextCompat.checkSelfPermission(RegisterActivites.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivites.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                Toast.makeText(RegisterActivites.this, "Please accept for required permission ",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(RegisterActivites.this,new  String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                                       ,PReqCode);
            }
        }
        else
            openGallery();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == RESULT_OK && requestCode==REQUESCODE && data !=null){
            //the user has successefully picked an images
            //we need to save uri

        pickedImageUri=data.getData();
        ImgUserPhoto.setImageURI(pickedImageUri);
        }

    }
}
