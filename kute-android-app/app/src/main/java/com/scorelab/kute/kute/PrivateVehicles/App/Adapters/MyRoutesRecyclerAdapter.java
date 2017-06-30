package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;


import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Route;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.HeaderRecyclerViewHolder;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.MyRoutesItemViewHolder;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class MyRoutesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int header = 7;
    private final String TAG="MyRoutesRecycler";
    private static final int general_list_item=77;
    private ArrayList<Route> data_source;



    /**************** Constructor ****/
    public MyRoutesRecyclerAdapter(ArrayList<Route> data_source) {
        this.data_source = data_source;
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
                return new MyRoutesItemViewHolder(item_view);
            default:item_view=layout_inflater.inflate(R.layout.my_route_item,parent,false);
                return new MyRoutesItemViewHolder(item_view);
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

    /***************** End of Overrides *************/
    /************ Configuring View Holders ****************/
    private void configureGeneralItem(final MyRoutesItemViewHolder vh, final int position)
    {
        Route m=data_source.get(position-1);
        vh.from.setText(m.getSource());
        vh.to.setText(m.getDestination());
        vh.no_of_seats.setText(m.getSeats_available().toString());
        vh.is_starred.setChecked(m.getIs_starred());
        vh.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,String.format("Route Share request:The position is %d",vh.getAdapterPosition()));
                shareRoute(vh.getAdapterPosition());
            }
        });
        vh.is_starred.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG,String.format("Route Starred:The position is %d",vh.getAdapterPosition()));
            }
        });
        vh.name.setText(m.getName());
    }

    private void configureRecyclerHead(HeaderRecyclerViewHolder vh)
    {
        vh.title.setText("My Routes");
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
