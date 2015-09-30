package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.ArrayList;

/**
 * Created by Andriy on 9/21/2015.
 */
public interface TracksLoaderInterface {

    int MY_TRACKS = 1, RECOMMENDATIONS = 2, SAVED = 3, SEARCH = 4, USE_PREVIOUS = 5;
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
     * Returns recommendations from VK
     * @param userId recommendations for that user
     * @param from from track with that number we will load new ones
     * @param count how many tracks will be returned in response
     */
    void getRecommendationsByUserID(String userId, int from, int count);

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

    /**
     *
     * @param source for that source will be returned playlist
     * @return playlist from that source
     */
    ArrayList<MusicTrackPOJO> getTracksFromSource(int source);

    void downloadTrack(MusicTrackPOJO trackToDownload);

}
