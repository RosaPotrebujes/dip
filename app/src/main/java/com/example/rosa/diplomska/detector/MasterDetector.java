package com.example.rosa.diplomska.detector;


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
    String getUserActivity();
    void activityDetectedDialog(String title, String post);
}