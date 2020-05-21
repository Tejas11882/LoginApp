package com.example.blogapp.Activities;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blogapp.R;

import java.net.URI;

import javax.xml.transform.Result;

public class RegisterActivites extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int  PReqCode=1;
    static int  REQUESCODE=1;
    Uri pickedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    //inu Views
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
