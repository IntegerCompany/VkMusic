package com.company.integer.vkmusic.supportclasses;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by andrew on 04.11.15.
 */
public class VkMusicAnalytic {
    private static VkMusicAnalytic ourInstance = new VkMusicAnalytic();

    public static VkMusicAnalytic getInstance() {
        return ourInstance;
    }

    private VkMusicAnalytic() {
    }

    Tracker tracker;

    public void setup(Tracker tracker){
        this.tracker = tracker;
    }

    public void addToPlaylistPressed(){
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("AddToPlaylist")
                .build());
    }

    public Tracker getTracker() {
        return tracker;
    }
}
