package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.ArrayList;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderListener {

    int MY_TRACKS = 1, SOMEONES_TRACKS = 2, SEARCH = 3;

    void tracksLoaded(ArrayList<MusicTrackPOJO> musicTracks, int queryType);

    void tracksLoadingError(String errorMessage);
}
