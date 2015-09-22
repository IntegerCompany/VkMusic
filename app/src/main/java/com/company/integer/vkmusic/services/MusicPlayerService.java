package com.company.integer.vkmusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.logic.MusicPlayer;
import com.company.integer.vkmusic.logic.TracksDataLoader;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MusicPlayerInterface, MusicPlayerListener, TracksLoaderInterface, TracksLoaderListener {

    private final String LOG_TAG = "MusicPlayerService";

    MyBinder binder = new MyBinder();

    private TracksDataLoader tracksDataLoader = new TracksDataLoader();
    private TracksLoaderListener dataLoadingCallbackForUI;

    private MusicPlayer musicPlayer = new MusicPlayer();
    private MusicPlayerListener playerCallbackForUI;

    private ArrayList<MusicTrackPOJO> myTracksPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> recommendationsPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> savedPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> searchPlaylist = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        tracksDataLoader.setTracksLoadingListener(this);
        musicPlayer.setMusicPlayerListener(this);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MusicPlayerService onBind");
        return binder;
    }



    // MusicPlayer interface methods-----------
    @Override
    public void play() throws IOException {
        musicPlayer.play();
    }

    @Override
    public void pause() {
        musicPlayer.pause();
    }

    @Override
    public void setPlayList(ArrayList<MusicTrackPOJO> tracks, int position) {
        musicPlayer.setPlayList(tracks, position);
    }

    @Override
    public void addTracksToCurrentPlaylist(ArrayList<MusicTrackPOJO> tracksToAdd) {
        musicPlayer.addTracksToCurrentPlaylist(tracksToAdd);
    }

    @Override
    public ArrayList<MusicTrackPOJO> getPlaylist() {
        return musicPlayer.getPlaylist();
    }

    @Override
    public boolean nextTrack() throws IOException {
        return musicPlayer.nextTrack();
    }

    @Override
    public boolean previousTrack() throws IOException {
        return musicPlayer.previousTrack();
    }

    @Override
    public void setCurrentTrackTime(int time) {
        musicPlayer.setCurrentTrackTime(time);
    }

    @Override
    public int getCurrentTrackPosition() {
        return musicPlayer.getCurrentTrackPosition();
    }

    @Override
    public void setCurrentTrackPosition(int position) {
        musicPlayer.setCurrentTrackPosition(position);
    }

    @Override
    public int getCurrentTrackTime() {
        return musicPlayer.getCurrentTrackTime();
    }

    @Override
    public MusicTrackPOJO getCurrentTrack() {
        return musicPlayer.getCurrentTrack();
    }

    @Override
    public void setMusicPlayerListener(MusicPlayerListener musicPlayerListener) {
        playerCallbackForUI = musicPlayerListener;
    }

    @Override
    public boolean isPlaying() {
        return musicPlayer.isPlaying();
    }
    // MusicPlayer interface methods end--------

    // MusicPlayer callbacks-------
    @Override
    public void endOfPlaylist() {

        playerCallbackForUI.endOfPlaylist();
    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        playerCallbackForUI.onPlayerTrackUpdating(percent);
    }

    @Override
    public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {
        playerCallbackForUI.onCurrentTrackChanged(musicTrack);
    }
    // MusicPlayer callbacks end-------

    // TracksDataLoader interface methods-----------
    @Override
    public void search(String query, int from, int count) {
        tracksDataLoader.search(query, from, count);
    }

    @Override
    public void getTracksByUserId(String userId, int from, int count) {
        tracksDataLoader.getTracksByUserId(userId, from, count);
    }

    @Override
    public void getRecommendationsByUserID(String userId, int from, int count) {
        tracksDataLoader.getRecommendationsByUserID(userId, from, count);
    }

    @Override
    public void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener) {
        dataLoadingCallbackForUI = tracksLoaderListener;
    }

    @Override
    public void uploadMore(int source) {
        switch (source){
            case MY_TRACKS:
                getTracksByUserId(AppState.getLoggedUser().getUserId(), myTracksPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;
            case RECOMMENDATIONS:
                getRecommendationsByUserID(tracksDataLoader.getLastSearchQuery(), recommendationsPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;
            case SEARCH:
                search(tracksDataLoader.getLastSearchQuery(), searchPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;

        }

    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        switch (source){
            case MY_TRACKS:
                return myTracksPlaylist;
            case RECOMMENDATIONS:
                return recommendationsPlaylist;
            case SAVED:
                return savedPlaylist;
            case SEARCH:
                return searchPlaylist;
        }
        return new ArrayList<>();
    }
    // TracksDataLoader interface methods end-----------

    // TracksDataLoader callbacks methods-----------
    @Override
    public void tracksLoaded(ArrayList<MusicTrackPOJO> newTracks, int source) {
        switch (source){
            case MY_TRACKS:
                myTracksPlaylist.addAll(newTracks);
                dataLoadingCallbackForUI.tracksLoaded(myTracksPlaylist, source);
                break;
            case RECOMMENDATIONS:
                recommendationsPlaylist.addAll(newTracks);
                dataLoadingCallbackForUI.tracksLoaded(recommendationsPlaylist, source);
                break;
            case SAVED:
                savedPlaylist.addAll(newTracks);
                dataLoadingCallbackForUI.tracksLoaded(savedPlaylist, source);
                break;
            case SEARCH:
                searchPlaylist.addAll(newTracks);
                dataLoadingCallbackForUI.tracksLoaded(searchPlaylist, source);
                break;
        }



    }

    @Override
    public void tracksLoadingError(String errorMessage) {
        dataLoadingCallbackForUI.tracksLoadingError(errorMessage);
    }
    // TracksDataLoader callbacks methods end-----------


    public class MyBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }


}
