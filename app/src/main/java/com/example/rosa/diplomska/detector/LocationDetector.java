package com.example.rosa.diplomska.detector;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.rosa.diplomska.view.activity.MainActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class LocationDetector extends Application {
    private final static int REQUEST_CHECK_SETTINGS = 3;
    MainActivity mMainActivity;
    LocationRequest mLocationRequest;
    Location mLastKnownLocation;
    List<Address> mAddresses;
    String mAddress;

    String mError = "";
    boolean mLocationStart = false;
    boolean mLocationEnd = false;

    private long UPDATE_INTERVAL = 10 * 1000;  // 10 secs
    private long FASTEST_INTERVAL = 5 * 1000; // 5 sec
    private LocationSettingsRequest mLocationSettingsRequest;
    private Task<LocationSettingsResponse> checkLocationSettingsTask;
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationCallback mLocationCallback;
    private LocationResolutionCallback locationResolutionCallback;

    public LocationDetector() {
    }

    private OnLocationDetectedListener mOnLocationDetectedListener;

    public interface OnLocationDetectedListener {
        void onLocationDetected(String location);
    }
    public void setOnLocationDetectedListener(OnLocationDetectedListener listener) {
        this.mOnLocationDetectedListener = listener;
    }

    public LocationResolutionCallback getLocationResolutionCallback() {
        return this.locationResolutionCallback;
    }
    public void setUpLocationRequest(){
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(UPDATE_INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationDetection(final MainActivity mMainActivity) {
        Log.i(TAG,"Location detection started.");
        this.mMainActivity = mMainActivity;
        setUpLocationRequest();
        //check settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mMainActivity);

        locationResolutionCallback = new LocationResolutionCallback() {
            @Override
            public void onResolutionResult(boolean result) {
                if(result) {
                    startLocationUpdate();
                } else {
                    mAddress = "";
                    //mOnLocationDetectedListener.onLocationDetected(mAddress);
                    Intent locationIntent = new Intent(DetectorConstants.ACTION_LOCATION_DETECTED);
                    locationIntent.putExtra(DetectorConstants.EXTRA_LOCATION,mAddress);
                    mMainActivity.sendBroadcast(locationIntent);
                }
            }
        };

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastKnownLocation = locationResult.getLastLocation();
                if(mLastKnownLocation != null) {
                    mAddress = getAddress(mLastKnownLocation);
                } else {
                    mAddress = "";
                }
                Intent locationIntent = new Intent(DetectorConstants.ACTION_LOCATION_DETECTED);
                locationIntent.putExtra(DetectorConstants.EXTRA_LOCATION,mAddress);
                mMainActivity.sendBroadcast(locationIntent);
                //mOnLocationDetectedListener.onLocationDetected(mAddress);
                stopLocationUpdate();
            }
        };

        //check location settings
        SettingsClient client = LocationServices.getSettingsClient(mMainActivity);
        checkLocationSettingsTask = client.checkLocationSettings(mLocationSettingsRequest);
        checkLocationSettingsTask.addOnSuccessListener(mMainActivity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //preverim permission in zahtevam location update
                startLocationUpdate();
            }
        });
        checkLocationSettingsTask.addOnFailureListener(mMainActivity, new OnFailureListener() {
            //ce niso nastavitve OK pogledam če je kej kar loh uporabnik poprav.
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(mMainActivity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mMainActivity);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("location", false);
                        editor.apply();
                        mAddress = "";
                        //mOnLocationDetectedListener.onLocationDetected(mAddress);
                        Intent locationIntent = new Intent(DetectorConstants.ACTION_SONG_DETECTED);
                        locationIntent.putExtra(DetectorConstants.EXTRA_SONG,mAddress);
                        sendBroadcast(locationIntent);
                    }
                } else {
                    mAddress = "";
                    //mOnLocationDetectedListener.onLocationDetected(mAddress);
                    Intent locationIntent = new Intent(DetectorConstants.ACTION_SONG_DETECTED);
                    locationIntent.putExtra(DetectorConstants.EXTRA_SONG,mAddress);
                    sendBroadcast(locationIntent);
                }
            }
        });
    }

    public Location getLastKnownLocation() {
        return this.mLastKnownLocation;
    }

    public void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(mMainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    public void stopLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public String getAddress(Location location) {
        Geocoder geocoder = new Geocoder(mMainActivity, Locale.getDefault());
        String mAddress = "";
        try {
            mAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            mError = e.getMessage();
            Log.e(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            mError = e.getMessage();
            Log.e(TAG, e.getMessage());
        }
        if (mAddresses == null || mAddresses.size()  == 0) {
            Log.e(TAG, "No address was found.");
            //if(mError.isEmpty()) {
            mError = "No address was found.";
            //}
        } else {
            Address address = mAddresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            mAddress = TextUtils.join(System.getProperty("line.separator"),addressFragments);
            Log.i(TAG,"location found: "+mAddress);
        }
        mLocationStart = false;
        mLocationEnd = true;
        return mAddress;
    }
    public interface LocationResolutionCallback {
        void onResolutionResult(boolean result);
    }
}
