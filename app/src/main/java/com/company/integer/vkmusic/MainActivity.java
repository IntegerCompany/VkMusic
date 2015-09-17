package com.company.integer.vkmusic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.company.integer.vkmusic.pojo.MusicTrackSO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnStart, btnStop, btnPause, btnGetLength;
    private SeekBar seekBarProgress;

    private MediaPlayer player;
    private int mediaFileLengthInMilliseconds;
    private Handler handler = new Handler();

    private String currentMusicTrackURL;
    private GsonBuilder gsonBuilder;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        setUIListeners();
        initPlayer();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        getMusicTracks(AppState.getLoggedUser().getUserId(), 1, 10);


        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, AppState.getLoggedUser().getUserId());
        params.put(VKApiConst.COUNT, "1");
        VKRequest requestaudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, AppState.getLoggedUser().getUserId(), VKApiConst.COUNT, "10"));
        requestaudio.executeWithListener(new VKRequest.VKRequestListener() {


            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    currentMusicTrackURL = response.json.getJSONObject("response").getJSONArray("items").getJSONObject(0).getString("url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Toast.makeText(MainActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        initPlayer();
    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) player.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (player.isPlaying()) {
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
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnGetLength = (Button) findViewById(R.id.btn_get_length);
        seekBarProgress = (SeekBar) findViewById(R.id.seek_musicprogress);
    }

    private void setUIListeners(){
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    player.setDataSource(currentMusicTrackURL);
                    player.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaFileLengthInMilliseconds = player.getDuration(); // gets the song length in milliseconds from URL
                if(!player.isPlaying()){
                    player.start();
                }else {
                    player.pause();
                }
                primarySeekBarProgressUpdater();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    player.stop();
                } catch (Exception e) {
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    player.pause();
                } catch (Exception e) {
                }
            }
        });
        btnGetLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("123", player.getDuration() + "");
                } catch (Exception e) {
                }
            }
        });
        seekBarProgress.setOnTouchListener(new View.OnTouchListener() {
                                               @Override
                                               public boolean onTouch(View v, MotionEvent event) {
                                                   if (player.isPlaying()) {
                                                       SeekBar sb = (SeekBar) v;
                                                       int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                                                       player.seekTo(playPositionInMillisecconds);
                                                   }
                                                   return false;
                                               }
                                           }
        );
    }

    private void initPlayer(){
        player = new MediaPlayer();
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBarProgress.setSecondaryProgress(percent);
            }
        });
        seekBarProgress.setMax(99); // It means 100% .0-99
    }

    private void getMusicTracks(String ownerId, int from, int count){
        VKParameters params = new VKParameters();
        params.put(VKApiConst.OWNER_ID, AppState.getLoggedUser().getUserId());
        params.put(VKApiConst.COUNT, "1");
        VKRequest requestaudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, ownerId,VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestaudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackSO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackSO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    Log.e("MainActivity", "Returned info about " + musicTracks.size() + " tracks.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Toast.makeText(MainActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
