package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.util.ArrayList;

/********** This will serve as a data model for Route List ******/

public class Route {
    String source,destination,name;
    Integer seats_available;
    Boolean is_starred;
    Long id;    //Firebase index
    ArrayList<Boolean>days=new ArrayList<Boolean>(7);//Days  on which the route works

    public Route(String name, String source, String destination, Integer seats_available, Boolean is_starred) {
        this.source = source;
        this.destination = destination;
        this.seats_available = seats_available;
        this.is_starred = is_starred;
        this.name=name;

    }
    public Route(Long id,String name, String source, String destination, Integer seats_available, Boolean is_starred) {
        this.source = source;
        this.destination = destination;
        this.seats_available = seats_available;
        this.is_starred = is_starred;
        this.name=name;
        this.id=id;

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
}
