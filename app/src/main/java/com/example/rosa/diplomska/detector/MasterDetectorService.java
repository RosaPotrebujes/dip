package com.example.rosa.diplomska.detector;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MasterDetectorService extends Service {
    private BluetoothAdapter mBluetoothAdapter;
    private int mPeople;
    String mBTErrMsg;
    BroadcastReceiver mReceiver;
    List<Address> mAddresses;
    String mAddress;

    boolean mBTStart = false;
    boolean mBTFinish = false;

    boolean mLocationStart = false;
    boolean mLocationEnd = false;

    private final IBinder mBinder = new MasterDetectorBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startBTDetection() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    mBTStart = true;
                    mPeople = 0;
                    //discovery starts, we can show progress dialog or perform other tasks
                    Log.i(TAG, "Bluetooth discovery started.");
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //devicesFound = 0;//
                    Log.i(TAG, "Bluetooth discovery ended. Found "+mPeople+" devices.");
                    mBTErrMsg = "";
                    mBTFinish = true;
                    mBTStart = false;
                    mOnChangeListener.onDetectedPeople(mPeople);
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Device found: " + device.getName() + "; MAC " + device.getAddress());
                    mPeople++;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter);
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
    }

    public void getAddress(Location location) {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            mAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            Log.e(TAG, e.getMessage());
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
            Log.e(TAG, e.getMessage());
        }
        if (mAddresses == null || mAddresses.size()  == 0) {
            Log.e(TAG, "No address was found.");
            if(errorMessage.isEmpty()) {
                errorMessage = "No address was found.";
                mAddress = "";
                mLocationStart = false;
                mLocationEnd = true;
            }
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
            mOnChangeListener.onDetectedLocation(mAddress);
            mLocationStart = false;
            mLocationEnd = true;
        }
    }
    public class MasterDetectorBinder extends Binder {
        public MasterDetectorService getService() {
            return MasterDetectorService.this;
        }
    }
    OnChangeListener mOnChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }
    public interface OnChangeListener {
        public void onDetectedLocation(String location);
        public void onDetectedPeople(int people);
    }

}
