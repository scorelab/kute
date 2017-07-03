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
import android.widget.ProgressBar;

import com.scorelab.kute.kute.PrivateVehicles.App.Adapters.FriendRecyclerAdapter;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.R;
import java.util.ArrayList;



public class CurrentFriendList extends AppCompatActivity implements RecyclerItemClick {
    static String TAG = "CurrentFriendActivity";
    ArrayList<String> person_list;
    ArrayList<Person>person_detail_list;
    RecyclerView friend_recycler;
    FriendRecyclerAdapter recycler_adapter;
    ImageButton back;
    final int[] pastVisiblesItems = new int[1];
    final int[] visibleItemCount = new int[1];
    final int[] totalItemCount = new int[1];
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_friends_activity);
        person_list = new ArrayList<String>();
        person_detail_list=(ArrayList<Person>) getIntent().getSerializableExtra("FriendList");
        /************** Initialise the views *********/
        //TODO get friends from facebook and google and get their images
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        back = (ImageButton) findViewById(R.id.backNav);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        friend_recycler = (RecyclerView) findViewById(R.id.personRecycler);
        recycler_adapter = new FriendRecyclerAdapter(person_detail_list, this, this);
        final android.support.v7.widget.LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        friend_recycler.setLayoutManager(mLayoutManager);
        friend_recycler.setItemAnimator(new DefaultItemAnimator());
        friend_recycler.setAdapter(recycler_adapter);
        friend_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount[0] = mLayoutManager.getChildCount();//Gives the number of children currently on the screen
                totalItemCount[0] = mLayoutManager.getItemCount();//gives total items of recycler view
                pastVisiblesItems[0] = mLayoutManager.findFirstVisibleItemPosition();//gives the index of item at the top of the screen

                Log.d("Recycler",String.format("visible %d past visible %d total %d",visibleItemCount[0],pastVisiblesItems[0],totalItemCount[0]));
                if ((visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0]) {
                    Log.d("Check it",String.format("%d %d",mLayoutManager.findLastCompletelyVisibleItemPosition(),recycler_adapter.getItemCount()));
                    if(mLayoutManager.findLastCompletelyVisibleItemPosition()==recycler_adapter.getItemCount()-1 /*&& recycleradapter.getItemCount()>5 You have your ofset values here*/){
                        //************************* Reached the End of recycler View ***********/
                        Log.d("Status","reached the Bottom");
                        //************** Set The visibility of progress bar as true *****/
                        progressBar.setVisibility(View.VISIBLE);
                        //******************** Call The function to load more data *********//
                        loadRecyclerItems(recycler_adapter.getItemCount());


                    }

                }
            }
        });
    }

    @Override
    public void onRecyclerItemClick(int position) {
        Log.d(TAG, String.format("Item position clicked :%d", position));
        Intent i = new Intent(this, PersonDetail.class);
        i.putExtra("Person", person_list.get(position - 1));
        i.putExtra("isAFriend", true);
        startActivity(i);
    }

    /***************** Custom Function ******/
    private void loadRecyclerItems(Integer current_length)
    {
        if(person_list.size()==current_length){
            //When we have loaded all the items
            Log.d("check","Yes");
        }
        else{

        }
    }
}
