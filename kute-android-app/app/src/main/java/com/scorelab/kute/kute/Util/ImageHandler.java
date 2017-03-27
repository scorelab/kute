package com.scorelab.kute.kute.Util;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by nrv on 2/5/17.
 */

public class ImageHandler {

    public static String imagesaved_prefrence_key="imagePreferance";
    public static String MainKey="KutePrefrence";


    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static void saveImageToprefrence(SharedPreferences sharedPreferences,Bitmap img){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(imagesaved_prefrence_key, encodeTobase64(img));
        editor.commit();
    }

    public static Bitmap getUserImage(SharedPreferences shared){
        //SharedPreferences shared = getSharedPreferences("MyApp_Settings", MODE_PRIVATE);
        String photo = shared.getString(imagesaved_prefrence_key,null);
        if(photo!=null){
            return decodeBase64(photo);
        }
        return null; //ToDo change to work with default image

    }



}
