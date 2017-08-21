package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class Person implements Serializable {
    /***************** Defing Properties ***********/
    //Add provision for vehicle
    //Make these variables private
    public String id,name,img_base64,occupation,other_details,contact_phone,vehicle;
    //Implement in some other way public Boolean is_friend; // This will indicate whether the given person is users friend
    public ArrayList<Route> route_list;
    public String token;
    /*********** Properties Defined *******/
     public Person() {
     }

     /****************** This constructor will configure for the list item
     ****************** rest detail will be filled later when person detail activity is called *******/

    public Person(String name,String id,String img_base64,String token1)   //Add id and img_url(Not being added right now because we dont have the backend for now)
    {
        this.name=name;
        this.img_base64=img_base64;
        this.id=id;
        this.token=token1;
    }

    /************** This method will be invoked when we load personDetail Activity **********/

    public void completePersonDetail(String occupation,String other_details,String phone,String vehicle)
    {
        this.occupation=occupation;
        this.other_details=other_details;
        this.contact_phone=phone;
        this.vehicle=vehicle;
    }

    public void addRoutelist(ArrayList<Route>list)
    {
        this.route_list=list;
    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

}
