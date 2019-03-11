package com.example.rosa.diplomska.view.fragment;

import android.app.ActionBar;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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

    SwipeRefreshLayout homeSwipeRefresh;

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

        homeSwipeRefresh = binding.homeSwipeRefresh;
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        //@ColorInt int color = typedValue.data;
        homeSwipeRefresh.setColorSchemeResources(typedValue.resourceId, R.color.gray);
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        homeSwipeRefresh.setProgressBackgroundColorSchemeColor(typedValue.data);

        binding.recyclerHome.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerHome.setLayoutManager(mLayoutManager);
        binding.recyclerHome.setItemAnimator(new DefaultItemAnimator());

        //getPosts
        if(posts == null)
            posts = new ObservableArrayList<>();

        ra = new RecyclerAdapter(posts);
        binding.recyclerHome.setAdapter(ra);

        if(posts.isEmpty()) {
            mavm.getHomePosts();
        }

        homeSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mavm != null) {
                            mavm.getHomePosts();
                        }
                    }
                }, 2500);
            }
        });

        setRetainInstance(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }
    public void notifyHomeAdapterOfChange() {
        ra.notifyDataSetChanged();
        //SwipeRefreshLayout.LayoutParams lp = binding.homeSwipeRefresh.getLayoutParams();
        //lp.height = 4000;
        //binding.homeSwipeRefresh.setLayoutParams(lp);
        //binding.homeSwipeRefresh.setLayoutParams(new SwipeRefreshLayout.LayoutParams(SwipeRefreshLayout.LayoutParams.MATCH_PARENT, SwipeRefreshLayout.LayoutParams.MATCH_PARENT));

        binding.homeSwipeRefresh.getLayoutParams().height = SwipeRefreshLayout.LayoutParams.MATCH_PARENT;
        binding.homeSwipeRefresh.requestLayout();

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
        //binding.progressBarHome.setVisibility(View.VISIBLE);
        homeSwipeRefresh.setRefreshing(true);
    }
    public void setHomeProgressBarInvisible() {
        //binding.progressBarHome.setVisibility(View.INVISIBLE);
        homeSwipeRefresh.setRefreshing(false);
    }
}