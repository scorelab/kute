package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.DaysPickerRecyclerActivityInterface;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 28/07/17.
 */

public class DaysPickerAdapter extends RecyclerView.Adapter<DaysPickerAdapter.MyViewHolder> {
    DaysPickerRecyclerActivityInterface messenger_activity;
    String [] Days=new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    ArrayList<Boolean>days_status;
    //0-Monday,1-Tuesday,2-Wednesday............

    public DaysPickerAdapter(DaysPickerRecyclerActivityInterface messenger_activity1,ArrayList<Boolean>days_status_list){
        messenger_activity=messenger_activity1;
        days_status=days_status_list;
    }

    //Custom View Holder Class
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView day_name;
        public ImageView tick;
        public MyViewHolder(View view){
            super(view);
            day_name=(TextView)view.findViewById(R.id.daysLabel);
            tick=(ImageView)view.findViewById(R.id.tick);
            view.setOnClickListener(this);
        }
        //Implementing the on click listener for the recycler view

        @Override
        public void onClick(View v) {
                int current_adapter_position=getAdapterPosition();
                if (v.findViewById(R.id.tick).getVisibility()==View.VISIBLE){
                    v.findViewById(R.id.tick).setVisibility(View.INVISIBLE);
                    messenger_activity.getDay("Delete",current_adapter_position);
                }
                else {
                    v.findViewById(R.id.tick).setVisibility(View.VISIBLE);
                    messenger_activity.getDay("Add",current_adapter_position);
                }
        }
    }

    //Implementing main methods of the recycler
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.days_selector_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(days_status!=null) {
            if (days_status.get(position)) {
                holder.tick.setVisibility(View.VISIBLE);
            }
        }
        holder.day_name.setText(Days[position]);
    }

    @Override
    public int getItemCount() {
        return Days.length;
    }
}
