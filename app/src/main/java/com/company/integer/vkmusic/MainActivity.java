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
import android.widget.Toast;

import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.interfaces.MusicPlayerListener;
import com.company.integer.vkmusic.services.MusicPlayerService;
import com.company.integer.vkmusic.supportclasses.AppState;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MusicPlayerListener{

    private static final String LOG_TAG = "MainActivity";
    private Button btnStart, btnPause, btnNext, btnPrevious;
    private SeekBar seekBarProgress;

    private int mediaFileLengthInMilliseconds;
    private Handler handler = new Handler();

    MusicPlayerService musicPlayerService;
    MusicPlayerInterface musicPlayer;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // All code goes here coz service wii call this method from not UI thread
            }
        });
    }

    @Override
    public void switchedToNextTrack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // All code goes here coz service wii call this method from not UI thread


            }
        });
    }

    @Override
    public void onPlayerTrackUpdating(int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    private void startMusicPlayerService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainActivity.this, MusicPlayerService.class);
                startService(serviceIntent);
                ServiceConnection sConn = new ServiceConnection() {

                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        Log.d(LOG_TAG, "MainActivity onServiceConnected");
                        musicPlayerService = ((MusicPlayerService.MyBinder) binder).getService();
                        musicPlayer = musicPlayerService.getMusicPlayer(MainActivity.this);
                        musicPlayerService.getMusicTracks(AppState.getLoggedUser().getUserId(), 1, 10);
                    }

                    public void onServiceDisconnected(ComponentName name) {
                        Log.d("MainActivity", "MainActivity onServiceDisconnected");
                    }
                };
                bindService(serviceIntent, sConn, 0);
            }
        }).start();

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
                                                   if (musicPlayer.isPlaying()) {
                                                       SeekBar sb = (SeekBar) v;
                                                       int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                                                       musicPlayer.setCurrentTrackTime(playPositionInMillisecconds);
                                                   }
                                                   return false;
                                               }
                                           }
        );
    }






}
