package com.example.rosa.diplomska;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> posts;

    public RecyclerAdapter (List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.post, parent, false);

        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.activity.setText(post.getActivity());
        holder.userName.setText(post.getUser());
        holder.userPic.setImageResource(R.drawable.ic_account_box_black_24dp);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

