package com.company.integer.vkmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.services.MusicPlayerService;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayerListener, TracksLoaderListener{

    private static final String LOG_TAG = "MainActivity";
    private Button btnStart, btnPause, btnNext, btnPrevious;
    private SeekBar seekBarProgress;
    private TextView tvNowPlaying;

    private int mediaFileLengthInMilliseconds;
    private Handler handler = new Handler();

    MusicPlayerService musicPlayerService;
    MusicPlayerInterface musicPlayer;
    TracksLoaderInterface dataLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        initViews();
        setUIListeners();
        startMusicPlayerService();
    }

    @Override
    public void endOfPlaylist() {

    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCurrentTrackChanged(MusicTrackPOJO musicTrack) {
        tvNowPlaying.setText("Now playing \n" + musicTrack.getArtist() + "\n \n" + musicTrack.getTitle());
        mediaFileLengthInMilliseconds = musicTrack.getDuration() * 1000;
        seekBarProgress.setProgress((int) (((float) musicPlayer.getCurrentTrackTime() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (musicPlayer.getCurrentTrackTime() ==0 )
            seekBarProgress.setProgress(0);

    }

    @Override
    public void tracksLoaded(ArrayList<MusicTrackPOJO> musicTracks, int queryType) {
        try {
            musicPlayer.setPlayList(musicTracks, 0);
            musicPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            }
            public void onServiceDisconnected(ComponentName name) {
                Log.d("MainActivity", "MainActivity onServiceDisconnected");
            }
        };
        bindService(serviceIntent, sConn, 0);

    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) musicPlayer.getCurrentTrackTime() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (musicPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    private void findViewsById(){
        btnStart = (Button) findViewById(R.id.btn_start);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        seekBarProgress = (SeekBar) findViewById(R.id.seek_musicprogress);
        tvNowPlaying = (TextView) findViewById(R.id.tv_now_playing);
    }

    private void initViews() {
        seekBarProgress.setMax(99); // It means 100% .0-99
    }

    private void setUIListeners(){
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicPlayer.play();
                    mediaFileLengthInMilliseconds = musicPlayer.getCurrentTrack().getDuration() * 1000;
                    primarySeekBarProgressUpdater();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPlayer.pause();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicPlayer.nextTrack();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    musicPlayer.previousTrack();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        seekBarProgress.setOnTouchListener(new View.OnTouchListener() {
                                               @Override
                                               public boolean onTouch(View v, MotionEvent event) {
                                                   SeekBar sb = (SeekBar) v;
                                                   int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                                                   musicPlayer.setCurrentTrackTime(playPositionInMillisecconds);
                                                   return false;
                                               }
                                           }
        );
    }



}
