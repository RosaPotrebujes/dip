package com.example.rosa.diplomska.volley;

import android.app.Application;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppSingleton extends Application{
    private static AppSingleton mAppSingletonInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppSingletonInstance = this;
    }

    private AppSingleton(Context c) {
        mContext = c;
        mRequestQueue = getRequestQueue();
//        this.mRequestQueue = getRequestQueue();
//        this.mContext = mContext;
    }

    public static synchronized AppSingleton getInstance(Context c) {
        if(mAppSingletonInstance == null) {
            mAppSingletonInstance = new AppSingleton(c);
        }
        return mAppSingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}