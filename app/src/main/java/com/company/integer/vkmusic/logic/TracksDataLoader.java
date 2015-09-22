package com.company.integer.vkmusic.logic;

import android.util.Log;

import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
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

/**
 * Created by Andriy on 9/21/2015.
 */
public class TracksDataLoader implements TracksLoaderInterface {
    private static final String LOG_TAG = "TracksDataLoader";

    TracksLoaderListener tracksLoaderListener;
    Gson gson;

    public TracksDataLoader(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    public void search(String query, int from, int count) {
    }

    @Override
    public void getTracksByUserId(String userId, int from, int count) {
        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, AppState.getLoggedUser().getUserId());
        params.put(VKApiConst.COUNT, "1");
        VKRequest requestAudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, userId,VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.MY_TRACKS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(LOG_TAG, error.errorMessage);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }

    @Override
    public void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener) {
        this.tracksLoaderListener = tracksLoaderListener;
    }


}
