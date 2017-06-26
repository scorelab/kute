package com.scorelab.kute.kute.PrivateVehicles.App.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Fragments.PlaceHolderFragment;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 15/06/17.
 */

public class PersonDetail extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton fab;
    RoundedImageView profile_image;
    TextView name, occupation, contact_phone, vehicle, other_details;
    TableRow contact_row, other_details_row, vehicle_row, no_info_row;
    ImageButton otherdetail_dropdown;
    Boolean is_otherdetails_dropdown;
    ScrollView scroll_view;
    ImageLoader mImageLoader;
    ImageButton back;
    RelativeLayout occupation_row;
    private final String TAG = "PersonDetail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_detail);
        //Inserting fragment in route frame
        PlaceHolderFragment noroutes = new PlaceHolderFragment();//Currently we are straight away employing a placeholder no routes fragment
        //we wll check whether the routes are available or not
        Bundle args = new Bundle();
        args.putString("Label", "No Routes Available");
        noroutes.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.routeFramePersonDetail, noroutes).commit();
        //Initialise the views and variables required
        //View Initialization
        name = (TextView) findViewById(R.id.name);
        occupation = (TextView) findViewById(R.id.Occupation);
        profile_image = (RoundedImageView) findViewById(R.id.personImage);
        contact_phone = (TextView) findViewById(R.id.ContactPhone);
        vehicle = (TextView) findViewById(R.id.Vehicle);
        other_details = (TextView) findViewById(R.id.otherDetailsText);
        contact_row = (TableRow) findViewById(R.id.contact_row);
        fab = (FloatingActionButton) findViewById(R.id.personDetailFAB);
        other_details_row = (TableRow) findViewById(R.id.rowOtherDetails);
        no_info_row = (TableRow) findViewById(R.id.noDetailsRow);
        otherdetail_dropdown = (ImageButton) findViewById(R.id.otherDetailsDropdownIcon);
        vehicle_row = (TableRow) findViewById(R.id.vehicleRow);
        occupation_row = (RelativeLayout) findViewById(R.id.workRow);
        scroll_view = (ScrollView) findViewById(R.id.personDetailScroll);
        back = (ImageButton) findViewById(R.id.backNav);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Variable initialization
        is_otherdetails_dropdown = false;
        //Fill in the person details
        fillPersonDetails();
        //Onclick listeners
        contact_row.setOnClickListener(this);
        otherdetail_dropdown.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(getIntent().getBooleanExtra("isAFriend", false))) {
                    Log.d("Check", "Was Clicked");
                    fab.setImageResource(R.drawable.ic_query_builder_white_24dp);
                }
            }
        });

    }

    //On click listener for the entire activity
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_row:
                TextView t = (TextView) v.findViewById(R.id.ContactPhone);
                callPerson(t.getText().toString());
                break;
            case R.id.otherDetailsDropdownIcon:
                is_otherdetails_dropdown = handleOtherDetailsDropdown(is_otherdetails_dropdown, otherdetail_dropdown, other_details_row);
                Log.d("Boolean check", is_otherdetails_dropdown.toString());
                break;
        }
    }

    /*************** Custom Functions *****************/
    //Allows you to open the dialer screen with the person's number
    public void callPerson(String number) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + number));
        startActivity(i);
    }

    //Handles the dropdown of other details ; returns bool to allow toggling
    public Boolean handleOtherDetailsDropdown(Boolean is_otherdetail_dropdown, ImageButton icon, final TableRow other_details_text) {
        if (is_otherdetail_dropdown) {
            other_details_text.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            return false;
        } else {
            other_details_text.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
            //Adjusting the scrollview for smaller screens
            scroll_view.post(new Runnable() {
                @Override
                public void run() {
                    scroll_view.scrollTo(0, other_details_text.getBottom());
                }
            });
            return true;
        }
    }

    /*********** Filling user details from intent *******/
    private void fillPersonDetails() {
        Person p = (Person) getIntent().getSerializableExtra("Person");
        Boolean is_friend = getIntent().getBooleanExtra("isAFriend", false);
        name.setText(p.name);
        if (is_friend) {
            fab.setImageResource(R.drawable.ic_done_all_white_24dp);
        }
        if (p.occupation != null) {
            occupation.setText(p.occupation);
            contact_phone.setText(p.contact_phone);
            vehicle.setText(p.vehicle);
            other_details.setText(p.other_details);
        } else {
            occupation_row.setVisibility(View.INVISIBLE);
            vehicle_row.setVisibility(View.GONE);
            contact_row.setVisibility(View.GONE);
            no_info_row.setVisibility(View.VISIBLE);

        }
        mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
        String img_url = String.format("https://graph.facebook.com/%s/picture?type=normal", p.id);
        Log.d(TAG, "Image Url for ImageLoader is" + img_url);
        mImageLoader.get(img_url, ImageLoader.getImageListener(profile_image,
                R.drawable.ic_person_black_36dp, R.drawable.ic_person_black_36dp));

    }
}
