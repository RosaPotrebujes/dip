package com.example.rosa.diplomska.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.FriendBinding;


public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //protected ImageView fav;
    // protected ImageView userPic;
    private FriendBinding mBinding; //ime xmlja+Binding
    private FriendViewHolderClickListener listener;
    private boolean isPending;

    public FriendViewHolder(FriendBinding binding, FriendViewHolderClickListener listener, boolean pending) {
        super(binding.getRoot());
        mBinding = binding;
        isPending = pending;
        mBinding.deleteFriend.setOnClickListener(this);
        if(mBinding.acceptFriend.getVisibility() == View.VISIBLE)
            mBinding.acceptFriend.setOnClickListener(this);
        this.listener = listener;
    }

    public void bind(@NonNull User friend) {
        mBinding.setFriend(friend);
        mBinding.executePendingBindings();
    }

    //public User getFriend(){
  //      return mBinding.getFriend();
//    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.accept_friend:
                listener.clickedAcceptFriendRequest(mBinding.getRoot());
                break;
            case R.id.delete_friend:
                if(isPending)
                    listener.clickedDeclineFriendRequest(mBinding.getRoot());
                else
                    listener.clickedDeleteFriend(mBinding.getRoot());
                break;
            default:
                break;
        }
    }
}