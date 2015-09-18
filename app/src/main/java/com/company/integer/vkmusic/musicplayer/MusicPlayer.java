package com.company.integer.vkmusic.musicplayer;

import android.media.MediaPlayer;
import android.util.Log;

import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andriy on 9/18/2015.
 */
public class MusicPlayer implements MusicPlayerInterface {
    private final String LOG_TAG = "Music Player";

    private MediaPlayer player;
    private MusicPlayerListener musicPlayerListener;

    private ArrayList<MusicTrackPOJO> playlist = new ArrayList<>();
    private MusicTrackPOJO currentTrack;
    private int currentTrackPosition;
    private int currentTrackTime;

    public MusicPlayer(){
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if (nextTrack()) {
                        musicPlayerListener.switchedToNextTrack();
                    }else{
                        musicPlayerListener.endOfPlaylist();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public synchronized void play() throws IOException {
        Log.d(LOG_TAG, "Play");
        playCurrentTrack();
    }

    @Override
    public synchronized void pause() {
        Log.d(LOG_TAG, "Pause");
        player.pause();
    }

    @Override
    public synchronized void setPlayList(ArrayList<MusicTrackPOJO> tracks, int position) {
        playlist = tracks;
        currentTrackPosition = position;
        currentTrack = playlist.get(currentTrackPosition);
        Log.d(LOG_TAG, "Set playlist");
    }

    @Override
    public synchronized ArrayList<MusicTrackPOJO> getPlaylist() {
        Log.d(LOG_TAG, "Get playlist");
        return playlist;
    }

    @Override
    public synchronized boolean nextTrack() throws IOException {
        Log.d(LOG_TAG, "Next track");
        if (currentTrackPosition + 1 > playlist.size() - 1) return false;
        currentTrackPosition++;
        playCurrentTrack();
        return true;
    }

    @Override
    public synchronized boolean previousTrack() throws IOException {
        Log.d(LOG_TAG, "Previous track");
        if (currentTrackPosition - 1 < 0) return false;
        currentTrackPosition--;
        playCurrentTrack();
        return true;
    }

    @Override
    public synchronized void setCurrentTrackTime(int time) {
        Log.d(LOG_TAG, "Set current track time");
        player.seekTo(time);
    }

    @Override
    public synchronized int getCurrentTrackPosition() {
        Log.d(LOG_TAG, "Get current track position");
        return currentTrackPosition;
    }

    @Override
    public synchronized int getCurrentTrackTime() {
        Log.d(LOG_TAG, "Get current track time");
        currentTrackTime = player.getCurrentPosition();
        return currentTrackTime;
    }

    @Override
    public synchronized MusicTrackPOJO getCurrentTrack() {
        Log.d(LOG_TAG, "Get current track");
        return currentTrack;
    }

    @Override
    public synchronized void setMusicPlayerListener(MusicPlayerListener musicPlayerListener) {
        this.musicPlayerListener = musicPlayerListener;
        Log.d(LOG_TAG, "Set music player listener");
    }

    @Override
    public synchronized boolean isPlaying() {
        return player.isPlaying();
    }

    private void playCurrentTrack() throws IOException {
        player.setDataSource(currentTrack.getUrl());
        player.prepare();
        player.start();
    }



}
