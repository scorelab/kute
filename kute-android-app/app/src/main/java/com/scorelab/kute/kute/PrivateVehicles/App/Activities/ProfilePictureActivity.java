package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ProfilePictureActivity extends AppCompatActivity implements View.OnClickListener {
    Button rotate,edit,update;
    ImageButton back;
    RoundedImageView profile_image;
    SharedPreferences pref;
    private int PICK_IMAGE_REQUEST = 1;
    Uri picUri;
    Bitmap current_image;
    CoordinatorLayout cl;
    String yourBase64String;
    String TAG ="ProfilePictureActivity";

    Boolean did_profile_image_change=false;
    final int PROFILE_PICTURE_ACTIVITY_CODE=01;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_picture_activity);
        rotate=(Button)findViewById(R.id.rotate);
        edit=(Button)findViewById(R.id.edit);
        update=(Button)findViewById(R.id.update);
        back=(ImageButton)findViewById(R.id.backNav);
        profile_image=(RoundedImageView)findViewById(R.id.profileImage);
        cl=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        picUri=getCaptureImageOutputUri();
        pref=getApplicationContext().getSharedPreferences("user_credentials",0);
        //Setting Up the Current Image
        //Move Profile Picture logic to async loader or asynctask if possible keeping in mind in image loader is already lazy
        setupProfileImage();
        rotate.setOnClickListener(this);
        edit.setOnClickListener(this);
        update.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.edit:
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

                    askForPermission(0,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA});
                }
                else {
                    getNewProfileImage();
                }
                break;
            case R.id.rotate:
                current_image=rotateImage(current_image,90);
                profile_image.setImageBitmap(current_image);
                break;
            case R.id.update:
                String base_64=convertToBase64(current_image);
                did_profile_image_change=true;
                saveNewImageToDb(base_64);
                break;
            case R.id.backNav:
                Intent i=new Intent();
                i.putExtra("DidProfileChange",did_profile_image_change);
                if(did_profile_image_change) {
                    i.putExtra("ImageBase64String",yourBase64String);
                }
                setResult(PROFILE_PICTURE_ACTIVITY_CODE,i);
                finish();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK )
        {
            rotate.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
            Log.d("Check","we entered");
            Uri image_uri;
            if(data!=null && data.getData()!=null)
            {
                image_uri=data.getData();
            }
            else {
                image_uri=picUri;
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                bitmap=getResizedBitmap(bitmap,220);
                current_image=bitmap;
                profile_image.setImageBitmap(bitmap);
            }catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0 && ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED ) {
            if(permissions.length>1 && ActivityCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "We Got External Storage and Camera", Toast.LENGTH_SHORT).show();
                getNewProfileImage();
            }
            else
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
                //User just granted one of the denied permissions
                getNewProfileImage();
            }
            else {
                Snackbar.make(cl,"You need to give permissions to setup custom profile picture",Snackbar.LENGTH_LONG).show();
            }
        }
        else if(requestCode==0) {
            Snackbar.make(cl,"You need to give permissions to setup custom profile picture",Snackbar.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        if(picUri!=null) {
            outState.putParcelable("pic_uri", picUri);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        if(savedInstanceState.getParcelable("pic_uri")!=null) {
            picUri = savedInstanceState.getParcelable("pic_uri");
        }

    }
    /******************** Custom Functions *************************/
    private void setupProfileImage()
    {

        String Base64=pref.getString("Profile_Image",null);
        if(!(pref.getString("Profile_Image",null).equals("null"))){
            //Get the Image from base64 string stored in the prefs
            //This is the case when the user has a custom profile image
            Log.d(TAG,"User has custom profile image");
            base64ToBitmap(Base64);
        }
        else {
            String img_url = String.format("https://graph.facebook.com/%s/picture?type=large", pref.getString("Id", "null"));
            Log.d(TAG,"Image Url for ImageLoader is"+img_url);
            ImageLoader mImageLoader = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();
            mImageLoader.get(img_url, ImageLoader.getImageListener(profile_image,
                    R.drawable.ic_person_black_36dp, R.drawable.ic_person_black_36dp));
        }
    }
    private void askForPermission(final Integer requestCode,final String... permission) {
        Boolean did_user_deny_external= ActivityCompat.shouldShowRequestPermissionRationale(ProfilePictureActivity.this, permission[0]);
        Boolean did_user_deny_camera=ActivityCompat.shouldShowRequestPermissionRationale(ProfilePictureActivity.this, permission[1]);
        if (ContextCompat.checkSelfPermission(ProfilePictureActivity.this, permission[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ProfilePictureActivity.this, permission[1]) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            getNewProfileImage();

        } else {
            //The if condition below would work if the user has denied one particular permission or both
            if (did_user_deny_camera && did_user_deny_external) {
                //When user denied access to both the camera and external storage
                setupPermissionDialog(requestCode, "Allow access to camera and external storage to add custom profile picture", permission);
            }
            else if (did_user_deny_camera || did_user_deny_external)
            {
                if(did_user_deny_camera)
                {
                    setupPermissionDialog(requestCode,"We need camera to setup custom profile image",permission[1]);
                }
                else {
                    setupPermissionDialog(requestCode,"We need external storage to setup custom profile image",permission[0]);
                }
            }
            else {
                setupPermissionDialog(requestCode,"Allow access to camera and external storage to add custom profile picture", permission);
            }

        }
    }

    private void getNewProfileImage()
    {
        Intent gallery_intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gallery_intent.setType("image/*");
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        final Intent chooserIntent = Intent.createChooser(gallery_intent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { captureIntent });
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private void setupPermissionDialog(final Integer requestCode,String message,final String... permission)
    {
        showMessageOKCancel(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ProfilePictureActivity.this, permission, requestCode);
                    }
                });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ProfilePictureActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private String convertToBase64(Bitmap bitmap)
    {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            yourBase64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("The Base64 is",yourBase64String);
            return yourBase64String;

        }catch (Exception e)
        {
            Log.d("convertToBase64",e.toString());
            return null;
        }
    }
    //A function to resize Bitmap to the corresponding size
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    private void base64ToBitmap(String base64)
    {
        byte []imageBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profile_image.setImageBitmap(decodedImage);
        //imageView.setImageBitmap(decodedImage);
    }
    private void saveNewImageToDb(String base_64)
    {
        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference("Users").child(pref.getString("Id","null")).child("img_base64");
        db_ref.setValue(base_64).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Update profile Image Firebase:"+e.toString());
            }
        });
        //Change the Shared Preference Value
        pref.edit().putString("Profile_Image",base_64).apply();
        Log.d(TAG,"Update Profile Image:Done");
    }

}
