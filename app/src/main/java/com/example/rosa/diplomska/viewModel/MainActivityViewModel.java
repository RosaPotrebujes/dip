package com.example.rosa.diplomska.viewModel;

import android.arch.lifecycle.ViewModel;

import com.example.rosa.diplomska.detector.MasterDetector;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.fragment.AddFriendDialogFragment;
import com.example.rosa.diplomska.volley.DataProvider;

public class MainActivityViewModel extends ViewModel {
    MainNavigator navigator;
    DataProvider dp;
    AddFriendDialogFragment df;
    MasterDetector masterDetector;

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
        dp.getUserPosts();
    }

    public void getHomePosts() {
        dp.getHomePosts();
    }

    public void getUserFriends() {
        dp.loadUserFriends();
    }

    public void friendRequestAccepted(User friend) {
        dp.acceptFriendRequest(friend);
    }

    public void friendRequestDeclined(User friend) {
        dp.declineFriendRequest(friend);
    }

    public void deleteFriend(User friend) {
        dp.deleteFriend(friend);
    }

    public void getUserPendingFriends() {
        dp.loadUserPendingFriends();
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
}
