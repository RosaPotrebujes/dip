package com.example.rosa.diplomska.model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.rosa.diplomska.BR;

public class LoginCredentials extends BaseObservable{
    public String loginUsername;
    public String loginPassword;

    public LoginCredentials() {
        this.loginUsername = "";
        this.loginPassword = "";
    }

    @Bindable
    public String getLoginUsername() {
        return this.loginUsername;
    }
    public void setLoginUsername(String username) {
        this.loginUsername = username;
        notifyPropertyChanged(BR.loginUsername);
    }

    @Bindable
    public String getLoginPassword() {
        return this.loginPassword;
    }
    public void setLoginPassword(String password) {
        this.loginPassword = password;
        notifyPropertyChanged(BR.loginPassword);
    }
}
