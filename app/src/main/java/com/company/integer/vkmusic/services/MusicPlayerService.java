package com.company.integer.vkmusic.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.logic.MusicPlayer;
import com.company.integer.vkmusic.logic.TracksDataLoader;
import com.company.integer.vkmusic.notificationPanel.NotificationPanel;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MusicPlayerListener {

    private final String LOG_TAG = "MusicPlayerService";
    public final static String EXTRA_PLAYLIST = "EXTRA_PLAYLIST";
    public final static String MY_TRACKS = "MY_TRACKS";

    private MusicPlayer musicPlayer = new MusicPlayer();

    @Override
    public void onCreate() {
        super.onCreate();

        registerMyBroadcastReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (EXTRA_PLAYLIST.equals(intent.getStringExtra(EXTRA_PLAYLIST))) {
            try {
                play();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    // MusicPlayer interface methods-----------

    public void play() throws IOException {

        Log.w(getClass().getName(), "Got to play()!");
        NotificationPanel nPanel = new NotificationPanel(this);

        Notification note=new Notification.Builder(getApplicationContext())
                .setContentText("wewaewaewa")
                .build();

        Intent i=new Intent(this, MainActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi= PendingIntent.getActivity(this, 0,
                i, 0);

        note.flags|= Notification.FLAG_NO_CLEAR;

        musicPlayer.play();
        startForeground(1337, nPanel.getNotification());

    }


    public void pause() {
        musicPlayer.pause();
    }



    // MusicPlayer callbacks-------
    @Override
    public void endOfPlaylist() {

    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        //playerCallbackForUI.onPlayerTrackUpdating(percent);
    }

    @Override
    public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {
        //playerCallbackForUI.onCurrentTrackChanged(musicTrack);
    }


    public void registerMyBroadcastReceiver(){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equalsIgnoreCase("com.example.app.ACTION_PLAY")) {
                    try {
                        play();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_PAUSE")){
                    pause();
                }else if(action.equalsIgnoreCase("com.example.app.ACTION_CLOSE")) {
                    Log.i("Action : ", "Close");
                    stopForeground(true);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("com.example.app.ACTION_PLAY");
        intentFilter.addAction("com.example.app.ACTION_PAUSE");
        intentFilter.addAction("com.example.app.ACTION_CLOSE");

        // register the receiver
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
