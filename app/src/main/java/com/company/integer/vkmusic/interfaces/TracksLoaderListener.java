package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.ArrayList;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderListener {

    int MY_TRACKS = 1, RECOMMENDATIONS = 2, SAVED = 3, SEARCH = 4;

    void tracksLoaded(ArrayList<MusicTrackPOJO> newPlaylist, int queryType);

    void tracksLoadingError(String errorMessage);
}
