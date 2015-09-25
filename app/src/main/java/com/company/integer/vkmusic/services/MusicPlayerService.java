package com.company.integer.vkmusic.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.company.integer.vkmusic.logic.MusicPlayer;
import com.company.integer.vkmusic.notificationPanel.NotificationPanel;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service {

    private final String LOG_TAG = "MusicPlayerService";
    public final static String EXTRA_PLAYLIST = "EXTRA_PLAYLIST";
    public final static String MY_TRACKS = "MY_TRACKS";

    private MusicPlayer musicPlayer = new MusicPlayer();
    private NotificationPanel nPanel = new NotificationPanel(this);

    @Override
    public void onCreate() {
        super.onCreate();
        registerMyBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (EXTRA_PLAYLIST.equals(intent.getStringExtra(EXTRA_PLAYLIST))) {
            play();
        }else if(MY_TRACKS.equals(intent.getAction())) {
            ArrayList<MusicTrackPOJO> arrayList = intent.getParcelableArrayListExtra(MY_TRACKS);
            Log.i("MY_TRACKS length = ", ""+ arrayList.size());
            musicPlayer.setPlayList(arrayList,0);
            musicPlayer.setCurrentTrackPosition(0);
        }

        return(START_NOT_STICKY);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MusicPlayerService onBind");
        return null;
    }

    public void play(){
        try {
            musicPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startForeground(1337, nPanel.getNotification());
    }


    public void pause() {
        musicPlayer.pause();
    }

    public void registerMyBroadcastReceiver(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equalsIgnoreCase("com.example.app.ACTION_PLAY")) {
                    if (intent.getExtras().getBoolean("play")){
                        play();
                        nPanel.updateToPlay(true);
                    }else{
                        pause();
                        nPanel.updateToPlay(false);
                    }
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_BACK")){
                    try {
                        musicPlayer.previousTrack();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_NEXT")) {
                    Log.i("Action : ", "Close");
                    try {
                        musicPlayer.nextTrack();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("com.example.app.ACTION_PLAY");
        intentFilter.addAction("com.example.app.ACTION_BACK");
        intentFilter.addAction("com.example.app.ACTION_NEXT");

        // register the receiver
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
