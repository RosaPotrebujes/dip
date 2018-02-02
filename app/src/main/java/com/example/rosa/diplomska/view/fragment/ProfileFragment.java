package com.example.rosa.diplomska.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.databinding.FragmentProfileBinding;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.view.adapter.RecyclerAdapter;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ObservableArrayList<Post> posts;
    FragmentProfileBinding binding;
    MainActivityViewModel viewModel;
    RecyclerAdapter ra;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        //View v = inflater.inflate(R.layout.fragment_profile, null);

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_profile, container, false);
        View v = binding.getRoot();

        if(getActivity() != null) {
            MainActivity ma = (MainActivity) getActivity();
            //to me rešuje pred illegal state
            viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            viewModel.setMainNavigator(ma.getNavigator());
            viewModel.setDataProvider(ma.getNavigator());
            binding.setMavm(viewModel);
            binding.setUser(ma.getUser());
        }

    //    User u = new User(1,"username","email");
    //    Post p1 = new Post(u.getUsername(),"lalala piost papa asfasf asdad.", new Timestamp(System.currentTimeMillis()));
    //    Post p2 = new Post(u.getUsername(),"jajaja i am lorde jajaja lorde lorde lorde.", new Timestamp(System.currentTimeMillis()));
    //    Post p3 = new Post(u.getUsername(),"bababa babanana bababa baba nananananaaaa.",new Timestamp(System.currentTimeMillis()));
    //    Post p4 = new Post(u.getUsername(),"Mock mock mock mock mock mock mock mock mock mock.",new Timestamp(System.currentTimeMillis()));
    //    Post p5 = new Post(u.getUsername(),"Laaa laaaa laaaa laaa laaaa laaaaaaaaaaa la la la la lal al al al a.",new Timestamp(System.currentTimeMillis()));

        binding.recyclerProfile.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerProfile.setLayoutManager(mLayoutManager);
        if(posts == null) {
            posts = new ObservableArrayList<>();
        }
        if(posts.isEmpty()) {
            viewModel.getUserPosts();
        }

        ra = new RecyclerAdapter(posts);
        binding.recyclerProfile.setAdapter(ra);

        setRetainInstance(true);
        return v;
    }

    public void editModeOn() {
        binding.editProfileData.setVisibility(View.INVISIBLE);
        binding.editProfileConfirm.setVisibility(View.VISIBLE);
        binding.editProfileCancel.setVisibility(View.VISIBLE);
        binding.editTextProfileUsername.setEnabled(true);
        binding.editTextProfileEmail.setEnabled(true);
        binding.editTextProfileOther.setEnabled(true);
    }

    public void editModeOff() {
        binding.editProfileData.setVisibility(View.VISIBLE);
        binding.editProfileConfirm.setVisibility(View.INVISIBLE);
        binding.editProfileCancel.setVisibility(View.INVISIBLE);
        binding.editTextProfileUsername.setEnabled(false);
        binding.editTextProfileEmail.setEnabled(false);
        binding.editTextProfileOther.setEnabled(false);
    }

    public void notifyProfileAdapterOfChange() {
        ra.notifyDataSetChanged();
    }

    public void setProfilePosts(List<Post> posts) {
        this.posts.addAll(posts);
        notifyProfileAdapterOfChange();
    }

   // private void getPosts(){
   //     posts = new ArrayList<>();
   //     posts.add(new Post("Username", "Taking a bath while listening to Cradle of Filth with 2 other people.", Timestamp.valueOf("2014-08-05 18:19:03")));
   //     posts.add(new Post("Username", "Plese na Lady Gaga - Alejandro v Krizankah z 20 ljudmi.",Timestamp.valueOf("2014-08-05 20:19:03")));
   //     posts.add(new Post("Username", "Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi. Plese na techno glasbo v Cvetlicari z 10 ljudmi.",Timestamp.valueOf("2014-08-05 22:19:03")));
   // }
}
