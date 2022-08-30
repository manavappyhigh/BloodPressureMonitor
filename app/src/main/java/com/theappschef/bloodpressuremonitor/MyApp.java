package com.theappschef.bloodpressuremonitor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {

    private static final String TAG = "Messenger";
    private static MyApp sInstance;
    private Activity currentActivity;
    private Integer appCount = 0;
    public MyApp() {
        sInstance = this;
    }


    public static MyApp get() {
        return sInstance;
    }

    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        MultiDex.install(this);
        MyApp.context = getApplicationContext();


        FirebaseApp.initializeApp(this);
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
//
//            }
//        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}

