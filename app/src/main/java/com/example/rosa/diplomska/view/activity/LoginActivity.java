package com.example.rosa.diplomska.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.ActivityLoginBinding;
import com.example.rosa.diplomska.navigator.LoginNavigator;
import com.example.rosa.diplomska.view.fragment.LoginFragment;
import com.example.rosa.diplomska.view.fragment.RegisterFragment;
import com.example.rosa.diplomska.viewModel.LoginActivityViewModel;

import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements Observer, LoginNavigator {
    private ActivityLoginBinding binding;
    private LoginActivityViewModel loginActivityViewModel;

    FragmentManager fm;
    LoginFragment lf;
    RegisterFragment rf;

    private final static String LOGIN_FRAGMENT_TAG = "LoginFragment";
    private final static String REGISTER_FRAGMENT_TAG = "RegisterFragment";
    private LoginNavigator navigator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change theme (!more bit pred klicem super on create!
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = pref.getString("appTheme", "1");
        if (theme.equals("1")) {
            setTheme(R.style.Drowner);
        } else if (theme.equals("2")) {
            setTheme(R.style.VineLight);
        }
        navigator = this;
        if(pref.getBoolean("login",false)) {
            navigator.startMainActivity();
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        fm = getSupportFragmentManager();
        navigator.setFm(fm);

        loginActivityViewModel = new LoginActivityViewModel(navigator);
        binding.setLoginViewModel(loginActivityViewModel);
        super.onCreate(savedInstanceState);

        if (binding.containerLogin != null) {
            if(savedInstanceState != null){
                return;
            }else{
               navigator.goToLoginFragment();
            }
        }
    }
    public LoginNavigator getNavigator(){
        return this.navigator;
    }

    @Override
    public RegisterFragment getRegisterFragment() {
        RegisterFragment rf = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);
        rf = (rf != null) ? rf : new RegisterFragment();
        return rf;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSupportFragmentManager().beginTransaction().commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public Context getLNContext() {
        return this;
    }
    @Override
    public android.support.v4.app.FragmentManager getLNFM() {
        return this.fm;
    }
    @Override
    public LoginFragment getLoginFragment() {
        LoginFragment lf = (LoginFragment) this.fm.findFragmentByTag(LOGIN_FRAGMENT_TAG);
        lf = (lf != null) ? lf : new LoginFragment();
        return lf;
    }
    @Override
    public void goToRegisterFragment() {
        if(findViewById(R.id.container_login)!= null) {
            FragmentManager fm = this.fm;//getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            RegisterFragment rf = (RegisterFragment) getSupportFragmentManager().findFragmentByTag(REGISTER_FRAGMENT_TAG);
            rf = (rf != null) ? rf : new RegisterFragment();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
            ft.replace(R.id.container_login, rf, REGISTER_FRAGMENT_TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
    @Override
    public void goToLoginFragment() {
        if (findViewById(R.id.container_login) != null) {//binding.containerLogin != null){
            FragmentTransaction ft = this.fm.beginTransaction();
            LoginFragment lf = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
            lf = (lf != null) ? lf : new LoginFragment();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
            ft.add(R.id.container_login, lf, LOGIN_FRAGMENT_TAG);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }
    @Override
    public void lnAlertDialog(String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);//new AlertDialog.Builder(new ContextThemeWrapper(loginActivity, R.style.Drowner));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void disableLoginInterface() {
        getLoginFragment().disableLoginInterface();
    }

    @Override
    public void enableLoginInterface() {
        getLoginFragment().enableLoginInterface();
    }

    @Override
    public void disableRegisterInterface() {
        getRegisterFragment().disableRegisterInterface();
    }

    @Override
    public void enableRegisterInterface() {
        getRegisterFragment().enableRegisterInterface();
    }

    @Override
    public void startMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    public void setFm(android.support.v4.app.FragmentManager fm){
        this.fm = fm;

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        fm.executePendingTransactions();
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle instanceState) {
        super.onRestoreInstanceState(instanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        fm.executePendingTransactions();
        navigator = this;
    }

    @Override
    public void update(Observable o, Object arg) {
    }
    public LoginActivityViewModel getlvm() {
        return this.binding.getLoginViewModel();
    }

    public ActivityLoginBinding getBinding() {
        return this.binding;
    }

}