package com.scorelab.kute.kute.PrivateVehicles.App.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.volley.toolbox.ImageLoader;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.Interfaces.RecyclerItemClick;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.HeaderRecyclerViewHolder;
import com.scorelab.kute.kute.PrivateVehicles.App.ViewHolders.PersonItemViewHolder;
import com.scorelab.kute.kute.R;

import java.util.ArrayList;

/**
 * Created by nipunarora on 10/06/17.
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int header = 7;
    private static final int general_list_item = 77;
    private ArrayList<Person> data_source;
    private ImageLoader mImageLoader;
    private final String TAG = "FriendRecyclerAdapter";
    Context mcontext;
    RecyclerItemClick item_click_handler;
    String source;


    /**************** Constructor ****/
    public FriendRecyclerAdapter(String source1,ArrayList<Person> data_source, Context context, RecyclerItemClick item_click_handler)//
    {
        this.data_source = data_source;
        this.mcontext = context;
        this.item_click_handler = item_click_handler;
        this.source=source1;

    }
    /******** End of Constructor ******/

    /******************* Overrides ******/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View item_view = null;
        switch (viewType) {
            case header:
                item_view = layout_inflater.inflate(R.layout.recycler_head, parent, false);
                return new HeaderRecyclerViewHolder(item_view);
            case general_list_item:
                item_view = layout_inflater.inflate(R.layout.person_item, parent, false);
                return new PersonItemViewHolder(item_view, item_click_handler);
            default:
                item_view = layout_inflater.inflate(R.layout.person_item, parent, false);
                return new PersonItemViewHolder(item_view, item_click_handler);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case header:
                configureRecyclerHead((HeaderRecyclerViewHolder) holder);
                break;
            case general_list_item:
                configureGeneralItem((PersonItemViewHolder) holder, position);
                break;
            default:
                configureGeneralItem((PersonItemViewHolder) holder, position);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return data_source.size() + 1;//added 1 for header tile
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return header;
        else
            return general_list_item;
    }


    /***************** End of Overrides *************/
    /************ Configuring View Holders ****************/
    private void configureGeneralItem(PersonItemViewHolder vh, int position) {
        Person p = data_source.get(position - 1);
        vh.name.setText(p.name);
        /********* Test ******/
        RoundedImageView person_image =  vh.profile_pic;
        mImageLoader = VolleySingleton.getInstance(mcontext).getImageLoader();
        if(p.img_base64.equals("null") || p.img_base64==null){
            String img_url = String.format("https://graph.facebook.com/%s/picture?type=normal", p.id);
            //Log.d(TAG,"Image Url for ImageLoader is"+img_url);
            mImageLoader.get(img_url, ImageLoader.getImageListener(person_image,
                    R.drawable.ic_person_black_36dp, R.drawable.ic_person_black_36dp));
        }else {
            //TODO This task of converting base64 to bitmap can be offloaded to a handler Thread
            Bitmap bm=base64ToBitmap(p.img_base64);
            person_image.setImageBitmap(bm);
        }



        /******** End of Test Data ********/
    }

    private void configureRecyclerHead(HeaderRecyclerViewHolder vh) {
        vh.title.setText(source);
        vh.head_image.setBackgroundResource(R.drawable.community);
    }
    /******** End of Configuring of view holders ***************/
    //Method to convert Base64 to bitmap
    private Bitmap base64ToBitmap(String base64)
    {
        byte []imageBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
        //imageView.setImageBitmap(decodedImage);
    }
}

