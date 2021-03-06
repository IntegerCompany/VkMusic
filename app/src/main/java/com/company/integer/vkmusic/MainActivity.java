package com.company.integer.vkmusic;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.company.integer.vkmusic.fragments.MainFragment;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.logic.TracksDataLoader;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.pojo.UserPOJO;
import com.company.integer.vkmusic.services.MusicPlayerService;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.company.integer.vkmusic.supportclasses.VkMusicAnalytic;
import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.vk.sdk.VKSdk;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        TracksLoaderInterface, TracksLoaderListener {

    private static final String LOG_TAG = "MainActivity";
    FloatingActionButton fabPrevious;
    FloatingActionButton fabPlayPause;
    FloatingActionButton fabNext;
    MainFragment mainFragment;
    SearchView etSearchText;
    MusicTrackPOJO currentMusicTrack;

    private TracksDataLoader tracksDataLoader;
    private int lastSource = TracksLoaderInterface.MY_TRACKS;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<MusicTrackPOJO> myTracksPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> recommendationsPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> savedPlaylist = new ArrayList<>();
    private ArrayList<MusicTrackPOJO> searchPlaylist = new ArrayList<>();

    private boolean isPlaying = false;
    private int currentPlaylist = TracksLoaderInterface.MY_TRACKS;
    private int currentTrack = 0;
    private String searchQuery = "";

    float baseFabPlayX = 0;
    float baseFabNextX = 0;
    float baseFabPreviousX = 0;
    boolean firstMeasure = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(AppState.getTheme());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(AppState.getColors().getColorAccentID());
        }
        setContentView(R.layout.activity_main);
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fabPrevious = (FloatingActionButton) findViewById(R.id.fab_previous);
        fabPlayPause = (FloatingActionButton) findViewById(R.id.fab_play_pause);
        fabNext = (FloatingActionButton) findViewById(R.id.fab_next);

        fabPlayPause.setBackgroundTintList(ColorStateList.valueOf(AppState.getColors().getColorPrimaryID()));
        fabPrevious.setBackgroundTintList(ColorStateList.valueOf(AppState.getColors().getColorAccentID()));
        fabNext.setBackgroundTintList(ColorStateList.valueOf(AppState.getColors().getColorAccentID()));

        tracksDataLoader = new TracksDataLoader(this);
        tracksDataLoader.setTracksLoadingListener(this);

        attemptToGetTracks();
        tracksDataLoader.getSavedTracks();

        fabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isPlaying) {
                    if (!isPlayListEmpty()) {
                        playMusic();
                    }
                } else {
                    pauseMusic();
                }
            }
        });

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlayListEmpty()) {
                    Intent nextIntent = new Intent("com.example.app.ACTION_NEXT");
                    sendBroadcast(nextIntent);
                    mainFragment.updateSeekBarAndTextViews(0);
                    mainFragment.setLoading();
                }
            }
        });

        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlayListEmpty()) {
                    Intent pauseIntent = new Intent("com.example.app.ACTION_BACK");
                    sendBroadcast(pauseIntent);
                    mainFragment.setLoading();
                }
            }
        });

        if (!Environment.getExternalStorageDirectory().canWrite()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        try {
            VkMusicAnalytic.getInstance().getTracker().setScreenName("MainActivity");
            VkMusicAnalytic.getInstance().getTracker().send(new HitBuilders.ScreenViewBuilder().build());
        }catch (Exception ignored){}

        mainFragment.setupViewPager();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSavedTracks();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, R.string.saved_deactivated, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerMyBroadcastReceiver();
        sendBroadcast(new Intent("com.example.app.ACTION_UPDATE_TRACK"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mainFragment.isOnSearchScreen()) {
            Gson gson = new Gson();
            SharedPreferences.Editor sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE).edit();
            sharedPreferences.putBoolean("isSearch", mainFragment.isOnSearchScreen());
            sharedPreferences.putString("searchQuery", etSearchText.getQuery().toString());
            if (searchPlaylist.size() > 20) sharedPreferences.putString("tracks", gson.toJson(searchPlaylist.subList(0, 9)));
            else sharedPreferences.putString("tracks", gson.toJson(searchPlaylist));
            sharedPreferences.putInt("currentTrack", currentTrack);
            sharedPreferences.apply();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isServiceRunning()) {
            Intent i = new Intent(MainActivity.this, MusicPlayerService.class);
            i.setAction("MY_TRACKS");
            i.putParcelableArrayListExtra("MY_TRACKS", new ArrayList<MusicTrackPOJO>());
            startService(i);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
        mainFragment.switchToTab(AppState.getTab(), false);
        if (sharedPreferences.getBoolean("isSearch", false)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<MusicTrackPOJO>>() {
            }.getType();
            searchQuery = sharedPreferences.getString("searchQuery", "");
            searchPlaylist = gson.fromJson(sharedPreferences.getString("tracks", ""), type);
            currentTrack = sharedPreferences.getInt("currentTrack", 0);
            if (searchPlaylist == null) searchPlaylist = new ArrayList<>();
            mainFragment.searchCompleted(searchPlaylist, currentTrack);
        }else mainFragment.makeSearchUIActions(false);
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
        mainFragment.setLoading();
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
        changePlayingTrackIntent.putExtra("currentPlaylist", currentPlaylist);
        sendBroadcast(changePlayingTrackIntent);
    }

    public void setTranslations(float k) {
        if (firstMeasure){
            baseFabPlayX = fabPlayPause.getX();
            baseFabNextX = fabNext.getX();
            baseFabPreviousX = fabPrevious.getX();
            firstMeasure = false;
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        float endFabPlayX = ((size.x / 2) - fabPlayPause.getWidth()/2) * k;
        float diff = baseFabPlayX - endFabPlayX;
        float playX = diff * k;
        playX = baseFabPlayX - playX;
        float nextX = (playX + dpToPx(60)) + dpToPx((int) ((k - 0.01) * 10));
        float previousX = playX - dpToPx(44) - dpToPx((int) ((k - 0.01) * 10));

        fabPlayPause.setX(playX);
        fabPlayPause.setTranslationY(-(height - dpToPx(446)) * k);
        fabPrevious.setX(previousX);
        fabPrevious.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
        fabNext.setX(nextX);
        fabNext.setTranslationY(-(height - dpToPx(446) + dpToPx(27)) * k);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5);
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
    public void getSavedTracks() {
        tracksDataLoader.getSavedTracks();
    }

    @Override
    public void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener) {
        //dataLoadingCallbackForUI = tracksLoaderListener;
    }

    // TracksDataLoader callbacks methods-----------
    @Override
    public void tracksLoaded(final ArrayList<MusicTrackPOJO> newTracks, final int source) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Starting service on track loaded
                Intent i = new Intent(MainActivity.this, MusicPlayerService.class);

                if (source == currentPlaylist && currentMusicTrack == null) {
                    currentTrack = 0;
                    currentMusicTrack = newTracks.get(currentTrack);
                    mainFragment.setCurrentTrack(currentMusicTrack, currentTrack);
                }
                switch (source) {
                    case TracksLoaderInterface.MY_TRACKS:
                        myTracksPlaylist.addAll(newTracks);
                        if (!isServiceRunning()) {
                            i.setAction("MY_TRACKS");
                            i.putParcelableArrayListExtra("MY_TRACKS", newTracks);
                            startService(i);
                        }
                        mainFragment.updateList();
                        mainFragment.showError(false);
                        break;
                    case TracksLoaderInterface.RECOMMENDATIONS:
                        recommendationsPlaylist.addAll(newTracks);
                        mainFragment.updateList();
                        mainFragment.showError(false);
                        break;
                    case TracksLoaderInterface.SAVED:
                        savedPlaylist.clear();
                        savedPlaylist.addAll(newTracks);
                        mainFragment.updateList();
                        if (!isServiceRunning()) {
                            i.setAction("MY_TRACKS");
                            i.putParcelableArrayListExtra("MY_TRACKS", newTracks);
                            startService(i);
                        }
                        break;
                    case TracksLoaderInterface.SEARCH:
                        searchPlaylist.addAll(newTracks);
                        mainFragment.searchCompleted(searchPlaylist);
                        break;

                }
                mainFragment.showLoading(false);
                if (currentPlaylist == source) {
                    setCurrentPlaylist(source);
                }
            }
        });

    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.company.integer.vkmusic.services.MusicPlayerService".equals(service.service.getClassName())) {
                Log.d("TESTing2", "TRUE");
                return true;
            }
        }
        Log.d("TESTing2", "FALSE");
        return false;
    }

    @Override
    public void tracksLoadingError(final String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainFragment.showError(true);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void trackDownloadingProgress(MusicTrackPOJO track, int percent) {
        Log.d(LOG_TAG, getString(R.string.downloading_notification) + track.getTitle() + " | " + percent + "%");
    }

    @Override
    public void trackDownloadFinished(final MusicTrackPOJO track) {
        Log.d(LOG_TAG, getString(R.string.download_complete_notification) + track.getTitle());


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.downloaded_notification) + track.getTitle(), Toast.LENGTH_SHORT).show();
                getSavedTracks();
            }
        });
    }

    @Override
    public void trackLyricsReceived(String lyrics) {
        mainFragment.lyricsReceived(lyrics);

    }
    // TracksDataLoader callbacks methods end-----------

    @Override
    public void uploadMore(int source) {
        switch (source) {
            case TracksLoaderInterface.MY_TRACKS:
                getTracksByUserId(AppState.getLoggedUserID(), myTracksPlaylist.size() + 1, AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.RECOMMENDATIONS:
                getRecommendationsByUserID(AppState.getLoggedUserID(), recommendationsPlaylist.size() + 1, AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.SEARCH:
                search(tracksDataLoader.getLastSearchQuery(), searchPlaylist.size() + 1, AppState.TRACKS_PER_LOADING);
                break;
            case TracksLoaderInterface.USE_PREVIOUS:
                uploadMore(lastSource);
                break;
        }
    }

    @Override
    public void getTrackLyrics(String id) {
        tracksDataLoader.getTrackLyrics(id);
    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        return null;
    }

    @Override
    public void downloadTrack(MusicTrackPOJO trackToDownload) {
        tracksDataLoader.downloadTrack(trackToDownload);
    }

    @Override
    public void addTrackToVkPlaylist(MusicTrackPOJO track) {
        tracksDataLoader.addTrackToVkPlaylist(track);
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
                    isPlaying = false;
                    mainFragment.updateList();
                    pauseMusicUIAction();
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_BACK")) {

                } else if (action.equalsIgnoreCase("com.example.app.ACTION_NEXT")) {

                } else if (action.equalsIgnoreCase("com.example.app.ACTION_TRACK_CHANGED")) {
                    MusicTrackPOJO musicTrack = intent.getParcelableExtra("musicTrack");
                    int time = intent.getExtras().getInt("CurrentTrackTime");
                    if (musicTrack != null) {
                        Log.d("debug", "current track before = " + currentTrack);
                        currentMusicTrack = musicTrack;
                        currentTrack = intent.getIntExtra("musicTrackPosition", currentTrack);
                        Log.d("debug", "current track after = " + currentTrack);
                        mainFragment.setCurrentTrack(musicTrack, currentTrack);
                        setCurrentPlaylist(intent.getIntExtra("currentPlaylist", currentPlaylist));
                        mainFragment.setMediaFileLengthInMilliseconds(musicTrack.getDuration() * 1000);
                        mainFragment.getSeekBar().setProgress((int) (((float) time / mainFragment.getMediaFileLengthInMilliseconds()) * 100)); // This math construction give a percentage of "was playing"/"song length"
                            if (time == 0) {
                            mainFragment.getSeekBar().setProgress(0);
                        }
                        if (intent.getExtras().getBoolean("isPlaying")) {
                            playMusicUIAction();
                            isPlaying = true;
                        }

                    }
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_LOADING_PROGRESS")) {
                    int percent = intent.getExtras().getInt("percent");
                    mainFragment.getSeekBar().setSecondaryProgress(percent);
                } else if (action.equalsIgnoreCase("com.example.app.ACTION_TRACK_PROGRESS")) {
                    int trackTime = intent.getExtras().getInt("currentTrackTime");

                    currentTrack = intent.getIntExtra("currentTrack", currentTrack);
                    setCurrentPlaylist(intent.getIntExtra("currentPlaylist", currentPlaylist));
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
    protected void onStop() {
        super.onStop();
        Log.d("Panel", "Sending intent");
        Intent stopService = new Intent("com.example.app.ACTION_DESTROY");
        sendBroadcast(stopService);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        etSearchText = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        etSearchText.setSuggestionsAdapter(createSearchViewAdapter());
        etSearchText.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                etSearchText.setQuery(AppState.getSearchHistory().get(position), true);
                return true;
            }
        });

        etSearchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                makeSearch(etSearchText.getQuery().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        etSearchText.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setCurrentPlaylist(TracksLoaderInterface.MY_TRACKS);
                mainFragment.makeSearchUIActions(false);
                searchQuery = "";
                mainFragment.switchToTab(TracksLoaderInterface.MY_TRACKS, true);
                return false;
            }
        });
        if (!searchQuery.equals("")) etSearchText.setQuery(searchQuery, false);
        mainFragment.makeSearchUIActions(getSharedPreferences("save", MODE_PRIVATE).getBoolean("isSearch", false));

        return true;
    }

    public void makeSearch(String query) {
        etSearchText.setQuery(query, false);
        AppState.addToSearchHistory(query);
        mainFragment.setSlidingUpPanelLayoutPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        getSearchPlaylist().clear();
        mainFragment.updateList();
        search(query, 0, 10);
        mainFragment.makeSearchUIActions(true);
        etSearchText.setSuggestionsAdapter(createSearchViewAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //todo add logic to options
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent in = new Intent(this, SettingsActivity.class);
                startActivity(in);
                finish();
                return true;
            case R.id.action_log_out:

                new AlertDialog.Builder(this)
                        .setTitle("Logout?")
                        .setPositiveButton("Yes, logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VKSdk.logout();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

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
        switch (currentPlaylist) {
            case TracksLoaderInterface.MY_TRACKS:
                changePlaylist.putExtra("playlist", myTracksPlaylist);
                changePlaylist.putExtra("track", currentTrack);
                break;
            case TracksLoaderInterface.RECOMMENDATIONS:
                changePlaylist.putExtra("playlist", recommendationsPlaylist);
                break;
            case TracksLoaderInterface.SAVED:
                changePlaylist.putExtra("playlist", savedPlaylist);
                break;
            case TracksLoaderInterface.SEARCH:
                changePlaylist.putExtra("playlist", searchPlaylist);
                break;
        }
        changePlaylist.putExtra("currentPlaylist", currentPlaylist);

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

    public ArrayList<MusicTrackPOJO> getPlaylistByName(int source) {
        switch (source) {
            case TracksLoaderInterface.MY_TRACKS:
                return myTracksPlaylist;
            case TracksLoaderInterface.RECOMMENDATIONS:
                return recommendationsPlaylist;
            case TracksLoaderInterface.SAVED:
                return savedPlaylist;
            case TracksLoaderInterface.SEARCH:
                return searchPlaylist;
        }
        return myTracksPlaylist;
    }

    private boolean isPlayListEmpty() {
        boolean isPlayListEmpty = false;
        if (mainFragment.isSearchEnabled()) {
            if (searchPlaylist.isEmpty()) {
                isPlayListEmpty = true;
            }
        } else {
            switch (mainFragment.getCurrentTab()) {
                case 0:
                    Log.d("TAB", "1");
                    if (myTracksPlaylist.isEmpty()) {
                        isPlayListEmpty = true;
                    }
                    break;
                case 1:
                    Log.d("TAB", "2");
                    if (recommendationsPlaylist.isEmpty()) {
                        isPlayListEmpty = true;
                    }
                    break;
                case 2:
                    Log.d("TAB", "3");
                    if (savedPlaylist.isEmpty()) {
                        isPlayListEmpty = true;
                    }
                    break;
            }
        }
        return isPlayListEmpty;
    }

    public MusicTrackPOJO getCurrentMusicTrack() {
        return currentMusicTrack;
    }

    private SimpleCursorAdapter createSearchViewAdapter(){
        String[] columnNames = {"_id","text"};
        MatrixCursor cursor = new MatrixCursor(columnNames);
        String[] array = new String[AppState.getSearchHistory().size()];
        array = AppState.getSearchHistory().toArray(array);
        String[] temp = new String[2];
        int id = 0;
        for(String item : array){
            temp[0] = Integer.toString(id++);
            temp[1] = item;
            cursor.addRow(temp);
        }
        String[] from = {"text"};
        int[] to = {R.id.tv_search_history_text};
        return new SimpleCursorAdapter(this, R.layout.searchview_autocomplete_item, cursor, from, to);
    }


    public void clearSearchQuery(){
        searchQuery = "";
        SharedPreferences.Editor sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE).edit();
        sharedPreferences.putBoolean("isSearch", false);
        sharedPreferences.putString("tracks", "");
        sharedPreferences.putInt("currentTrack", 0);
        sharedPreferences.apply();
    }


    public void attemptToGetTracks(){
        tracksDataLoader.getTracksByUserId(AppState.getLoggedUserID(), myTracksPlaylist.size(), 10);
        tracksDataLoader.getRecommendationsByUserID(AppState.getLoggedUserID(), recommendationsPlaylist.size(), 10);
    }

}