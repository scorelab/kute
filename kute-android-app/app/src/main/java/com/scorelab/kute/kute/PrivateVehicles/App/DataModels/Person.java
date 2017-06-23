package com.scorelab.kute.kute.PrivateVehicles.App.DataModels;

import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class Person {
    /***************** Defing Properties ***********/
    //Add provision for vehicle
            
    public String id,name,img_url,occupation;
    public Boolean is_friend; // This will indicate whether the given person is users friend
    public ArrayList<Route> route_list;

    /*********** Properties Defined *******/

    /****************** This constructor will configure for the list item
     ****************** rest detail will be filled later when person detail activity is called *******/

    public Person(String name)   //Add id and img_url(Not being added right now because we dont have the backend for now)
    {
        this.name=name;
    }

    /************** This method will be invoked when we load personDetail Activity **********/

    public void completePersonDetail(String occupation,boolean is_friend,ArrayList<Route>list)
    {
        this.occupation=occupation;
        this.is_friend=is_friend;
        this.route_list=list;
    }

}
