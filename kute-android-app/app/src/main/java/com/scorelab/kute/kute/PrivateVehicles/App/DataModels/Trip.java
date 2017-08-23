package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nipunarora on 20/08/17.
 */

public class Trip implements Serializable{
    //Declaring class members
    String route_id; //In case the trip is started from a route we will have a route id pointing out to the route and we will not need members such as the source address etc
    String source_address,destination_address;
    String source_name,destination_name;
    String source_cords,destination_cords;
    String time;
    Boolean is_owner; //This Boolean member would indicate whether the person is owner or not
    String owner_string; // This string would contain the name of the owner in case the user is not the owner that is the above boolean is 0
    ArrayList<String> travelling_with;
    String owner_address;

    /************************** Constructor ****************/
    public Trip(String route_id, Boolean owner, String owner_string,String time1) {
        this.route_id = route_id;
        this.is_owner = owner;
        this.owner_string = owner_string;
        this.time=time1;
    }

    public Trip(String source_address, String destination_address, String source_name, String destination_name, String source_cords, String destination_cords, String time, Boolean owner, String owner_string) {
        this.source_address = source_address;
        this.destination_address = destination_address;
        this.source_name = source_name;
        this.destination_name = destination_name;
        this.source_cords = source_cords;
        this.destination_cords = destination_cords;
        this.time = time;
        this.is_owner = owner;
        this.owner_string = owner_string;
    }

    /************************* Getters and Setters **************************/
    public String getRoute_id() {
        return route_id;
    }

    public String getSource_address() {
        return source_address;
    }

    public String getDestination_address() {
        return destination_address;
    }

    public String getSource_name() {
        return source_name;
    }

    public String getDestination_name() {
        return destination_name;
    }

    public String getSource_cords() {
        return source_cords;
    }

    public String getDestination_cords() {
        return destination_cords;
    }

    public String getTime() {
        return time;
    }

    public Boolean getIsOwner() {
        return is_owner;
    }

    public String getOwner_string() {
        return owner_string;
    }

    public ArrayList<String> getTravelling_with() {
        return travelling_with;
    }

    public String getOwner_address() {
        return owner_address;
    }
    //setters

    public void setSource_address(String source_address) {
        this.source_address = source_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public void setDestination_name(String destination_name) {
        this.destination_name = destination_name;
    }

    public void setSource_cords(String source_cords) {
        this.source_cords = source_cords;
    }

    public void setDestination_cords(String destination_cords) {
        this.destination_cords = destination_cords;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIsOwner(Boolean owner) {
        this.is_owner = owner;
    }

    public void setOwner_string(String owner_string) {
        this.owner_string = owner_string;
    }

    public void setTravelling_with(ArrayList<String> travelling_with) {
        this.travelling_with = travelling_with;
    }

    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }
}
