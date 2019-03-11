package com.example.rosa.diplomska.detector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

//https://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android

public class DetectionReceiver extends BroadcastReceiver {
    public boolean isRegistered;

    public Intent register(Context context, IntentFilter filter) {
        try {
            return !isRegistered
                    ? context.registerReceiver(this, filter)
                    : null;
        } finally {
            isRegistered = true;
        }
    }

    public boolean unregister(Context context) {
        // additional work match on context before unregister
        // eg store weak ref in register then compare in unregister
        // if match same instance
        return isRegistered
                && unregisterInternal(context);
    }

    private boolean unregisterInternal(Context context) {
        context.unregisterReceiver(this);
        isRegistered = false;
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
