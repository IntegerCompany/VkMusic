package com.company.integer.vkmusic;


import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.musicplayer.MusicPlayer;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class MusicPlayerTest {

    static MusicPlayer musicPlayer;

    @Before
    public void initMusicPlayer(){
        musicPlayer = new MusicPlayer();
        musicPlayer.setMusicPlayerListener(new MusicPlayerListener() {
            @Override
            public void endOfPlaylist() {

            }

            @Override
            public void onPlayerTrackUpdating(int percent) {

            }

            @Override
            public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {

            }
        });
    }

    @Test
    public void test(){

    }


    
}