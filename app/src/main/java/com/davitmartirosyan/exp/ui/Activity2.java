package com.davitmartirosyan.exp.ui;

import android.Manifest;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.davitmartirosyan.exp.R;
import com.davitmartirosyan.exp.util.AppUtil;
import com.davitmartirosyan.exp.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Activity2 extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_PICK_IMAGE = 2;

    private static int counter = 10;

    private ImageView mphoto;
    private ImageButton takePhoto;
    private Button upload;
    private Button fromGallery;

    private ProgressDialog progressDialog;

    private Uri mCapturedImageURI;
    private Uri downloadUrl;

    private String mImageGalleryPath;
    private String capturedImageFilePath;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        takePhoto = (ImageButton) findViewById(R.id.activity_2_imagebtn_ib);
        mphoto = (ImageView) findViewById(R.id.activity_2_photo_iv);
        upload = (Button)findViewById(R.id.activity_2_upload_btn);
        fromGallery = (Button)findViewById(R.id.activity_2_take_from_gallery_btn);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            takePhoto.setEnabled(false);
            ActivityCompat.requestPermissions(this,new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CODE);

        }

        takePhoto.setOnClickListener(this);
        upload.setOnClickListener(this);
        fromGallery.setOnClickListener(this);

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

            case R.id.activity_2_take_from_gallery_btn:
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1,"Select Picture"),REQUEST_PICK_IMAGE);
                break;

            case R.id.activity_2_upload_btn:
                if(capturedImageFilePath!=null) {
                    imageUpload(capturedImageFilePath);
                    capturedImageFilePath = null;
                }
                else if (mImageGalleryPath!=null) {
                    imageUpload(mImageGalleryPath);
                    mImageGalleryPath = null;
                }
                else {
                    Toast.makeText(this,"Please take a picture",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    capturedImageFilePath = getPath(mCapturedImageURI);
                    Log.d("testt",capturedImageFilePath);
                    Glide.with(this).load(capturedImageFilePath).into(mphoto);
                    break;

                case REQUEST_PICK_IMAGE:
                    Uri mTakeImageGalleryURI = data.getData();
                    mImageGalleryPath = getPath(mTakeImageGalleryURI);
                    Log.d("testt",mImageGalleryPath);
                    Glide.with(this).load(mImageGalleryPath).into(mphoto);
                    break;
            }
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

    public String getPath(Uri uri) {
        if( uri == null ) {
            Log.d("testt","Something went wrong!");
            return null;
        }
        int column_index_data;
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor.moveToFirst() && !cursor.isClosed() ){
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(column_index_data);
            cursor.close();
            return path;
        }
        return path;
    }

    public void imageUpload(String path) {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        Uri file = Uri.fromFile(new File(path));
        StorageReference riversRef = mStorageRef.child(path.substring(20));
        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            downloadUrl = taskSnapshot.getDownloadUrl();
            mphoto.setImageResource(android.R.color.transparent);
            progressDialog.dismiss();
            Log.d("testt", downloadUrl.toString());
            Toast.makeText(Activity2.this, "Upload Successful! :)", Toast.LENGTH_SHORT).show();
            AppUtil.sendNotification(
                    getApplicationContext(),
                    Activity2.class,
                    "Cymetry Exp",
                    "You have successfuly submited a photo",
                    "",
                    Constant.NotifType.SUBMIT);
            }
        }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity2.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
