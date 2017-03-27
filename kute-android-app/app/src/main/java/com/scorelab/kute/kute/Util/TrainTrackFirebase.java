package com.scorelab.kute.kute.Util;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by nrv on 2/10/17.
 */
@IgnoreExtraProperties
public class TrainTrackFirebase {
    public long lat;
    public long lon;
    public long speed;
    public String trainname;

    public long getLat() {
        return lat;
    }

    public long getLon() {
        return lon;
    }

    public long getSpeed() {
        return speed;
    }

    public String getTrainname() {
        return trainname;
    }
}
