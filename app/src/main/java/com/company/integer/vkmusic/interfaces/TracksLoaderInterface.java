package com.company.integer.vkmusic.interfaces;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderInterface {

    void search(String query, int from, int count);

    void getTracksByUserId(String userId, int from, int count);

    void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener);

}
