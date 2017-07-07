package com.scorelab.kute.kute.PrivateVehicles.App.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.FriendRecyclerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.R;
import java.util.ArrayList;



public class CurrentFriendList extends AppCompatActivity implements RecyclerItemClick {
    static String TAG = "CurrentFriendActivity";
    ArrayList<Person> person_list;
    RecyclerView friend_recycler;
    FriendRecyclerAdapter recycler_adapter;
    ImageButton back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_friends_activity);
        person_list = (ArrayList<Person>) getIntent().getSerializableExtra("FriendList");
        back = (ImageButton) findViewById(R.id.backNav);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        friend_recycler = (RecyclerView) findViewById(R.id.personRecycler);
        recycler_adapter = new FriendRecyclerAdapter(person_list, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        friend_recycler.setLayoutManager(mLayoutManager);
        friend_recycler.setItemAnimator(new DefaultItemAnimator());
        friend_recycler.setAdapter(recycler_adapter);
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Log.d(TAG, String.format("Item position clicked :%d", position));
        Intent i = new Intent(this, PersonDetail.class);
        i.putExtra("Person", person_list.get(position - 1));
        i.putExtra("isAFriend", true);
        startActivity(i);
    }
}
