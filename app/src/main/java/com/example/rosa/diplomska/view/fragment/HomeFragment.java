package com.example.rosa.diplomska.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosa.diplomska.R;
//import com.example.rosa.diplomska.databinding.FragmentHomeBinding;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.databinding.FragmentHomeBinding;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.view.adapter.RecyclerAdapter;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ObservableArrayList<Post> posts;
    private LinearLayoutManager mLayoutManager;
    RecyclerAdapter ra;
    MainActivityViewModel mavm;
    FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_home, null);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View v = binding.getRoot();

        if(getActivity() != null) {
            MainActivity ma = (MainActivity) getActivity();
            //to me rešuje pred illegal state
            mavm = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            mavm.setMainNavigator(ma.getNavigator());
            mavm.setDataProvider(ma.getNavigator());
        }

        binding.recyclerHome.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerHome.setLayoutManager(mLayoutManager);

        //getPosts
        if(posts == null)
            posts = new ObservableArrayList<>();

        ra = new RecyclerAdapter(posts);
        binding.recyclerHome.setAdapter(ra);

        if(posts.isEmpty()) {
            mavm.getHomePosts();
        }

        setRetainInstance(true);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }
    public void notifyHomeAdapterOfChange() {
        ra.notifyDataSetChanged();
    }
    public void setHomePosts(List<Post> posts) {
        this.posts.addAll(posts);
        /*for (Post p:posts) {
            if(!this.posts.contains(p)){
                this.posts.add(p);
            }
        }
        notifyHomeAdapterOfChange();*/
    }
    public ObservableArrayList<Post> getHomePosts() {
        return this.posts;
    }
    public void setHomeProgressBarVisible() {
        binding.progressBarHome.setVisibility(View.VISIBLE);
    }
    public void setHomeProgressBarInvisible() {
        binding.progressBarHome.setVisibility(View.INVISIBLE);
    }
}