package com.example.rosa.diplomska.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.FragmentRegisterBinding;
import com.example.rosa.diplomska.model.Registration;
import com.example.rosa.diplomska.model.ResourceProvider;
import com.example.rosa.diplomska.navigator.LoginNavigator;
import com.example.rosa.diplomska.view.activity.LoginActivity;
import com.example.rosa.diplomska.viewModel.LoginActivityViewModel;


public class RegisterFragment extends Fragment {

    FragmentRegisterBinding binding;
//    RegisterViewModel viewModel;
    LoginActivityViewModel lavm;
    //za to da lahko dobim string iz resources.
    private ResourceProvider mResourceProvider;
    public ResourceProvider getResourceProvider() {
        if (mResourceProvider == null)
            mResourceProvider = new ResourceProvider(getContext());
        return mResourceProvider;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //brez data bindinga
       // View v = inflater.inflate(R.layout.fragment_register, null);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);

        View v = binding.getRoot();

        Registration registration = new Registration();

        if(getActivity() != null) {
            LoginActivity la = (LoginActivity) getActivity();
            LoginNavigator l = la.getNavigator();
            lavm = ViewModelProviders.of(getActivity()).get(LoginActivityViewModel.class);
            lavm.setNavigator(l);
            lavm.setDataProvider(l);
            lavm.setResourceProvider(getResourceProvider());
            lavm.setRegistration(registration);
            binding.setLoginActivityViewModel(lavm);
        }

        binding.executePendingBindings();

        //textWatcher
        binding.editTextRegisterUsername.addTextChangedListener(usernameWatcher);
        binding.editTextRegisterEmail.addTextChangedListener(emailWatcher);
        binding.editTextRegisterPassword.addTextChangedListener(passWatcher);
        binding.editTextRegisterConfirmPassword.addTextChangedListener(confPassWatcher);

        setRetainInstance(true);
        return v;
    }

    public void disableRegisterInterface() {
        binding.progressBarRegister.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);
        binding.editTextRegisterUsername.setEnabled(false);
        binding.editTextRegisterEmail.setEnabled(false);
        binding.editTextRegisterPassword.setEnabled(false);
        binding.editTextRegisterConfirmPassword.setEnabled(false);
    }

    public void enableRegisterInterface() {
        binding.progressBarRegister.setVisibility(View.INVISIBLE);
        binding.btnRegister.setEnabled(true);
        binding.editTextRegisterUsername.setEnabled(true);
        binding.editTextRegisterEmail.setEnabled(true);
        binding.editTextRegisterPassword.setEnabled(true);
        binding.editTextRegisterConfirmPassword.setEnabled(true);
    }


    private final TextWatcher usernameWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutUsername.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutUsername.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                lavm.getRegistration().setRegisterUsernameError("");//viewModel.getRegistration().setRegisterUsernameError("");
                //binding.getRegistration().setRegisterUsernameError("");
                //binding.textInputLayoutRegisterUsername.setError("");
            else
                lavm.validateUsername();//viewModel.validateUsername();
        }
    };

    private final TextWatcher passWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutPass.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutPass.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                lavm.getRegistration().setRegisterPasswordError("");//viewModel.getRegistration().setRegisterPasswordError("");//binding.textInputLayoutRegisterPassword.setError("");
            else
                lavm.validatePassword();//viewModel.validatePassword();
        }
    };

    private final TextWatcher confPassWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutConfPass.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutConfPass.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                lavm.getRegistration().setRegisterPasswordError("");//viewModel.getRegistration().setRegisterConfirmPasswordError("");//binding.textInputLayoutRegisterConfirmPassword.setError("");
            else
                lavm.validateConfirmPassword();//viewModel.validateConfirmPassword();
        }
    };

    private final TextWatcher emailWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutEmail.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutEmail.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                lavm.getRegistration().setRegisterEmailError("");//viewModel.getRegistration().setRegisterEmailError("");//binding.textInputLayoutRegisterEmail.setError("");//textLayoutEmail.setError("");
            else
                //vnos se je spremenil
                lavm.validateEmail();//viewModel.validateEmail();
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}