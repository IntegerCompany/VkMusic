package com.company.integer.vkmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.company.integer.vkmusic.fragments.MainFragment;
import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.services.MusicPlayerService;
import com.company.integer.vkmusic.supportclasses.AppState;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MusicPlayerListener, TracksLoaderListener {

    FloatingActionButton fabPrevious;
    FloatingActionButton fabPlayPause;
    FloatingActionButton fabNext;
    private static final String LOG_TAG = "MainActivity";
    MusicPlayerService musicPlayerService;
    MusicPlayerInterface musicPlayer;
    TracksLoaderInterface dataLoader;
    MainFragment mainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        fabPrevious = (FloatingActionButton) findViewById(R.id.fab_previous);
        fabPlayPause = (FloatingActionButton) findViewById(R.id.fab_play_pause);
        fabNext = (FloatingActionButton) findViewById(R.id.fab_next);

        fabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!musicPlayer.isPlaying()) {
                    try {
                        musicPlayer.play();
                        mainFragment.setMediaFileLengthInMilliseconds(musicPlayer.getCurrentTrack().getDuration() * 1000);
                        mainFragment.primarySeekBarProgressUpdater();
                        fabPlayPause.setImageDrawable(getResources().getDrawable(R.mipmap.pause));
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    musicPlayer.pause();
                    fabPlayPause.setImageDrawable(getResources().getDrawable(R.mipmap.play));
                }

            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicPlayer.nextTrack();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicPlayer.previousTrack();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        startMusicPlayerService();
    }

    @Override
    public void endOfPlaylist() {
        musicPlayerService.uploadMore(USE_PREVIOUS);
    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        mainFragment.getSeekBar().setSecondaryProgress(percent);
    }

    @Override
    public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {
        mainFragment.setCurrentTrack();
        mainFragment.setMediaFileLengthInMilliseconds(musicTrack.getDuration() * 1000);
        mainFragment.getSeekBar().setProgress((int) (((float) musicPlayer.getCurrentTrackTime() / mainFragment.getMediaFileLengthInMilliseconds()) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (musicPlayer.getCurrentTrackTime() == 0)
            mainFragment.getSeekBar().setProgress(0);

    }

    @Override
    public void tracksLoaded(ArrayList<MusicTrackPOJO> newPlaylist, int queryType) {
        musicPlayer.setPlayList(newPlaylist, musicPlayer.getCurrentTrackPosition());
        mainFragment.setupViewPager();
    }

    @Override
    public void tracksLoadingError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void startMusicPlayerService() {

        Intent serviceIntent = new Intent(MainActivity.this, MusicPlayerService.class);
        startService(serviceIntent);
        ServiceConnection sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected");
                musicPlayerService = ((MusicPlayerService.MyBinder) binder).getService();
                musicPlayer = musicPlayerService;
                dataLoader = musicPlayerService;
                musicPlayer.setMusicPlayerListener(MainActivity.this);
                dataLoader.setTracksLoadingListener(MainActivity.this);
                mainFragment.setMusicPlayer(musicPlayer);
                dataLoader.getTracksByUserId(AppState.getLoggedUser().getUserId(), 1, 10);
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.d("MainActivity", "MainActivity onServiceDisconnected");
            }
        };
        bindService(serviceIntent, sConn, 0);

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void setTranslations(float k) {
        Log.d("sliding :", "" + k);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        fabPlayPause.setTranslationX(-(width / 2 - dpToPx(79)) * k);
        fabPlayPause.setTranslationY(-(height - dpToPx(446)) * k);
        fabPrevious.setTranslationX(-(width / 2 - dpToPx(79) + dpToPx(24)) * k);
        fabPrevious.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
        fabNext.setTranslationX(-(width / 2 - dpToPx(79) - dpToPx(24)) * k);
        fabNext.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
    }

    public MusicPlayerInterface getMusicPlayer(){
        return musicPlayer;
    }

}