package com.example.rosa.diplomska.detector;


import android.content.SharedPreferences;
import android.location.Location;

import java.io.File;

public interface MasterDetector {
    boolean checkIfBTSupported();
    boolean checkIfGPSSupported();
    boolean checkIfGooglePlayServicesAvailable();
    boolean checkIfMusicDetectionSupported();
    boolean checkIfMotionDetectionSupported();
    void askPermissions();
    void checkSensorSupport();
    void startDetection();
    void activityDetectedDialog(String title, String post);
    void startSongDetection();
    void startLocationDetection();
    void startPeopleDetection();
    void startMovementDetection();
    //void createOnChangedPreferenceListener();
    //void registerOnChangedPreferenceListener(SharedPreferences.OnSharedPreferenceChangeListener listener);
}