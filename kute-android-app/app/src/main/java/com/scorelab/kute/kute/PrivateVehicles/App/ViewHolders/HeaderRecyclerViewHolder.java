package com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 10/06/17.
 */

public class HeaderRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public AppCompatImageView head_image;

    public HeaderRecyclerViewHolder(View itemView) {
        super(itemView);
        this.title = (TextView) itemView.findViewById(R.id.header);
        this.head_image = (AppCompatImageView) itemView.findViewById(R.id.headIcon);
    }
}
