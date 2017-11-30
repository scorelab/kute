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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Main;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Utils.NotificationActivity;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Trip;
import com.scorelab.kute.kute.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


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
            String messagebody;
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            try
            {
                JSONObject data=new JSONObject(remoteMessage.getData());
                processDataMessage(data);
                //imageurl=data.getString("image");
            }
            catch (Exception e)
            {
                Log.d("Json Exception",e.toString());
                messagebody=null;
                //imageurl=null;
            }
            //sendBigNotification(messagebody);
        }

        //***************** CHECK TO SEE IF THERE IS A NOTIFICATION *********************//

        if(remoteMessage.getNotification()!=null)

        {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intent = new Intent(this, Main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            sendBigNotification(remoteMessage.getNotification().getBody(),pendingIntent);
        }
    }

    /************************* Custom Functions****************/

    @TargetApi(16)
    private void sendBigNotification(String messageBody,PendingIntent pending_intent) {
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
                .setContentIntent(pending_intent)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notification);
    }



    //Process the received data message
    private void processDataMessage(JSONObject data){
        try {
            String notifType = data.getString("Message");
            if(notifType.equals("FoundRide")){
                //Case where we need to confirm from the user

                String owner=data.getString("Owner");
                boolean is_owner=getSharedPreferences("user_credentials", 0).getString("Id", null).equals(owner);
                if(is_owner){
                    generateFoundRideNotif(new com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Notification(owner,data.getString("Rider"),notifType,data.getString("Name"),data.getString("RiderStart"),data.getString("RiderDrop")));
                }else {
                    generateFoundRideNotif(new com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Notification(owner,data.getString("Rider"),notifType,data.getString("Name")));
                }
            }else if(notifType.equals("ConfirmedRide")){
                //Case where we declare ride is confirmed

                String owner=data.getString("Owner");
                String id=getSharedPreferences("user_credentials", 0).getString("Id", null);
                boolean is_owner=id.equals(owner);
                generateConfirmedRideNotif(data.getString("Name"),is_owner,id,data);
            }
        }catch (Exception e){
                    Log.d(TAG,"Exception in processDataMessage : "+e.toString());
                    }
    }

    //Method handling ride confirmation for owner
    private void confirmRideOwner(String firebase_id, final String rider_id){
        Log.d(TAG,"In Confirm Ride Owner");
        final DatabaseReference trip_ref=FirebaseDatabase.getInstance().getReference("Trips").child(firebase_id);
        trip_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trip t=dataSnapshot.getValue(Trip.class);
                if(t.getTravelling_with()!=null){
                    t.getTravelling_with().add(rider_id);
                }else{
                    ArrayList<String> temp=new ArrayList<String>();
                    temp.add(rider_id);
                    t.setTravelling_with(temp);
                }
                Log.d(TAG,"Adding new rider to trip");
                trip_ref.setValue(t).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failure in adding the rider to travelling with list");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    //Method handling ride confirmation for rider
    private void confirmRideRider(final String firebase_id, final String name){
        Log.d(TAG,"In Confirm Ride Rider");
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        final DatabaseReference temp_trip_ref=db.getReference("Temporarytrips");
        final DatabaseReference trip_ref=db.getReference("Trips");
        temp_trip_ref.child(firebase_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trip temp=dataSnapshot.getValue(Trip.class);
                temp.setOwner_string(name);
                Log.d(TAG,"Adding trip");
                trip_ref.child(firebase_id).setValue(temp).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"failed adding trip to Trips node");
                    }
                });
                temp_trip_ref.child(firebase_id).removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failure removing trip to temp trip_ref");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Method generating found ride notification
    private void generateFoundRideNotif(com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Notification not){
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("Notification",not);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT|
                PendingIntent.FLAG_ONE_SHOT);
        sendBigNotification("You found a match for your trip with your friend "+not.getOpposite_name()+"\n Tap to confirm",pendingIntent);
    }

    //Method Generating confirmed ride notification
    private void generateConfirmedRideNotif(String Name,boolean is_owner,String self_id,JSONObject data){
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        sendBigNotification("Your trip with "+Name+" is confirmed",pendingIntent);

        //Process the confirmations
        if(is_owner) {
            try {
                confirmRideOwner(self_id, data.getString("Rider"));
            }catch (Exception e){
                Log.d(TAG,"Json Exception "+e.toString());
            }
        }
        else
            try {
                confirmRideRider(self_id, data.getString("Name"));
            }catch (Exception e){
                Log.d(TAG,"Json Exception "+e.toString());
            }
    }

}
