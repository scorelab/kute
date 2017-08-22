package com.scorelab.kute.kute.PrivateVehicles.App.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;



/**
 * Created by nipunarora on 21/08/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG="FirebaseMessagingSrvc";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //************ CHECK TO SEE IF THERE IS A DATA MESSAGE *******************
        if(remoteMessage.getData().size() >0)

        {
            String messagebody,imageurl;
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            try
            {
                JSONObject data=new JSONObject(remoteMessage.getData());
                messagebody=data.getString("Message");
                //imageurl=data.getString("image");
            }
            catch (Exception e)
            {
                Log.d("Json Exception",e.toString());
                messagebody=null;
                //imageurl=null;
            }
            sendBigNotification(messagebody);
        }

        //***************** CHECK TO SEE IF THERE IS A NOTIFICATION *********************//

        if(remoteMessage.getNotification()!=null)

        {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendBigNotification(remoteMessage.getNotification().getBody());
        }
    }

    @TargetApi(16)
    private void sendBigNotification(String messageBody) {
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Log.d(TAG,"The message body is :"+messageBody);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_car_white_24dp);
        Bitmap image= BitmapFactory.decodeResource(getResources(), R.drawable.auto);
         Notification notification = new NotificationCompat.Builder(this)
                 .setStyle(new NotificationCompat.BigTextStyle()
                         .setBigContentTitle("KUTE")
                .bigText(messageBody))
                 .setSmallIcon(R.drawable.ic_people_black_24dp)
                .setLargeIcon(Icon)
                 .setContentTitle("KUTE")
                 .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notification);
    }

}
