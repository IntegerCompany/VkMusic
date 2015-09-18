package com.company.integer.vkmusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.musicplayer.MusicPlayer;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MusicPlayerService extends Service{

    private final String LOG_TAG = "MusicPlayerService";

    MyBinder binder = new MyBinder();
    private Gson gson;


    private MusicPlayer musicPlayer = new MusicPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        Log.d(LOG_TAG, "MusicPlayerService onCreate");
        Log.d(LOG_TAG, "this thread is" + Thread.currentThread().getName());
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MusicPlayerService onBind");
        return binder;
    }

    public MusicPlayerInterface getMusicPlayer(MusicPlayerListener musicPlayerListener){
        musicPlayer.setMusicPlayerListener(musicPlayerListener);
        return musicPlayer;
    }

    public class MyBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
    public void getMusicTracks(String ownerId, int from, int count){
        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, AppState.getLoggedUser().getUserId());
        params.put(VKApiConst.COUNT, "1");
        VKRequest requestAudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, ownerId,VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    musicPlayer.setPlayList(musicTracks, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(LOG_TAG, error.errorMessage);
            }
        });
    }
}
