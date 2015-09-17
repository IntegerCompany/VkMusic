package com.company.integer.vkmusic;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by Andriy on 9/17/2015.
 */
public class VkMusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
