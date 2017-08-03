package com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders;
/**
 * Created by nipunarora on 10/06/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scorelab.kute.kute.R;


/************ Defining the View Holder Class *************/
public class MyRoutesItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
    public TextView from, to, no_of_seats, name;
    public CheckBox is_starred;
    public ImageButton share;
    RecyclerClick click_listener;

    public interface RecyclerClick{
        void onRecyclerClick(int position, Context c);

    }

    public MyRoutesItemViewHolder(View itemView,RecyclerClick listener) {
        super(itemView);
        from = (TextView) itemView.findViewById(R.id.textFrom);
        to = (TextView) itemView.findViewById(R.id.textTo);
        no_of_seats = (TextView) itemView.findViewById(R.id.noOfSeats);
        is_starred = (CheckBox) itemView.findViewById(R.id.star);
        name = (TextView) itemView.findViewById(R.id.RouteHead);
        itemView.setOnClickListener(this);
        this.click_listener=listener;
    }

    @Override
    public void onClick(View v) {
        click_listener.onRecyclerClick(getAdapterPosition(),v.getContext());
    }
}
/******************** View Holder End ******/