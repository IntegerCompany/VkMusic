package com.company.integer.vkmusic;

import android.app.Application;
import android.content.Intent;

import com.company.integer.vkmusic.supportclasses.AppState;
import com.company.integer.vkmusic.supportclasses.VkMusicAnalytic;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.vk.sdk.VKSdk;

public class VkMusicApplication extends Application {

    private Tracker mTracker;


    @Override
    public void onCreate() {
        super.onCreate();

        VKSdk.initialize(this);
        AppState.setupAppState(this);
        VkMusicAnalytic.getInstance().setup(getDefaultTracker());
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-55928846-4");
        }
        return mTracker;
    }

    }
