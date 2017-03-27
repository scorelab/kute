package com.scorelab.kute.kute.Util;

/**
 * Created by nrv on 2/9/17.
 */

public class MessageKey {

    //Message Types which goes Service -> Activity (resultCode in ResultRecever.
    public static int MyLocationUpdate=1;
    public static int onStatusChangedLocation=2;
    public static int FireBaseTrackUpdate=3;


    public static int InitShow=0;
    public static int TrackTrain=1;
    public static int TrackBus=2;
    public static int PublishTrain=3;
    public static int PublishBus=4;

    public static String vehiclekeyindex="vehkey";
    public static String activityserviceintentName="com.kute.acttoservice";

    public static String intenetKeyTrackStatus="TrackStatus";
    public static String intenetKeyTrackVehicle="TrackVehicle";

}
