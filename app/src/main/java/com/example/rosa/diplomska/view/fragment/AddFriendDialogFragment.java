package com.example.rosa.diplomska.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.FragmentAddFriendBinding;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;


public class AddFriendDialogFragment extends DialogFragment {

    FragmentAddFriendBinding binding;
    MainActivityViewModel vm;

    public AddFriendDialogFragment() {
    }

    public static AddFriendDialogFragment newInstance() {
        AddFriendDialogFragment dialog = new AddFriendDialogFragment();
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.fragment_add_friend, null, false);
       // binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false);

        if(getActivity() != null) {
            MainActivity ma = (MainActivity) getActivity();
            MainNavigator mn = ma.getNavigator();
            //to me rešuje pred illegal state
            vm = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            vm.setMainNavigator(mn);
            vm.setDataProvider(mn);
            binding.setMavm(vm);
        }
        disableFindButton();
        binding.editTextAddUser.addTextChangedListener(addUserWatcher);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        binding.progressBarFindingUser.setVisibility(View.INVISIBLE);
      //  setRetainInstance(true);
        return binding.getRoot();
    }
    public void setProgressBarInvisible() {
        binding.progressBarFindingUser.setVisibility(View.INVISIBLE);
    }

    public void setProgressBarVisible() {
        binding.progressBarFindingUser.setVisibility(View.VISIBLE);
    }

    public void enableFindButton() {
        binding.btnFindFriend.setEnabled(true);
    }

    public void disableFindButton() {
        binding.btnFindFriend.setEnabled(false);
    }

    public void enableSearch() {
        binding.textInputLayoutAddUser.setEnabled(true);
    }

    public void disableSearch() {
        binding.textInputLayoutAddUser.setEnabled(false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // setRetainInstance(true);
    }

    private final TextWatcher addUserWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutUsername.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutUsername.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() < 3)
                disableFindButton();
            else
                enableFindButton();
        }
    };
}
