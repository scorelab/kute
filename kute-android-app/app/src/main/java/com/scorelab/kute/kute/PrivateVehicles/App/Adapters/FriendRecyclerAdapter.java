package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.HeaderRecyclerViewHolder;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.PersonItemViewHolder;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int header = 7;
    private static final int general_list_item=77;
    private ArrayList<Person> data_source;
    Bitmap testdp;



    /**************** Constructor ****/
    public FriendRecyclerAdapter(ArrayList<Person> data_source,Bitmap test)//The bitmap arguement has been passed just to put in the test person image
    //Later on we will have have an image url which would be used with volley image loader to get the image
     {
        this.data_source = data_source;
        this.testdp=test;
    }
    /******** End of Constructor ******/

    /******************* Overrides ******/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layout_inflater= LayoutInflater.from(parent.getContext());
        View item_view=null;
        switch(viewType)
        {
            case header:item_view=layout_inflater.inflate(R.layout.recycler_head,parent,false);
                return new HeaderRecyclerViewHolder(item_view);
            case general_list_item:item_view=layout_inflater.inflate(R.layout.person_item,parent,false);
                return new PersonItemViewHolder(item_view);
            default:item_view=layout_inflater.inflate(R.layout.person_item,parent,false);
                return new PersonItemViewHolder(item_view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {
            case header:
                configureRecyclerHead((HeaderRecyclerViewHolder)holder);
                break;
            case general_list_item:
                configureGeneralItem((PersonItemViewHolder)holder,position);
                break;
            default:
                configureGeneralItem((PersonItemViewHolder)holder,position);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return data_source.size()+1;//added 1 for header tile
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return header;
        else
            return general_list_item;
    }

    /***************** End of Overrides *************/
    /************ Configuring View Holders ****************/
    private void configureGeneralItem(PersonItemViewHolder vh, int position)
    {
        Person p=data_source.get(position-1);
        vh.name.setText(p.name);
        /********* Test ******/
        vh.profile_pic.setImageBitmap(testdp);
        /******** End of Test Data ********/
    }

    private void configureRecyclerHead(HeaderRecyclerViewHolder vh)
    {
        vh.title.setText("Current Friends");
        vh.head_image.setBackgroundResource(R.drawable.community);
    }
    /******** End of Configuring of view holders ***************/
}

