package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

/**
 * Created by Andriy on 9/18/2015.
 */
public interface MusicPlayerListener {

    /**
     * Playlist end. Probably you need to call "uploadMore()" here
     */
    void endOfPlaylist();

    /**
     * Returns buffering progress for current track
     * @param percent buffering progress
     */
    void onPlayerTrackUpdating(int percent);

    /**
     * Called when current track was changed by any reason
     * @param musicTrack current track
     */
    void onCurrentTrackChanged(MusicTrackPOJO musicTrack);
}
