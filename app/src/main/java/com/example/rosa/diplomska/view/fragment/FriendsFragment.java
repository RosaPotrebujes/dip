package com.example.rosa.diplomska.view.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
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
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.FragmentFriendsBinding;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.view.adapter.FriendViewHolderClickListener;
import com.example.rosa.diplomska.view.adapter.FriendsAdapter;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment implements FriendViewHolderClickListener {
    MainActivityViewModel viewModel;
    FragmentFriendsBinding binding;

    RecyclerView friendsRecyclerView;
    RecyclerView pendingFriendsRecyclerView;

    LinearLayoutManager friendsLayoutManager;
    LinearLayoutManager pendingFriendsLayoutManager;

    FriendsAdapter friendsAdapter;
    FriendsAdapter pendingFriendsAdapter;

    ObservableArrayList<User> friends;
    ObservableArrayList<User> pendingFriends;

    MainActivity ma;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);

        if(getActivity() != null) {
            ma = (MainActivity) getActivity();
            MainNavigator mn = ma.getNavigator();
            //to me rešuje pred illegal state
            viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            viewModel.setMainNavigator(mn);
            viewModel.setDataProvider(mn);
            binding.setMavm(viewModel);
        }


        friendsRecyclerView = binding.recyclerFriends;
        friendsRecyclerView.setHasFixedSize(true);
        pendingFriendsRecyclerView = binding.recyclerPendingFriends;
        pendingFriendsRecyclerView.setHasFixedSize(true);

        friendsLayoutManager = new LinearLayoutManager(getContext());
        friendsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pendingFriendsLayoutManager = new LinearLayoutManager(getContext());
        pendingFriendsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        friendsRecyclerView.setLayoutManager(friendsLayoutManager);
        pendingFriendsRecyclerView.setLayoutManager(pendingFriendsLayoutManager);

        if(friends == null) {
            friends = new ObservableArrayList<>();
        }
        if(pendingFriends == null) {
            pendingFriends = new ObservableArrayList<>();
        }
        if(friends.isEmpty() && ma != null) {
            for (User f : ma.getUser().getFriends()) {
                if (!f.getPending()) {
                    friends.add(f);
                }
            }
            //viewModel.getUserFriends();
        }

        if(pendingFriends.isEmpty() && ma != null) {
            for (User f : ma.getUser().getFriends()) {
                if (f.getPending()) {
                    pendingFriends.add(f);
                }
            }
            //viewModel.getUserPendingFriends();
        }

        friendsAdapter = new FriendsAdapter(friends,false, this);
        pendingFriendsAdapter = new FriendsAdapter(pendingFriends, true, this);
        friendsRecyclerView.setAdapter(friendsAdapter);
        pendingFriendsRecyclerView.setAdapter(pendingFriendsAdapter);

        View v = binding.getRoot();
        setRetainInstance(true);
        return v;
    }
    public List<User> getMockFriends() {
        ArrayList<User> f = new ArrayList<>();
        User u1 = new User(1,"Eva Bathory","eva.bathory@eternalife.com", "this is description", null, null);
        User u2 = new User(2,"Janez Novak","janez.novak@tralala.si","",null,null);
        User u3 = new User(3,"Aljana Kovac","aljana@jana.ja","description",null,null);
        User u4 = new User(4,"Geralt of Rivia","whitewolf@witcher.si","lalalasdasd",null,null);
        f.add(u1);
        f.add(u2);
        f.add(u3);
        f.add(u4);
        return f;
    }
    public List<User> getMockPendingFriends() {
        ArrayList<User> f = new ArrayList<>();
        User u1 = new User(5,"Alejandro","alealejandro@eternalife.com", "this is description", null, null);
        User u2 = new User(6,"Roberto","lalalal@emailalalala.la","",null,null);
        f.add(u1);
        f.add(u2);
        return f;
    }
    public int getUserIdFromPreferences() {
        String home_posts_URL = "http://192.168.1.119/ada_login_api/Source_Files/index.php";
        String home_posts_TAG = "HOME_REQUEST_TAG";
        SharedPreferences pref = this.getActivity().getApplicationContext().getSharedPreferences("myPref", 0);
        int userId = pref.getInt("userId",-1);
        return userId;
    }

    public void notifyFriendsAdapterOfChange() {
        friendsAdapter.notifyDataSetChanged();
    }
    public void notifyPendingFriendsAdapterOfChange() {
        pendingFriendsAdapter.notifyDataSetChanged();
        friendsAdapter.getItemCount();
    }

    public boolean isFriendsAdapterNull() {
        return friendsAdapter == null;
    }

    public boolean isPendingFriendsAdapterNull() {
        return pendingFriendsAdapter == null;
    }
    @Override
    public void clickedAcceptFriendRequest(View v) {
        int friendPosition = pendingFriendsLayoutManager.getPosition(v);
        User friend = pendingFriendsAdapter.getFriend(friendPosition);
        pendingFriends.remove(friend);
        friends.add(friend);
        viewModel.friendRequestAccepted(friend);
        pendingFriendsAdapter.notifyDataSetChanged();
        friendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickedDeclineFriendRequest(View v) {
        int friendPosition = pendingFriendsLayoutManager.getPosition(v);
        User friend = pendingFriendsAdapter.getFriend(friendPosition);
        pendingFriends.remove(friend);
        viewModel.friendRequestDeclined(friend);
        pendingFriendsAdapter.notifyDataSetChanged();
    }

    @Override
    public void clickedDeleteFriend(View v) {
        int friendPosition = friendsLayoutManager.getPosition(v);
        User friend = friendsAdapter.getFriend(friendPosition);
        friends.remove(friend);
        viewModel.deleteFriend(friend);
        friendsAdapter.notifyDataSetChanged();
    }

    public void addUserToPendingFriends(User friend) {
        pendingFriends.add(friend);
        notifyPendingFriendsAdapterOfChange();
    }
    public void addUserToFriends(User friend) {
        friends.add(friend);
        notifyFriendsAdapterOfChange();
    }
    public void removeUserFromPendingFriends(User friend) {
        pendingFriends.remove(friend);
        notifyPendingFriendsAdapterOfChange();
    }
    public void removeUserFromFriends(User friend) {
        friends.remove(friend);
        notifyFriendsAdapterOfChange();
    }

    public ObservableArrayList<User> getPendingFriends() {
        return this.pendingFriends;
    }
    public void setPendingFriends(ObservableArrayList<User> f) {
        this.pendingFriends.addAll(f);// = f;
        notifyPendingFriendsAdapterOfChange();
        //this.pendingFriends.addAll(f);
    }
    public void setPendingFriends(List<User> f) {
        this.pendingFriends.addAll(f);// = f;
        notifyPendingFriendsAdapterOfChange();
        //this.pendingFriends.addAll(f);
    }
    public ObservableArrayList<User> getFriends() {
        return this.friends;
    }
    public void setFriends(ObservableArrayList<User> f) {
        this.friends.addAll(f);// = f;
        notifyFriendsAdapterOfChange();
        //this.friends.addAll(f);
    }
    public void setFriends(List<User> f) {
        this.friends.addAll(f);// = f;
        notifyFriendsAdapterOfChange();
        //this.friends.addAll(f);
    }
}
