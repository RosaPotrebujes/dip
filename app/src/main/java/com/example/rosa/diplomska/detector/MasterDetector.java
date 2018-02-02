package com.example.rosa.diplomska.detector;


import android.location.Location;

public interface MasterDetector {
    boolean checkIfBTSupported();
    boolean checkIfGPSSupported();
    boolean checkIfGooglePlayServicesAvailable();
    void askPermissions();
    void checkSensorSupport();
    void startDetection();
    void startBTDetection();
    void startLocationDetection();
    void startLocationUpdate();
    void stopLocationUpdate();
    void setUpLocationRequest();
    Location getLastKnownLocation();
}