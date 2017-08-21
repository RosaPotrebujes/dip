package com.example.rosa.diplomska;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, null);

        //mogoce bi to z naslovi lahko kje drugje...
     /*   MainActivity ma = (MainActivity) getActivity();
        if (ma.getSupportActionBar() != null) {
            ma.getSupportActionBar().setTitle(R.string.menu_about);
        }*/
        setRetainInstance(true);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(
        Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }
}