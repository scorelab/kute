package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.toolbox.ImageLoader;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.PersonDetail;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.HeaderRecyclerViewHolder;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.PersonItemViewHolder;
import com.scorelab.kute.kute.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by nipunarora on 10/06/17.
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int header = 7;
    private static final int general_list_item=77;
    private ArrayList<Person> data_source;
    private ImageLoader mImageLoader;
    private final String TAG="FriendRecyclerAdapter";
    Context mcontext;
    RecyclerItemClick item_click_handler;



    /**************** Constructor ****/
    public FriendRecyclerAdapter(ArrayList<Person> data_source,Context context,RecyclerItemClick item_click_handler)//
     {
         this.data_source = data_source;
         this.mcontext= context;
         this.item_click_handler=item_click_handler;

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
                return new PersonItemViewHolder(item_view,item_click_handler);
            default:item_view=layout_inflater.inflate(R.layout.person_item,parent,false);
                return new PersonItemViewHolder(item_view,item_click_handler);
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
        RoundedImageView person_image=(RoundedImageView)vh.profile_pic;
        mImageLoader = VolleySingleton.getInstance(mcontext).getImageLoader();
        String img_url = String.format("https://graph.facebook.com/%s/picture?type=normal", p.id);
        //Log.d(TAG,"Image Url for ImageLoader is"+img_url);
        mImageLoader.get(img_url, ImageLoader.getImageListener(person_image,
                R.drawable.ic_person_black_36dp, R.drawable.ic_person_black_36dp));
        /******** End of Test Data ********/
    }

    private void configureRecyclerHead(HeaderRecyclerViewHolder vh)
    {
        vh.title.setText("Current Friends");
        vh.head_image.setBackgroundResource(R.drawable.community);
    }
    /******** End of Configuring of view holders ***************/
}

