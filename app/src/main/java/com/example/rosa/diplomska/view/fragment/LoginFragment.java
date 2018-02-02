package com.example.rosa.diplomska.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.FragmentLoginBinding;
import com.example.rosa.diplomska.model.LoginCredentials;
import com.example.rosa.diplomska.navigator.LoginNavigator;
import com.example.rosa.diplomska.view.activity.LoginActivity;
import com.example.rosa.diplomska.viewModel.LoginActivityViewModel;

public class LoginFragment extends Fragment {
    FragmentLoginBinding binding;
    LoginCredentials lc;
    LoginActivityViewModel lavm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        // get the root view
        View v = binding.getRoot();

        lc = new LoginCredentials();
        binding.setLoginCredentials(lc);
        binding.executePendingBindings();

        if(getActivity() != null) {
            LoginActivity la = (LoginActivity) getActivity();
            LoginNavigator l = la.getNavigator();
            //to me rešuje pred illegal state
            lavm = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
            lavm.setNavigator(l);
            lavm.setDataProvider(l);
            binding.setLoginActivityViewModel(lavm);
        }

        setRetainInstance(true);
        return v;
    }

    public void setProgressBarVisible() {
        binding.progressBarLogin.setVisibility(View.VISIBLE);
    }
    public void setProgressBarInvisible() {
        binding.progressBarLogin.setVisibility(View.INVISIBLE);
    }

    public void disableLoginInterface() {
        setProgressBarVisible();
        binding.btnLogin.setEnabled(false);
        binding.textViewRegister.setEnabled(false);
        binding.editTextPassword.setEnabled(false);
        binding.editTextUsername.setEnabled(false);
    }
    public void enableLoginInterface() {
        setProgressBarInvisible();
        binding.btnLogin.setEnabled(true);
        binding.textViewRegister.setEnabled(true);
        binding.editTextPassword.setEnabled(true);
        binding.editTextUsername.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}