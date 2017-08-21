package com.example.rosa.diplomska;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView userName;
    protected TextView activity;
    protected ImageView userPic;
    protected ImageView fav;

    public PostViewHolder(View v) {
        super(v);
        userName = (TextView) v.findViewById(R.id.home_user_name);
        activity = (TextView) v.findViewById(R.id.home_activity);
        userPic = (ImageView) v.findViewById(R.id.home_user_pic);
        fav = (ImageView) v.findViewById(R.id.home_fav_post);
        fav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.home_fav_post:
                if (v.getTag().toString().equals("unfav")) {
                    ImageView v2 = (ImageView) v;
                    v2.setImageResource(R.drawable.star_full_icon);
                    v.setTag("fav");
                } else{
                    ImageView v2 = (ImageView) v;
                    v2.setImageResource(R.drawable.ic_star_border);
                    v.setTag("unfav");
                }
                break;
            default:
                break;
        }
    }
}
