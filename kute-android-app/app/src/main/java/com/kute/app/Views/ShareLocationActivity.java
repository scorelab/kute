package com.kute.app.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.kute.app.Activities.SplashActivity;
import com.kute.app.Bussiness.Train;
import com.kute.app.R;

import java.util.ArrayList;

public class ShareLocationActivity extends AppCompatActivity {

    private Spinner vehicleList;
    private Button shareNow, cancel;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarShare);
        setSupportActionBar(toolbar);

        vehicleList = (Spinner) findViewById(R.id.itemListSpinner);

        shareNow = (Button) findViewById(R.id.share_button);
        cancel = (Button) findViewById(R.id.cancel_button);


        ArrayList<String> lst = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(ShareLocationActivity.this, android.R.layout.simple_list_item_1, lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleList.setAdapter(adapter);
        getTrains();
        shareNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(getApplicationContext(),
                        IndividualShareLocationActivity.class);
                Bundle selectedDetails = new Bundle();
                selectedDetails.putString("Selected Vehicle", vehicleList.getSelectedItem().toString());
                share.putExtra("bundle", selectedDetails);
                startActivity(share);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cancelled = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(cancelled);
                finish();
            }
        });
    }

    public void getTrains(){
        final ArrayList<Train> trains=new ArrayList<Train>();
        //Firebase ref = new Firebase("https://docs-examples.firebaseio.com/web/saving-data/fireblog/posts");
        Firebase ref = new Firebase("https://kute-37f82.firebaseio.com/android/TrainsNo/TrainData/SrilankanTrains");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot snapshot) {
                                                   System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                                                   adapter.clear();
                                                   for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                       Train train = postSnapshot.getValue(Train.class);
                                                       trains.add(train);
                                                       adapter.add(train.getTrainname());
                                                       adapter.notifyDataSetChanged();
                                                   }

                                                   vehicleList.setAdapter(adapter);


                                                   // do some stuff once
                                               }

                                               @Override
                                               public void onCancelled(FirebaseError firebaseError) {
                                                   Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                                               }
                                           }

        );
        for (Train train:trains) {
            adapter.clear();
            adapter.add(train.getTrainname());
            adapter.notifyDataSetChanged();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.sign_out:
                Intent goBack = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(goBack);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
