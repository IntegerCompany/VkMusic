package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.ArrayList;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderListener {

    int MY_TRACKS = 1, RECOMMENDATIONS = 2, SAVED = 3, SEARCH = 4;

    /**
     * Called when VK returned info about tracks
     * @param newPlaylist tracks
     * @param queryType from which source comes playlist
     */
    void tracksLoaded(ArrayList<MusicTrackPOJO> newPlaylist, int queryType);

    /**
     * Called when something went wrong
     * @param errorMessage
     */
    void tracksLoadingError(String errorMessage);
}
