package com.scorelab.kute.kute.PrivateVehicles.App.Fragments.FrameFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.scorelab.kute.kute.PrivateVehicles.App.Activities.PersonDetail;
import com.scorelab.kute.kute.PrivateVehicles.App.DataModels.Person;
import com.scorelab.kute.kute.PrivateVehicles.App.RoundedImageView;
import com.scorelab.kute.kute.PrivateVehicles.App.Utils.VolleySingleton;
import com.scorelab.kute.kute.R;


/**
 * Created by nipunarora on 18/06/17.
 */

public class FriendFrame extends Fragment {
    View v;
    private final String TAG="FriendFrame";
    ImageLoader mImageLoader;
    CardView firend_frame_card;
    public FriendFrame() {}
    //TODO load the data source through Args
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.person_item,container,false);
    /************************************ Setting up Person_item Layout by getting data from the args ***************/

        if(getArguments()!=null)//When Called for other places done for now
        {
            /************************ Setting Up the friend Frame with Person Information *********/
            final Person top_friend=(Person) getArguments().getSerializable("Friend_1");
            RoundedImageView person_image = (RoundedImageView) v.findViewById(R.id.personimg);
            firend_frame_card=(CardView)v.findViewById(R.id.friendFrameCard);
            firend_frame_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getContext(), PersonDetail.class);
                    i.putExtra("Person",top_friend);
                    i.putExtra("isAFriend",true);
                    startActivity(i);
                }
            });
            TextView person_name = (TextView) v.findViewById(R.id.name);
            person_name.setText(top_friend.name);
            mImageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();
            String img_url = String.format("https://graph.facebook.com/%s/picture?type=normal", top_friend.id);
            Log.d(TAG,"Image Url for ImageLoader is"+img_url);
            mImageLoader.get(img_url, ImageLoader.getImageListener(person_image,
                    R.drawable.ic_person_black_36dp, R.drawable.ic_person_black_36dp));
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()==null) {
            RoundedImageView x = (RoundedImageView) v.findViewById(R.id.personimg);
            Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.samplperson);
            Bitmap scaled = Bitmap.createScaledBitmap(icon, 60, 60, true);
            x.setImageBitmap(scaled);
        }
    }
}
