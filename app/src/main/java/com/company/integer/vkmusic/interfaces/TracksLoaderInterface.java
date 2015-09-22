package com.company.integer.vkmusic.interfaces;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderInterface {

    /**
     * VK search for music
     * @param query text from search field
     * @param from from track with that number we will load new ones
     * @param count how many tracks will be returned in response
     */
    void search(String query, int from, int count);

    /**
     * VK music of some user
     * @param userId music of that user will be returned
     * @param from from track with that number we will load new ones
     * @param count how many tracks will be returned in response
     */
    void getTracksByUserId(String userId, int from, int count);

    /**
     * Callback for track loading
     * @param tracksLoaderListener callback
     */
    void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener);

    /**
     * Used to load more tracks
     * @param source adds new tracks to playlist from that source
     */
    void uploadMore(int source);

}
