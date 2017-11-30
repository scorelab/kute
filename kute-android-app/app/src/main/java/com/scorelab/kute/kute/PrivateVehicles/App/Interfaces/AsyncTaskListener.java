package com.scorelab.kute.kute.PrivateVehicles.App.Interfaces;

/**
 * Created by nipunarora on 23/06/17.
 */

/******************************* Created an interface to create asynctasks which do not leak memory upon screen rotation **************/

public interface AsyncTaskListener {
    void onTaskStarted(Object...attachments);

    void onTaskCompleted(Object attachment);

}
