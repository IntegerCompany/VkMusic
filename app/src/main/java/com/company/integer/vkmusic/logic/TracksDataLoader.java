package com.company.integer.vkmusic.logic;

import android.util.Log;

import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
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


public class TracksDataLoader implements TracksLoaderInterface {
    private static final String LOG_TAG = "TracksDataLoader";

    TracksLoaderListener tracksLoaderListener;
    Gson gson;
    String lastSearchQuery = "";

    public TracksDataLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public String getLastSearchQuery() {
        return lastSearchQuery;
    }

    @Override
    public void search(String query, int from, int count) {
        lastSearchQuery = query;
        VKRequest requestAudio = new VKRequest("audio.search", VKParameters.from(VKApiConst.Q, query, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.SEARCH);
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
    public void getTracksByUserId(String userId, int from, int count) {
        VKRequest requestAudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, userId, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
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
    public void getRecommendationsByUserID(String userId, int from, int count) {
        VKRequest requestAudio = new VKRequest("audio.getRecommendations", VKParameters.from(VKApiConst.OWNER_ID, userId, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.RECOMMENDATIONS);
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

    @Override
    public void uploadMore(int source) {
        //Unused
    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        //Unused
        return null;
    }


}
