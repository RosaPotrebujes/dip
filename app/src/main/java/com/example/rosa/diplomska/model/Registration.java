package com.example.rosa.diplomska.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.rosa.diplomska.BR;

public class Registration extends BaseObservable {
    private String registerUsername;
    private String registerEmail;
    private String registerPassword;
    private String registerConfirmPassword;

    private String registerUsernameError;
    private String registerEmailError;
    private String registerPasswordError;
    private String registerConfirmPasswordError;

    private String registerEmptyFieldsError;

    public Registration(String username, String email, String password, String confirmPassword, String registerEmptyFieldsError) {
        this.registerUsername        = username;
        this.registerEmail           = email;
        this.registerPassword        = password;
        this.registerConfirmPassword = confirmPassword;
        this.registerEmailError      = registerEmptyFieldsError;
    }

    public Registration() {
        this.registerUsername             = "";
        this.registerEmail                = "";
        this.registerPassword             = "";
        this.registerConfirmPassword      = "";
        this.registerUsernameError        = "";
        this.registerEmailError           = "";
        this.registerPasswordError        = "";
        this.registerConfirmPasswordError = "";
        this.registerEmptyFieldsError     = "";
    }

    @Bindable
    public String getRegisterUsernameError() {
        return this.registerUsernameError;
    }
    public void setRegisterUsernameError(String error) {
        this.registerUsernameError = error;
        notifyPropertyChanged(BR.registerUsernameError);
    }

    @Bindable
    public String getRegisterEmailError() {
        return this.registerEmailError;
    }
    public void setRegisterEmailError(String error) {
        this.registerEmailError = error;
        notifyPropertyChanged(BR.registerEmailError);
    }

    @Bindable
    public String getRegisterPasswordError() {
        return this.registerPasswordError;
    }
    public void setRegisterPasswordError(String error) {
        this.registerPasswordError = error;
        notifyPropertyChanged(BR.registerPasswordError);
    }

    @Bindable
    public String getRegisterConfirmPasswordError() {
        return this.registerConfirmPasswordError;
    }
    public void setRegisterConfirmPasswordError(String error) {
        this.registerConfirmPasswordError = error;
        notifyPropertyChanged(BR.registerConfirmPasswordError);
    }

    @Bindable
    public String getRegisterUsername() {
        return this.registerUsername;
    }
    public void setRegisterUsername(String username) {
        this.registerUsername = username;
        notifyPropertyChanged(BR.registerUsername);
    }

    @Bindable
    public String getRegisterEmail() {
        return this.registerEmail;
    }
    public void setRegisterEmail(String email) {
        this.registerEmail = email;
        notifyPropertyChanged(BR.registerEmail);
    }

    @Bindable
    public String getRegisterPassword() {
        return this.registerPassword;
    }
    public void setRegisterPassword(String password) {
        this.registerPassword = password;
        notifyPropertyChanged(BR.registerPassword);
    }

    @Bindable
    public String getRegisterConfirmPassword() {
        return this.registerConfirmPassword;
    }
    public void setRegisterConfirmPassword(String confirmPassword) {
        this.registerConfirmPassword = confirmPassword;
        notifyPropertyChanged(BR.registerConfirmPassword);
    }

    @Bindable
    public String getRegisterEmptyFieldsError() {
        return this.registerEmptyFieldsError;
    }
    public void setRegisterEmptyFieldsError(String error) {
        this.registerEmptyFieldsError = error;
        notifyPropertyChanged(BR.registerEmptyFieldsError);
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}