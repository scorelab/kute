package com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 10/06/17.
 */

public class PersonItemViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public RoundedImageView profile_pic;

    /****************** Default Constructor **************/

    public PersonItemViewHolder(View itemView, final RecyclerItemClick item_click_handler) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.name);
        profile_pic = (RoundedImageView) itemView.findViewById(R.id.personimg);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                Log.d("FriendsRecyclerAdapter", "The position clicked is: " + pos);
                item_click_handler.onRecyclerItemClick(pos);

            }
        });

    }
}
