package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.io.Serializable;
import java.util.ArrayList;

/********** This will serve as a data model for Route List ******/

public class Route implements Serializable{
    String source,destination,name,time;
    Integer seats_available;
    Boolean is_starred;
    Long id;    //Firebase index
    ArrayList<Boolean>days=new ArrayList<Boolean>(7);//Days  on which the route works

    public Route() {//Default constructor for firebase
    }

    public Route(String name, String source, String destination, Integer seats_available,ArrayList<Boolean> day_list,String _time) {
        this.source = source;
        this.destination = destination;
        this.seats_available = seats_available;
        this.name=name;
        this.days=day_list;
        this.time=_time;

    }
    public Route(Long id,String name, String source, String destination, Integer seats_available, Boolean is_starred,String _time) {
        this.source = source;
        this.destination = destination;
        this.seats_available = seats_available;
        this.is_starred = is_starred;
        this.name=name;
        this.id=id;
        this.time=_time;
    }
    public Route(String name, String source, String destination, Integer seats_available, Boolean is_starred,String _time) {
        this.source = source;
        this.destination = destination;
        this.seats_available = seats_available;
        this.is_starred = is_starred;
        this.name=name;
        this.time=_time;

    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public Integer getSeats_available() {
        return seats_available;
    }

    public Boolean getIs_starred() {
        return is_starred;
    }

    public String getName()
    {
        return name;
    }

    public Long getId() {return id;}

    public String getTime() {
        return time;
    }

    public ArrayList<Boolean> getDays() {
        return days;
    }
}
