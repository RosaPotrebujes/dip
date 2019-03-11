package com.example.rosa.diplomska.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.FriendBinding;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private List<User> friends;
    private boolean pendingFriends;
    FriendBinding binding;
    private FriendViewHolderClickListener listener;
    FriendViewHolder holder;
    public FriendsAdapter(List<User> friends, boolean pending, FriendViewHolderClickListener listener) {
        this.friends = friends;
        this.pendingFriends = pending;
        this.listener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.friend, parent, false);
        if(!pendingFriends)
            binding.acceptFriend.setVisibility(View.INVISIBLE);
        else
            binding.acceptFriend.setVisibility(View.VISIBLE);
        holder = new FriendViewHolder(binding,listener,pendingFriends);
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.bind(friend);
    }

    public User getFriend(int position){
        //return holder.getFriend();
        return friends.get(position);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public boolean isPendingFriend() {
        return this.pendingFriends;
    }

}
