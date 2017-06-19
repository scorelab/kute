package com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 10/06/17.
 */

public class PersonItemViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public RoundedImageView profile_pic;
    /****************** Default Constructor **************/
    public PersonItemViewHolder(View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.name);
        profile_pic=(RoundedImageView)itemView.findViewById(R.id.personimg);
    }
}
