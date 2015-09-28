package com.company.integer.vkmusic.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.logic.MusicPlayer;
import com.company.integer.vkmusic.notificationPanel.NotificationPanel;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MusicPlayerListener {

    private final String LOG_TAG = "MusicPlayerService";
    public final static String EXTRA_PLAYLIST = "EXTRA_PLAYLIST";
    public final static String MY_TRACKS = "MY_TRACKS";

    private MusicPlayer musicPlayer = new MusicPlayer();
    private NotificationPanel nPanel;

    @Override
    public void onCreate() {
        super.onCreate();
        registerMyBroadcastReceiver();
        nPanel = new NotificationPanel(this);
        musicPlayer.setMusicPlayerListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(MY_TRACKS.equals(intent.getAction())) {
            ArrayList<MusicTrackPOJO> arrayList = intent.getParcelableArrayListExtra(MY_TRACKS);
            Log.i("MY_TRACKS length = ", ""+ arrayList.size());
            musicPlayer.setPlayList(arrayList,0);
            musicPlayer.setCurrentTrackPosition(0);
        }

        return(START_NOT_STICKY);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void play(){
        try {
            musicPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startForeground(1337, nPanel.getNotification(true));
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
                    play();
                    nPanel.updateToPlay(true);
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_PAUSE")) {
                    pause();
                    nPanel.updateToPlay(false);
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
                else if(action.equalsIgnoreCase("com.example.app.ACTION_UPDATE_TRACK")) {
                    onCurrentTrackChanged(musicPlayer.getCurrentTrack());
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_SET_TRACK")){
                    musicPlayer.setCurrentTrackPosition(intent.getIntExtra("newTrackPosition", 0));
                    onCurrentTrackChanged(musicPlayer.getCurrentTrack());
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("com.example.app.ACTION_PLAY");
        intentFilter.addAction("com.example.app.ACTION_PAUSE");
        intentFilter.addAction("com.example.app.ACTION_BACK");
        intentFilter.addAction("com.example.app.ACTION_NEXT");
        intentFilter.addAction("com.example.app.ACTION_UPDATE_TRACK");
        intentFilter.addAction("com.example.app.ACTION_SET_TRACK");
        // register the receiver
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void endOfPlaylist() {

    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        Intent in = new Intent("com.example.app.ACTION_TRACK_PROGRESS");
        in.putExtra("percent",percent);
        sendBroadcast(in);
    }

    @Override
    public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {
        Intent in = new Intent("com.example.app.ACTION_TRACK_CHANGED");
        in.putExtra("CurrentTrackTime",musicPlayer.getCurrentTrackTime());
        in.putExtra("musicTrack",musicTrack);
        in.putExtra("musicTrackPosition", musicPlayer.getCurrentTrackPosition());
        sendBroadcast(in);
    }
}
