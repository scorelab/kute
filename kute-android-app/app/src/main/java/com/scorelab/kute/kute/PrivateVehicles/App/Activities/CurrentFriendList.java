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
import com.scorelab.kute.kute.PrivateVehicles.App.AsyncTasks.LoadFirebaseFriends;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.AsyncTaskListener;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.R;
import java.util.ArrayList;


//We can optimize the loading of friends by restricting firebase content to return just two fields for each user and then retrieve detail only when specific persons detail activity is opened
public class CurrentFriendList extends AppCompatActivity implements RecyclerItemClick,AsyncTaskListener {
    static String TAG = "CurrentFriendActivity";
    ArrayList<String> person_list;
    ArrayList<Person>person_detail_list;
    RecyclerView friend_recycler;
    FriendRecyclerAdapter recycler_adapter;
    ImageButton back;
    final int[] pastVisiblesItems = new int[1];
    final int[] visibleItemCount = new int[1];
    final int[] totalItemCount = new int[1];
    int start_index_async,last_index_async,range;
    ProgressBar progressBar;
    boolean is_async_task_running=false;// A boolean created to prevent a new asynctask being created everytime we scroll down
    LoadFirebaseFriends load_friends_async;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_friends_activity);
        person_list = (ArrayList<String>)getIntent().getSerializableExtra("FriendList");
        person_detail_list=(ArrayList<Person>) getIntent().getSerializableExtra("FriendDetailList");
        /************** Initialise the views *********/
        //TODO get friends from facebook and google and get their images
        Log.d(TAG,"The length of the person list is:"+Integer.toString(person_list.size()));
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
                if ((visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0]&& visibleItemCount[0]!=totalItemCount[0] ) {
                    if(mLayoutManager.findLastCompletelyVisibleItemPosition()==recycler_adapter.getItemCount()-1 /*&& recycleradapter.getItemCount()>5 You have your ofset values here*/){
                        //************************* Reached the End of recycler View ***********/
                        Log.d(TAG,"reached the Bottom");
                        //******************** Call The function to load more data *********//
                        loadRecyclerItems(recycler_adapter.getItemCount()-1);
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
    //Asynctask Interacting interface
    @Override
    public void onTaskStarted(Object...attachments) {
        //toggle the boolean to show that asynctask is running
        is_async_task_running=true;
        //get the index from asynctask
        start_index_async=(int)attachments[0];
        last_index_async=(int)attachments[1];
        range=last_index_async-last_index_async+1;

    }

    @Override
    public void onTaskCompleted(Object attachment) {
        Log.d(TAG,"The start indice is "+Integer.toString(start_index_async));
        Person temp=(Person)attachment;
        person_detail_list.add(temp);
        Log.d(TAG,"Received in test Friend name: "+temp.name);
        if(start_index_async>=last_index_async) {
            recycler_adapter.notifyItemRangeInserted(recycler_adapter.getItemCount(),range);
            is_async_task_running=false;
            Log.d(TAG,"Current Length  Of Recycler is"+Integer.toString(recycler_adapter.getItemCount()-1));
            progressBar.setVisibility(View.GONE);
        }
        ++start_index_async;

    }

    /***************** Custom Function ******/
    private void loadRecyclerItems(Integer current_length)
    {
        Log.d(TAG,"Current size is "+Integer.toString(recycler_adapter.getItemCount()));
        if(person_list.size()==current_length){
            //When we have loaded all the items
            Log.d("check","Yes");
        }
        else {
            //************** Set The visibility of progress bar as true *****/
            progressBar.setVisibility(View.VISIBLE);
            int difference = person_list.size() - current_length;
            //we need to load more friends from facebook
            if (!is_async_task_running) {
                load_friends_async=new LoadFirebaseFriends(person_list,this);
                if (difference < 9) {
                    load_friends_async.execute(current_length+1, (person_list.size()-1));
                }else {
                    load_friends_async.execute(current_length+1,current_length+9);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        load_friends_async.cancel(true);
    }
}
