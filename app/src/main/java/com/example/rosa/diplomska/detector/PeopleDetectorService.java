package com.example.rosa.diplomska.detector;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class PeopleDetectorService extends Service {
    private int mPeople;
    String mError = "";
    private BluetoothAdapter mBluetoothAdapter;
    IntentFilter filter;
    BroadcastReceiver mReceiver;
    boolean mReceiverRegistered = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                setUpBTDetection();
                registerBTReceiver(getBaseContext());
                startBTDetection();
            }
        });
        thread.start();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startBTDetection() {
        mBluetoothAdapter.startDiscovery();
    }


    public void setUpBTDetection() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    mPeople = 0;
                    Log.i(TAG, "Bluetooth discovery started.");
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.i(TAG, "Bluetooth discovery ended. Found "+mPeople+" devices.");
                    //mOnPeopleDetectedListener.onPeopleDetected(mPeople);
                    mError = "";
                    Intent peopleIntent = new Intent(DetectorConstants.ACTION_PEOPLE_DETECTED);
                    peopleIntent.putExtra(DetectorConstants.EXTRA_PEOPLE,mPeople);
                    sendBroadcast(peopleIntent);
                    //mOnChangeListener.onDetectedPeople(mPeople);
                    unregisterBTReceiver(getBaseContext());
                    stopSelf();
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Device found: " + device.getName() + "; MAC " + device.getAddress());
                    mPeople++;
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }
    public void registerBTReceiver(Context c) {
        c.registerReceiver(mReceiver,filter);
        mReceiverRegistered = true;
    }
    public void unregisterBTReceiver(Context c) {
        if(mReceiverRegistered) {
            c.unregisterReceiver(mReceiver);
            mReceiver = null;
            mReceiverRegistered = false;
        }
    }
}