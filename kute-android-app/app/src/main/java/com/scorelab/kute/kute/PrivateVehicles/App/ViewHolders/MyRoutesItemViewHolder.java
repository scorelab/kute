package com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders;

/**
 * Created by nipunarora on 10/06/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scorelab.kute.kute.R;


/************ Defining the View Holder Class *************/
public class MyRoutesItemViewHolder extends RecyclerView.ViewHolder
{
    public TextView from,to,no_of_seats,name;
    public CheckBox is_starred;
    public ImageButton share;

    public MyRoutesItemViewHolder(View itemView) {
        super(itemView);
        from=(TextView)itemView.findViewById(R.id.textFrom);
        to=(TextView)itemView.findViewById(R.id.textTo);
        no_of_seats=(TextView)itemView.findViewById(R.id.noOfSeats);
        is_starred=(CheckBox)itemView.findViewById(R.id.star);
        share=(ImageButton)itemView.findViewById(R.id.share);
        name=(TextView)itemView.findViewById(R.id.RouteHead);
    }
}
/******************** View Holder End ******/