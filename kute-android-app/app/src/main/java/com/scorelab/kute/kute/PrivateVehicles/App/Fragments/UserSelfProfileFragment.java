package com.scorelab.kute.kute.PrivateVehicles.App.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserSelfProfileFragment extends Fragment implements View.OnClickListener {
    View v;
    RelativeLayout work_row,work_row_edit;
    TextView name,occupation,vehicle,contact_phone,other_details;
    AppCompatEditText name_edit,occupation_edit,vehicle_edit,contact_phone_edit,other_details_edit;
    ImageButton other_details_dropdown,edit_icon;
    Boolean is_edit_layout_drawn=false;
    String name_string,occupation_string,vehicle_string,contact_string,other_details_string;
    SharedPreferences pref;
    final String TAG="UserSelfProfileFragment";

    public UserSelfProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.self_profile_fragment,container,false);
        work_row=(RelativeLayout)v.findViewById(R.id.workRow);
        work_row_edit=(RelativeLayout)v.findViewById(R.id.workRowEdit);
        name=(TextView)v.findViewById(R.id.name);
        name_edit=(AppCompatEditText)v.findViewById(R.id.nameEdit);
        occupation=(TextView)v.findViewById(R.id.occupationName);
        occupation_edit=(AppCompatEditText)v.findViewById(R.id.occupationNameEdit);
        vehicle=(TextView)v.findViewById(R.id.vehicleName);
        vehicle_edit=(AppCompatEditText)v.findViewById(R.id.vehicleNameEdit);
        contact_phone=(TextView)v.findViewById(R.id.contactPhone);
        contact_phone_edit=(AppCompatEditText)v.findViewById(R.id.contactPhoneEdit);
        other_details=(TextView)v.findViewById(R.id.otherDetailsText);
        other_details_edit=(AppCompatEditText)v.findViewById(R.id.otherDetailsEdit);
        other_details_dropdown=(ImageButton)v.findViewById(R.id.otherDetailsDropdownIcon);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        edit_icon=(ImageButton)getActivity().findViewById(R.id.searchIcon);//The Id is search icon because this image button is used for search in the homebase fragment
        edit_icon.setOnClickListener(this);
        pref=getApplicationContext().getSharedPreferences("user_credentials",0);
        loadInitialPersonDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.searchIcon:
                //Handle the logic for edit
                if(is_edit_layout_drawn) {
                    Log.d("Status","We are in edit layout drawn");
                    setupDetailsLayout();
                    is_edit_layout_drawn=false;
                }
                else {
                    Log.d("Status","We are in edit layout not drawn");
                    setupEditLayout();
                    is_edit_layout_drawn=true;
                }
        }
    }

    /***************************** Custom Functions *************************/
    //This method helps you setup the layout for editing the profile
    private void setupEditLayout()
    {
        edit_icon.setImageResource(R.drawable.ic_done_white_24dp);
        name.setVisibility(View.GONE);
        name_edit.setVisibility(View.VISIBLE);
        name_edit.setText(name_string);
        work_row.setVisibility(View.GONE);
        work_row_edit.setVisibility(View.VISIBLE);
        occupation_edit.setText(occupation_string);
        vehicle.setVisibility(View.GONE);
        vehicle_edit.setVisibility(View.VISIBLE);
        vehicle_edit.setText(vehicle_string);
        contact_phone.setVisibility(View.GONE);
        contact_phone_edit.setVisibility(View.VISIBLE);
        contact_phone_edit.setText(contact_string);
        other_details.setVisibility(View.GONE);
        other_details_edit.setVisibility(View.VISIBLE);
        other_details_edit.setText(other_details_string);
    }

    //The method helps you setup the details viewing of your profile you wont be able to edit it in this layout
    private void setupDetailsLayout()
    {
        edit_icon.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        Person p=saveCurrentProfile();
        name.setVisibility(View.VISIBLE);
        name_edit.setVisibility(View.GONE);
        name.setText(name_string);
        work_row.setVisibility(View.VISIBLE);
        work_row_edit.setVisibility(View.GONE);
        occupation.setText(occupation_string);
        vehicle.setVisibility(View.VISIBLE);
        vehicle_edit.setVisibility(View.GONE);
        vehicle.setText(vehicle_string);
        contact_phone.setVisibility(View.VISIBLE);
        contact_phone_edit.setVisibility(View.GONE);
        contact_phone.setText(contact_string);
        other_details.setVisibility(View.VISIBLE);
        other_details_edit.setVisibility(View.GONE);
        other_details.setText(other_details_string);
        registerFirebaseDbSelf(p);
    }
    //Save Current profile details
    private Person saveCurrentProfile()
    {
        name_string=name_edit.getText().toString();
        occupation_string=occupation_edit.getText().toString();
        vehicle_string=vehicle_edit.getText().toString();
        contact_string=contact_phone_edit.getText().toString();
        other_details_string=other_details_edit.getText().toString();
        //save details to shared preferences
        saveToPrefs();
        Person p=new Person(name_string,pref.getString("Id","null"),pref.getString("Profile_Image","null"));
        p.completePersonDetail(occupation_string,other_details_string,contact_string,vehicle_string);
        return p;
    }
    //Save the profile to Prefs
    private void saveToPrefs() {

        SharedPreferences.Editor editor=pref.edit();
        editor.putString("Name",name_string );
        editor.putString("Profile_Image","null");
        editor.putString("Occupation",occupation_string);
        editor.putString("Vehicle",vehicle_string);
        editor.putString("Contact",contact_string);
        editor.putString("OtherDetails",other_details_string);
        editor.apply();
    }
    //Save current profile to Firebase Database
    public void registerFirebaseDbSelf(final Person p) {
        Person temp = p;
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = root.child("Users");
        Log.d(TAG, "Saving Self To db");
        users.child(temp.id).setValue(temp).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Firebase Self Add Error:" + e.toString());
            }
        });
    }
    //Load current profile details on startup
    private void loadInitialPersonDetails() {
        name_string=pref.getString("Name","null");
        name.setText(name_string);
        occupation_string=pref.getString("Occupation","null");
        Log.d("Check",occupation_string);
        if(occupation_string.equals("null")) {
            Log.d("Status","We are in occupation_string is null");
            occupation.setText("Edit to set Occupation");
            contact_string = pref.getString("Contact", "null");
            contact_phone.setText(contact_string);
            vehicle_string = pref.getString("Vehicle", "null");
            vehicle.setText(vehicle_string);
            other_details_string = pref.getString("OtherDetails", "Check");
            Log.d("Status","Other details string is"+other_details_string);
            Log.d("Status","occupations string is"+occupation_string);
            if (other_details_string.equals("null")) {
                Log.d("Status","We are in other details string null");
                other_details.setText("Edit to add Other details you wish to share");

            } else {
                Log.d("Status","We are in other details string not null");
                other_details.setText(other_details_string);
            }
        }
        else {
            Log.d("Status","We are in occupation_string is not null");
            occupation.setText(occupation_string);
            contact_string = pref.getString("Contact", "null");
            contact_phone.setText(contact_string);
            vehicle_string = pref.getString("Vehicle", "null");
            vehicle.setText(vehicle_string);
            other_details_string = pref.getString("OtherDetails", "null");
            if (other_details_string.equals("null")) {
                other_details.setText("Other details you wish to share");
            } else {
                other_details.setText(other_details_string);
            }
        }
    }
}
