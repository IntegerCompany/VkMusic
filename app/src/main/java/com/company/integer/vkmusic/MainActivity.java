package com.company.integer.vkmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.company.integer.vkmusic.fragments.MainFragment;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.logic.TracksDataLoader;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.services.MusicPlayerService;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        TracksLoaderInterface, TracksLoaderListener {

    FloatingActionButton fabPrevious;
    FloatingActionButton fabPlayPause;
    FloatingActionButton fabNext;
    private static final String LOG_TAG = "MainActivity";
    MainFragment mainFragment;

    private TracksDataLoader tracksDataLoader;
    private int lastSource = TracksLoaderInterface.MY_TRACKS;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<MusicTrackPOJO> myTracksPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> recommendationsPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> savedPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> searchPlaylist = new ArrayList<>();

    private boolean isPlaying = false;
    private int currentPlaylist = TracksLoaderInterface.MY_TRACKS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracksDataLoader = new TracksDataLoader();
        tracksDataLoader.setTracksLoadingListener(this);
        tracksDataLoader.getTracksByUserId(AppState.getLoggedUser().getUserId(), 1, 10);

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fabPrevious = (FloatingActionButton) findViewById(R.id.fab_previous);
        fabPlayPause = (FloatingActionButton) findViewById(R.id.fab_play_pause);
        fabNext = (FloatingActionButton) findViewById(R.id.fab_next);

        fabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    playMusic();
                } else {
                    pauseMusic();
                }
            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent("com.example.app.ACTION_NEXT");
                sendBroadcast(nextIntent);
                mainFragment.updateSeekBarAndTextViews(0);
            }
        });

        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pauseIntent = new Intent("com.example.app.ACTION_BACK");
                sendBroadcast(pauseIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerMyBroadcastReceiver();
        sendBroadcast(new Intent("com.example.app.ACTION_UPDATE_TRACK"));
    }

    @Override
    public void onBackPressed() {
        if (mainFragment.getSlidingUpPanelLayoutPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mainFragment.setSlidingUpPanelLayoutPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public void playMusic() {
        isPlaying = true;
        Intent playIntent = new Intent("com.example.app.ACTION_PLAY");
        sendBroadcast(playIntent);
    }

    private void playMusicUIAction() {
        fabPlayPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.pause));
    }

    public void pauseMusic() {
        isPlaying = false;
        Intent pauseIntent = new Intent("com.example.app.ACTION_PAUSE");
        sendBroadcast(pauseIntent);
    }

    private void pauseMusicUIAction() {
        fabPlayPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.play));
    }

    public void setPlayingTrack(int position) {
        Intent changePlayingTrackIntent = new Intent("com.example.app.ACTION_SET_TRACK");
        changePlayingTrackIntent.putExtra("newTrackPosition", position);
        sendBroadcast(changePlayingTrackIntent);
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

    public ArrayList<MusicTrackPOJO> getPlaylist() {
        Log.d(LOG_TAG, "Get playlist");
        return myTracksPlaylist;
    }

    /**
     * TRACK DATA LOADER START
     *
     * @param query text from search field
     * @param from  from track with that number we will load new ones
     * @param count how many tracks will be returned in response
     */
    @Override
    public void search(String query, int from, int count) {
        tracksDataLoader.search(query, from, count);
    }

    @Override
    public void getTracksByUserId(String userId, int from, int count) {
        tracksDataLoader.getTracksByUserId(userId, from, count);
    }

    @Override
    public void getRecommendationsByUserID(String userId, int from, int count) {
        tracksDataLoader.getRecommendationsByUserID(userId, from, count);
    }

    @Override
    public void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener) {
        //dataLoadingCallbackForUI = tracksLoaderListener;
    }

    // TracksDataLoader callbacks methods-----------
    @Override
    public void tracksLoaded(ArrayList<MusicTrackPOJO> newTracks, int source) {
        //Starting service on track loaded
        Intent i = new Intent(this, MusicPlayerService.class);

        switch (source) {
            case TracksLoaderInterface.MY_TRACKS:
                myTracksPlaylist.addAll(newTracks);
                i.setAction("MY_TRACKS");
                i.putParcelableArrayListExtra("MY_TRACKS", newTracks);
                startService(i);
                mainFragment.setupViewPager();
                break;
            case TracksLoaderInterface.RECOMMENDATIONS:
                recommendationsPlaylist.addAll(newTracks);
                break;
            case TracksLoaderInterface.SAVED:
                savedPlaylist.addAll(newTracks);
                break;
            case TracksLoaderInterface.SEARCH:
                searchPlaylist.addAll(newTracks);
                mainFragment.searchCompleted(searchPlaylist);
                break;
        }


    }

    @Override
    public void tracksLoadingError(String errorMessage) {
        tracksLoadingError(errorMessage);
    }
    // TracksDataLoader callbacks methods end-----------

    @Override
    public void uploadMore(int source) {
        switch (source) {
            case TracksLoaderInterface.MY_TRACKS:
                getTracksByUserId(AppState.getLoggedUser().getUserId(), myTracksPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.RECOMMENDATIONS:
                getRecommendationsByUserID(tracksDataLoader.getLastSearchQuery(), recommendationsPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.SEARCH:
                search(tracksDataLoader.getLastSearchQuery(), searchPlaylist.size(), AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.USE_PREVIOUS:
                uploadMore(lastSource);
                break;
        }
    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        return null;
    }

    public void registerMyBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equalsIgnoreCase("com.example.app.ACTION_PLAY")) {
                    isPlaying = true;
                    mainFragment.updateList();
                    playMusicUIAction();
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_PAUSE")) {
                    mainFragment.updateList();
                    isPlaying = false;
                    pauseMusicUIAction();
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_BACK")) {

                } else if (action.equalsIgnoreCase("com.example.app.ACTION_NEXT")) {

                } else if (action.equalsIgnoreCase("com.example.app.ACTION_TRACK_CHANGED")) {
                    MusicTrackPOJO musicTrack = intent.getParcelableExtra("musicTrack");
                    int time = intent.getExtras().getInt("CurrentTrackTime");
                    mainFragment.setCurrentTrack(musicTrack, intent.getIntExtra("musicTrackPosition", 0));
                    mainFragment.setMediaFileLengthInMilliseconds(musicTrack.getDuration() * 1000);
                    mainFragment.getSeekBar().setProgress((int) (((float) time / mainFragment.getMediaFileLengthInMilliseconds()) * 100)); // This math construction give a percentage of "was playing"/"song length"
                    if (time == 0){
                        mainFragment.getSeekBar().setProgress(0);
                    }
                    if(intent.getExtras().getBoolean("isPlaying")){
                        playMusicUIAction();
                        isPlaying=true;
                    }
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_LOADING_PROGRESS")) {
                    int percent = intent.getExtras().getInt("percent");
                    mainFragment.getSeekBar().setSecondaryProgress(percent);
                    Log.d("SeekBar", "receiving" + percent);
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_TRACK_PROGRESS")) {
                    int trackTime = intent.getExtras().getInt("currentTrackTime");
                    mainFragment.updateSeekBarAndTextViews(trackTime);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("com.example.app.ACTION_PLAY");
        intentFilter.addAction("com.example.app.ACTION_PAUSE");
        intentFilter.addAction("com.example.app.ACTION_BACK");
        intentFilter.addAction("com.example.app.ACTION_NEXT");
        intentFilter.addAction("com.example.app.ACTION_TRACK_CHANGED");
        intentFilter.addAction("com.example.app.ACTION_LOADING_PROGRESS");
        intentFilter.addAction("com.example.app.ACTION_TRACK_PROGRESS");

        // register the receiver
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //todo add logic to options
        switch (item.getItemId()) {
            case R.id.action_my_music:
                return true;
            case R.id.action_friends:
                return true;
            case R.id.action_group:
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(int currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
        Intent changePlaylist = new Intent("com.example.app.ACTION_CHANGE_PLAYLIST");
        switch (currentPlaylist){
            case TracksLoaderInterface.MY_TRACKS:
                changePlaylist.putExtra("playlist", myTracksPlaylist);
                break;
            case TracksLoaderInterface.SEARCH:
                changePlaylist.putExtra("playlist", searchPlaylist);
                break;
        }
        sendBroadcast(changePlaylist);
    }

    public ArrayList<MusicTrackPOJO> getSavedPlaylist() {
        return savedPlaylist;
    }

    public ArrayList<MusicTrackPOJO> getSearchPlaylist() {
        return searchPlaylist;
    }

    public ArrayList<MusicTrackPOJO> getRecommendationsPlaylist() {
        return recommendationsPlaylist;
    }

    public ArrayList<MusicTrackPOJO> getMyTracksPlaylist() {
        return myTracksPlaylist;
    }
}