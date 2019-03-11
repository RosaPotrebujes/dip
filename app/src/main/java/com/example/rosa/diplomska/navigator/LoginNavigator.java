package com.example.rosa.diplomska.navigator;


import android.app.FragmentManager;
import android.content.Context;

import com.example.rosa.diplomska.view.fragment.LoginFragment;
import com.example.rosa.diplomska.view.fragment.RegisterFragment;

public interface LoginNavigator {
    Context getLNContext();
    FragmentManager fm = null;
    android.support.v4.app.FragmentManager getLNFM();
    void setFm(android.support.v4.app.FragmentManager fm);
    RegisterFragment getRegisterFragment();
    LoginFragment getLoginFragment();
    void goToRegisterFragment();
    void goToLoginFragment();
    void startMainActivity();
    void lnAlertDialog(String title, String username);
    void disableLoginInterface();
    void enableLoginInterface();
    void disableRegisterInterface();
    void enableRegisterInterface();
}
