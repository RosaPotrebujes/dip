package com.example.rosa.diplomska.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.databinding.PostBinding;


public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //protected ImageView fav;
   // protected ImageView userPic;
    private PostBinding mBinding; //ime xmlja+Binding

    public PostViewHolder(PostBinding binding) {
        super(binding.getRoot());
        mBinding = binding;

        //userPic = (ImageView) binding.getRoot().findViewById(R.id.home_user_pic);
        //ikonca za favourite
        //mBinding.homeFavPost.setOnClickListener(this);

//        without binding
//        fav = (ImageView) binding.getRoot().findViewById(R.id.home_fav_post);
//        fav.setOnClickListener(this);
    }

    public void bind(@NonNull Post post) {
        mBinding.setPost(post);
        mBinding.executePendingBindings();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //za favourite opcija
            /*case R.id.home_fav_post:
                if (v.getTag().toString().equals("unfav")) {
                    mBinding.homeFavPost.setImageResource(R.drawable.star_full_icon);
                    mBinding.homeFavPost.setTag("fav");
                } else{
                    mBinding.homeFavPost.setImageResource(R.drawable.ic_star_border);
                    mBinding.homeFavPost.setTag("unfav");
                }
                break;
            default:
                break;*/
        }
    }
}



/*
public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected TextView userName;
    protected TextView postContent;
    protected ImageView userPic;
    protected ImageView fav;


    public PostViewHolder(View v) {
        super(v);
        userName = (TextView) v.findViewById(R.id.home_user_name);
        postContent = (TextView) v.findViewById(R.id.home_activity);
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
*/