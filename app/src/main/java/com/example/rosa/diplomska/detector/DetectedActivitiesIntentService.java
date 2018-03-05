package com.example.rosa.diplomska.detector;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";

    public DetectedActivitiesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        //Log.i(TAG, "activity detected");
        String activity = "";
        int maxConfidence = 0;
        int defaultConfidence = 0;
        for (DetectedActivity da: detectedActivities) {
            if(!getActivityString(da.getType()).equals("hanging out")) {
                if (maxConfidence < da.getConfidence()) {
                    maxConfidence = da.getConfidence();
                    activity = getActivityString(da.getType());
                }
            } else {
                defaultConfidence = da.getConfidence();
            }
            //Log.i(TAG, getActivityString(da.getType()) + " " + da.getConfidence() + "%");
        }
        if(defaultConfidence > maxConfidence) {
            activity = "hanging out";
        }
        Log.i(TAG,"Detected activity: "+activity);
        Intent localIntent = new Intent(DetectorConstants.ACTION_MOTION_DETECTED);
        localIntent.putExtra(DetectorConstants.EXTRA_ACTIVITY,activity);
        sendBroadcast(localIntent);
        stopSelf();
    }
    static String getActivityString(int detectedActivityType) {
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return "driving";
            case DetectedActivity.ON_BICYCLE:
                return "bicycling";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            default:
                return "hanging out";
        }
    }
}