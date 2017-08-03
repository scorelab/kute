package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;


import com.scorelab.kute.kute.PrivateVehicles.App.Activities.Routes.RouteDetail;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.HeaderRecyclerViewHolder;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.MyRoutesItemViewHolder;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class MyRoutesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MyRoutesItemViewHolder.RecyclerClick {
    private static final int header = 7;
    private final String TAG="MyRoutesRecycler";
    private static final int general_list_item=77;
    private String header_banner;
    private ArrayList<Route> data_source;


    /**************** Constructor ****/
    public MyRoutesRecyclerAdapter(ArrayList<Route> data_source,String banner_header) {
        this.data_source = data_source;
        this.header_banner=banner_header;
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
            case general_list_item:item_view=layout_inflater.inflate(R.layout.my_route_item,parent,false);
                return new MyRoutesItemViewHolder(item_view,this);
            default:item_view=layout_inflater.inflate(R.layout.my_route_item,parent,false);
                return new MyRoutesItemViewHolder(item_view,this);
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
                configureGeneralItem((MyRoutesItemViewHolder)holder,position);
                break;
            default:
                configureGeneralItem((MyRoutesItemViewHolder)holder,position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return data_source.size()+1;//added 1 for the header tile
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0)
            return header;
        else
            return general_list_item;
    }

    //implementing the recycler item click interface

    @Override
    public void onRecyclerClick(int position, Context c) {
        Intent i = new Intent(c, RouteDetail.class);
        i.putExtra("Route", data_source.get(position-1));
        c.startActivity(i);
    }
    /***************** End of Overrides *************/
    /************ Configuring View Holders ****************/
    private void configureGeneralItem(final MyRoutesItemViewHolder vh, final int position)
    {
        Route m=data_source.get(position-1);
        vh.from.setText(m.getSource());
        vh.to.setText(m.getDestination());
        vh.no_of_seats.setText(m.getSeats_available().toString());
        if(m.getIs_starred()!=null) {
            vh.is_starred.setChecked(m.getIs_starred());
            //Should be assigned in the view holder
            vh.is_starred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, String.format("Route Starred:The position is %d", vh.getAdapterPosition()));
                }
            });
        }
        vh.name.setText(m.getName());
    }

    private void configureRecyclerHead(HeaderRecyclerViewHolder vh)
    {
        vh.title.setText(header_banner+" Routes");
        vh.head_image.setBackgroundResource(R.drawable.route2);
    }
    /******** End of Configuring of view holders ***************/
    /****************** Custom Functions *************************/
    public void shareRoute(Integer position)
    {
        //TODO handle the route share logic here
    }
    /**************** End of Custom Functions ******************/
}
