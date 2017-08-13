package com.davitmartirosyan.exp.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.davitmartirosyan.exp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Activity2 extends Activity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static int counter = 2;
    private static final int REQUEST_CODE = 0;

    private ImageView mphoto;
    private ImageButton takePhoto;
    private Button upload;

    private ProgressDialog progressDialog;

    private Uri mCapturedImageURI;
    private Uri downloadUrl;

    private StorageReference mStorageRef;

    private String capturedImageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        takePhoto = (ImageButton) findViewById(R.id.activity_2_imagebtn_ib);
        mphoto = (ImageView) findViewById(R.id.activity_2_photo_iv);
        upload = (Button)findViewById(R.id.activity_2_upload_btn);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            takePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(this,new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CODE);

        }

        takePhoto.setOnClickListener(this);
        upload.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.activity_2_imagebtn_ib:

                String fileName = "photo" + counter + ".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                break;

            case R.id.activity_2_upload_btn:
                if(capturedImageFilePath!=null) {
                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
                    Uri file = Uri.fromFile(new File(capturedImageFilePath));
                    StorageReference riversRef = mStorageRef.child(capturedImageFilePath.substring(20));
                    riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadUrl = taskSnapshot.getDownloadUrl();
                            mphoto.setImageResource(android.R.color.transparent);
                            progressDialog.dismiss();
                            Log.d("testt", downloadUrl.toString());
                            Toast.makeText(Activity2.this, "Upload Successful! :)", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Activity2.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                    capturedImageFilePath=null;
                }
                else {
                    Toast.makeText(Activity2.this,"Please take a picture",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_TAKE_PHOTO) {
            int column_index_data;
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null);
            if (cursor.moveToFirst() && !cursor.isClosed()) {
                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                capturedImageFilePath = cursor.getString(column_index_data);
            }
            cursor.close();
            Glide.with(this).load(capturedImageFilePath).fitCenter().into(mphoto);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto.setEnabled(true);
            }
        }
    }
}
