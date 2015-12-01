package com.company.integer.vkmusic;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.company.integer.vkmusic.supportclasses.AppState;
import com.company.integer.vkmusic.supportclasses.VkMusicAnalytic;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class VkMusicApplication extends Application {
    private Tracker mTracker;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
// VKAccessToken is invalid
                Log.d("VkMusicApp", "VKAccessToken is invalid");
            }else{
                if(oldToken != null){
                    Log.d("VkMusicApp", "OldToken : user_id = " + oldToken.userId);
                }else{
                    Log.d("VkMusicApp", "NewToken : user_id = " + newToken.userId);
                    AppState.setupAppState(VkMusicApplication.this);
                    AppState.setLoggedUserID(newToken.userId);
//                    startMainActivity();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        AppState.setupAppState(this);
        VkMusicAnalytic.getInstance().setup(getDefaultTracker());
        VKSdk.initialize(this);
        vkAccessTokenTracker.startTracking();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-70779383-2");
        }
        return mTracker;
    }
    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
