package com.example.rosa.diplomska.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Switch;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.view.activity.MainActivity;


public class SettingsFragment extends /*Fragment {*/ PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    SharedPreferences sharedPreferences;

    Switch bluetooth;
    Switch gps;
    Switch movement;
    Switch mic;

//    @Override
  //  public void onCreate(Bundle savedInstanceState){
    //    super.onCreate(savedInstanceState);
       // addPreferencesFromResource(R.xml.preference);
        //setRetainInstance(true);
  //  }

    @Override
    public void onCreatePreferences(Bundle bundle, String s){
        setPreferencesFromResource(R.xml.preference,s);
    //    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    //    onSharedPreferenceChanged(sharedPreferences,"gps");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if(preference instanceof android.support.v7.preference.ListPreference){
            if(key.equals("appTheme")){
                MainActivity ma = (MainActivity) getActivity();
//                if(sharedPreferences.getString("appTheme","1").equals("1")){
//                    Snackbar.make(getView(), "Drowner theme", Snackbar.LENGTH_SHORT).show();
//                }else if(sharedPreferences.getString("appTheme","1").equals("2")){
//                    Snackbar.make(getView(), "Vine red theme", Snackbar.LENGTH_SHORT).show();
//                }
                ma.recreate(); //da se osvezi. blink ampak meh, dela.
            }
        }
        else if(preference instanceof CheckBoxPreference){
/*            if(key.equals("gps"))
                Snackbar.make(getView(), "gps permission: "+((CheckBoxPreference) preference).isChecked(), Snackbar.LENGTH_SHORT).show();
            if(key.equals("mic"))
                Snackbar.make(getView(), "mic permission: "+((CheckBoxPreference) preference).isChecked(), Snackbar.LENGTH_SHORT).show();
            if(key.equals("bt"))
                Snackbar.make(getView(), "bt permission: "+((CheckBoxPreference) preference).isChecked(), Snackbar.LENGTH_SHORT).show();
            if(key.equals("motion"))
                Snackbar.make(getView(), "motion permission: "+((CheckBoxPreference) preference).isChecked(), Snackbar.LENGTH_SHORT).show();
            if(key.equals("automaticDetection"))
                Snackbar.make(getView(), "auto detect permission: "+((CheckBoxPreference) preference).isChecked(), Snackbar.LENGTH_SHORT).show();*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MainActivity ma = (MainActivity)getActivity();
        PackageManager pm = getActivity().getPackageManager();

     //   mListPreference = (ListPreference) getPreferenceManager().findPreference("preference_key");

        View v = inflater.inflate(R.layout.fragment_settings, null);

        if (ma.getSupportActionBar() != null) {
            ma.getSupportActionBar().setTitle(R.string.menu_settings);
        }

        bluetooth = (Switch) v.findViewById(R.id.switchBluetooth);
        gps = (Switch) v.findViewById(R.id.switchGPS);
        movement = (Switch) v.findViewById(R.id.switchMotion);
        mic = (Switch) v.findViewById(R.id.switchMicrophone);

        bluetooth.setClickable(false);
        gps.setClickable(false);
        movement.setClickable(false);
        mic.setClickable(false);

        //preverim, ce ima naprava mikrofon
        ma.hasMicrophone = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        if(ma.hasMicrophone){
            mic.setChecked(true);
        }else{
            mic.setChecked(false);
        }
        //preverim ce ima naprava bluetooth
        ma.hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if(ma.hasBluetooth){
            bluetooth.setChecked(true);
        }else{
            bluetooth.setChecked(false);
        }
        //preverim ce ima naprava gps
        ma.hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if(ma.hasGps){
            gps.setChecked(true);
        }else{
            gps.setChecked(false);
        }
        //preverim, ce ima naprava motion sensor
        ma.hasMotion = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        if(ma.hasMotion){
            movement.setChecked(true);
        }else{
            movement.setChecked(false);
        }
        //ce ker od senzorjev ni na voljo potem na to opozorimo uporabnika
        if(!ma.hasBluetooth || !ma.hasGps || !ma.hasMicrophone || !ma.hasMotion){
            v.findViewById(R.id.textViewWarningSensors).setVisibility(View.VISIBLE);
        }else{
            v.findViewById(R.id.textViewWarningSensors).setVisibility(View.INVISIBLE);
        }

        setRetainInstance(true);
        return v;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }*/
}
