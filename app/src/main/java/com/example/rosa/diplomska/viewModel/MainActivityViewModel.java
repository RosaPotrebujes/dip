package com.example.rosa.diplomska.viewModel;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Ignore;
import android.content.DialogInterface;
import android.databinding.ObservableBoolean;
import android.util.Log;

import com.example.rosa.diplomska.detector.LocationDetector;
import com.example.rosa.diplomska.detector.MasterDetector;
import com.example.rosa.diplomska.detector.UserActivity;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.fragment.AddFriendDialogFragment;
import com.example.rosa.diplomska.volley.DataProvider;

import java.util.List;
import java.util.Observable;

public class MainActivityViewModel extends ViewModel implements
        LocationDetector.OnLocationDetectedListener, UserActivity.OnActvityDoneListener {
    private MainNavigator navigator;
    private DataProvider dp;
    private AddFriendDialogFragment df;
    private MasterDetector masterDetector;
    private UserActivity mUserActivity;


    public MainActivityViewModel(MainNavigator navigator) {
        this.navigator = navigator;
        this.dp = new DataProvider(navigator);
    }
    public MainActivityViewModel(MainNavigator navigator, MasterDetector masterDetector) {
        this.navigator = navigator;
        this.masterDetector = masterDetector;
        this.dp = new DataProvider(navigator);
    }
    public MainActivityViewModel() {
    }

    public void setDataProvider(MainNavigator navigator) {
        this.dp = new DataProvider(navigator);
    }

    public void setMainNavigator(MainNavigator navigator) {
        this.navigator = navigator;
    }

    public void getUserPosts() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.getUserPosts();
            }
        });
        thread.start();
        //dp.getUserPosts();
    }

    public void getHomePosts() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.getHomePosts();
            }
        });
        thread.start();
        //dp.getHomePosts();
    }

    public void getUserFriends() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.loadUserFriends();
            }
        });
        thread.start();
        //dp.loadUserFriends();
    }

    public void friendRequestAccepted(final User friend) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.acceptFriendRequest(friend);
            }
        });
        thread.start();
        //dp.acceptFriendRequest(friend);
    }

    public void friendRequestDeclined(final User friend) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.declineFriendRequest(friend);
            }
        });
        thread.start();
        //dp.declineFriendRequest(friend);
    }

    public void deleteFriend(final User friend) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.deleteFriend(friend);
            }
        });
        thread.start();
        //dp.deleteFriend(friend);
    }

    public void getUserPendingFriends() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                dp.loadUserPendingFriends();
            }
        });
        thread.start();
        //dp.loadUserPendingFriends();
    }

    public void onButtonAddFriendClick() {
        df = navigator.getAddFriendDialogFragment();
        navigator.clickedAddFriend();
    }
    public void clickedFindFriend(String newFriend) {
        if(navigator.getUser().getUsername().equalsIgnoreCase(newFriend)){
            navigator.mnAlertDialog("Really?","...");
        } else {
            dp.sendFriendRequest(newFriend);
        }
    }

    //profile fragment
    public void onEditDataClick() {
        navigator.beginEditMode();
    }
    public void onConfirmEditClick() {
        navigator.endEditMode();
    }
    public void onCancelEditClick() {
        navigator.endEditMode();
    }

    public void onFabClick() {
        masterDetector.startDetection();
    }

    public void setUserActivity(UserActivity ua) {
        this.mUserActivity = ua;
    }
    //poslušamo za zaznano pesem

    public void onSongDetected(String song) {
        if(mUserActivity != null) {
            mUserActivity.setUaMusic(song);
            mUserActivity.setGotMusic(true);
        }
        //navigator.mnAlertDialog("Got a song!",song);
    }

    public void onLocationDetected(String location) {
        if(mUserActivity != null) {
            mUserActivity.setUaLocation(location);
            mUserActivity.setGotLocation(true);
        }
        //navigator.mnAlertDialog("Got a song!",location);
    }

    public void onPeopleDetected(int people) {
        if(mUserActivity != null) {
            mUserActivity.setUaPeople(people);
            mUserActivity.setGotPeople(true);
        }
        //navigator.mnAlertDialog("Number of people",Integer.toString(people));
    }

    public void onMotionDetected(String motion) {
        if(mUserActivity != null) {
            mUserActivity.setUaMovement(motion);
            mUserActivity.setGotMovement(true);
        }
    }
    @Override
    public void onActivityDone() {
        //String mPost = "is in " + mUserActivity.getUaLocation()
        //        + ", " + mUserActivity.getUaMovement() +
        //        " and listening to \"" +
        //        mUserActivity.getUaMusic() + "\" with " +
        //        mUserActivity.getUaPeople() + " other people.";
        String mPost = "is ";
        if(!mUserActivity.getUaLocation().isEmpty()) {
            mPost += " in " + mUserActivity.getUaLocation() + ", ";
        }
        if(!mUserActivity.getUaMovement().isEmpty()) {
            mPost += mUserActivity.getUaMovement();
        } else {
            mPost += "hanging out";
        }
        if(!mUserActivity.getUaMusic().isEmpty()) {
            mPost += " and listening to " + mUserActivity.getUaMusic();
        }
        if(mUserActivity.getUaPeople() > 0) {
            mPost += " with " + mUserActivity.getUaPeople() + " other people";
        }
        mPost += ".";
        masterDetector.activityDetectedDialog("Activity detected",mPost);
    }
    public void postDetectedActivity(Post post) {
        navigator.getUser().addPost(post);
        //navigator.getProfileFragment().addUserPost(post);
        dp.insertUserPost(post);
    }
}
