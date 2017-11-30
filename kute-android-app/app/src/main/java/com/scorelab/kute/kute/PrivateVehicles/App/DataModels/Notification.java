package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.io.Serializable;

/**
 * Created by nipunarora on 20/08/17.
 */

public class Notification implements Serializable {
    //Members
    String owner_id,rider_id,status;
    String opposite_name;
    String opposite_start,opposite_drop;

    //Constructors

    public Notification() {
    }

    public Notification(String owner_id, String rider_id, String status,String opp_name) {
        this.owner_id = owner_id;
        this.rider_id = rider_id;
        this.status = status;
        this.opposite_name=opp_name;
    }

    public Notification(String owner_id, String rider_id, String status, String opposite_name, String opposite_start, String opposite_drop) {
        this.owner_id = owner_id;
        this.rider_id = rider_id;
        this.status = status;
        this.opposite_name = opposite_name;
        this.opposite_start = opposite_start;
        this.opposite_drop = opposite_drop;
    }

    /**************  getters and setters *******************/
    public String getOwner_id() {
        return owner_id;
    }

    public String getRider_id() {
        return rider_id;
    }

    public String getOpposite_start() {
        return opposite_start;
    }

    public String getOpposite_drop() {
        return opposite_drop;
    }

    public String getStatus() {
        return status;
    }

    public String getOpposite_name() {
        return opposite_name;
    }

}
