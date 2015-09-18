package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andriy on 9/18/2015.
 */
public interface MusicPlayerInterface {

    /**
     * Start playing music
     */
    void play() throws IOException;

    /**
     * Pause playing
     */
    void pause();

    /**
     * Set new playlist and start from position
     * @param tracks - ArrayList with music tracks
     * @param position - position of track to play
     */
    void setPlayList(ArrayList<MusicTrackPOJO> tracks, int position);

    /**     *
     * @param tracksToAdd Adding that tracks to end of current playlist
     */
    void addTracksToCurrentPlaylist(ArrayList<MusicTrackPOJO> tracksToAdd);

    /**
     * Get current playlist tracks
     * @return current playlist
     */
    ArrayList<MusicTrackPOJO> getPlaylist();

    /**
     * Switch to next track
     * @return false if we at the end of playlist, otherwise true, and switching to next track
     */
    boolean nextTrack() throws IOException;

    /**
     * Switch to next track
     * @return false if we at the start of playlist, otherwise tru, and switching to previous track
     */
    boolean previousTrack() throws IOException;

    /**
     * Set time for current track
     * @param time track will be played from that time
     */
    void setCurrentTrackTime(int time);

    /**
     * Get position of current track
     * @return position of current track
     */
    int getCurrentTrackPosition();

    /**
     * Starts playing track on position from playlist
     * @param position - playing track from playlist at this position
     */
    void setCurrentTrackPosition(int position);

    /**
     * Get current track playing time
     * @return current track time
     */
    int getCurrentTrackTime();

    /**
     * Get current track information
     * @return current track POJO
     */
    MusicTrackPOJO getCurrentTrack();

    /**
     * Required to get callbacks from music player
     */
    void setMusicPlayerListener(MusicPlayerListener musicPlayerListener);

    boolean isPlaying();


}
