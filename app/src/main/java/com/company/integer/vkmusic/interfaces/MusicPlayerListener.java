package com.company.integer.vkmusic.interfaces;

import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

/**
 * Created by Andriy on 9/18/2015.
 */
public interface MusicPlayerListener {

    void endOfPlaylist();

    void onPlayerTrackUpdating(int percent);

    void onCurrentTrackChanged(MusicTrackPOJO musicTrack);
}
