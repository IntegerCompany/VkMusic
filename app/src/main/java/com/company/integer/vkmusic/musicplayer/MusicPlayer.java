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
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                musicPlayerListener.onPlayerTrackUpdating(percent);
            }
        });
//        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                try {
//                    if (nextTrack()) {
//                        musicPlayerListener.switchedToNextTrack();
//                    } else {
//                        musicPlayerListener.endOfPlaylist();
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    @Override
    public void play() throws IOException {
        Log.d(LOG_TAG, "Play");
        playCurrentTrack();
    }

    @Override
    public void pause() {
        Log.d(LOG_TAG, "Pause");
        player.pause();
    }

    @Override
    public void setPlayList(ArrayList<MusicTrackPOJO> tracks, int position) {
        playlist = tracks;
        currentTrackPosition = position;
        currentTrackTime = 0;
        currentTrack = playlist.get(currentTrackPosition);
        musicPlayerListener.onCurrentTrackChanged(currentTrack);
        Log.d(LOG_TAG, "Set playlist");
    }

    @Override
    public void addTracksToCurrentPlaylist(ArrayList<MusicTrackPOJO> tracksToAdd) {
        playlist.addAll(tracksToAdd);
    }

    @Override
    public ArrayList<MusicTrackPOJO> getPlaylist() {
        Log.d(LOG_TAG, "Get playlist");
        return playlist;
    }

    @Override
    public boolean nextTrack() throws IOException {
        Log.d(LOG_TAG, "Next track");
        if (currentTrackPosition + 1 > playlist.size() - 1) return false;
        currentTrackPosition++;
        currentTrackTime = 0;
        currentTrack = playlist.get(currentTrackPosition);
        musicPlayerListener.onCurrentTrackChanged(currentTrack);
        playCurrentTrack();
        return true;
    }

    @Override
    public boolean previousTrack() throws IOException {
        Log.d(LOG_TAG, "Previous track");
        if (currentTrackPosition - 1 < 0) return false;
        currentTrackPosition--;
        currentTrackTime = 0;
        currentTrack = playlist.get(currentTrackPosition);
        musicPlayerListener.onCurrentTrackChanged(currentTrack);
        playCurrentTrack();
        return true;
    }

    @Override
    public void setCurrentTrackTime(int time) {
        Log.d(LOG_TAG, "Set current track time");
        player.seekTo(time);
    }

    @Override
    public int getCurrentTrackPosition() {
        Log.d(LOG_TAG, "Get current track position");
        return currentTrackPosition;
    }

    @Override
    public void setCurrentTrackPosition(int position) {
        currentTrackPosition = position;
        currentTrackTime = 0;
        currentTrack = playlist.get(currentTrackPosition);
        musicPlayerListener.onCurrentTrackChanged(currentTrack);
    }

    @Override
    public int getCurrentTrackTime() {
        Log.d(LOG_TAG, "Get current track time");
        currentTrackTime = player.getCurrentPosition();
        //yea, its looks weird, but there is now way to check player
        if (player.getCurrentPosition() > 999999999) currentTrackTime = 0;
        return currentTrackTime;
    }

    @Override
    public MusicTrackPOJO getCurrentTrack() {
        Log.d(LOG_TAG, "Get current track");
        return currentTrack;
    }

    @Override
    public void setMusicPlayerListener(MusicPlayerListener musicPlayerListener) {
        this.musicPlayerListener = musicPlayerListener;
        Log.d(LOG_TAG, "Set music player listener");
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    private MusicTrackPOJO cachedMusicTrack = new MusicTrackPOJO();
    private void playCurrentTrack() throws IOException {
        if (!cachedMusicTrack.equals(currentTrack)) {
            player.reset();
            player.setDataSource(currentTrack.getUrl());
            player.prepare();
            cachedMusicTrack = currentTrack;
        }
        player.start();


    }



}
