package com.example.rosa.diplomska;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Post> posts;

    ImageView editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.fragment_profile, null);

        //recycler view
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_profile);
        // i shall see about this
        mRecyclerView.setHasFixedSize(true); //izbolsa performance
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getPosts(); //dobim podatke
        RecyclerAdapter ra = new RecyclerAdapter(posts);
        mRecyclerView.setAdapter(ra);

        editProfile = (ImageView) v.findViewById(R.id.edit_profile_data);
        editProfile.setOnClickListener(this);


        setRetainInstance(true);
        return v;
    }

    private void getPosts(){
        posts = new ArrayList<>();
        posts.add(new Post("Username", "Taking a bath while listening to Cradle of Filth with 2 other people."));
        posts.add(new Post("Username", "Plese na Lady Gaga - Alejandro v Krizankah z 20 ljudmi."));
        posts.add(new Post("Username", "Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi."));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_profile_data:
                if(getView()!=null) {
                    Snackbar.make(getView(), "Edit not yet implemented", Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
