package com.example.rosa.diplomska;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    //recycler view
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Post> posts;

    ImageView fav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.fragment_home, null);

/*        MainActivity ma = (MainActivity) getActivity();
        if (ma.getSupportActionBar() != null) {
            ma.getSupportActionBar().setTitle(R.string.menu_home);
        }*/

        //recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_home);
        // i shall see about this
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getPosts();
        RecyclerAdapter ra = new RecyclerAdapter(posts);
        mRecyclerView.setAdapter(ra);
        setRetainInstance(true);

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    private void getPosts(){
        posts = new ArrayList<>();
        posts.add(new Post("Eva Bathory", "Taking a bath while listening to Cradle of Filth with 2 other people."));
        posts.add(new Post("Janez Novak", "Plese na Lady Gaga - Alejandro v Krizankah z 20 ljudmi."));
        posts.add(new Post("Aljana Kovac", "Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi."));
        posts.add(new Post("Ime Priimek", "lalalala potatoooo nanananananana la la la la lalalala la la la"));
        posts.add(new Post("Geralt of Rivia", "is swish a swasha a swunking"));
        posts.add(new Post("Dale", "sha sha sha sha sha sha sha sha sha sha sha"));
    }
}