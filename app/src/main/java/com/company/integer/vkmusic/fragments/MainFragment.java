package com.company.integer.vkmusic.fragments;


import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.adapters.SimpleRecyclerAdapter;
import com.company.integer.vkmusic.adapters.ViewPagerAdapter;
import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.VersionModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private Toolbar toolbar;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDownload;
    private TextView tvNameOfSongPlayerLine;
    private TextView tvAuthorPlayerLine;
    private TextView tvNameOfSongFragment;
    private TextView tvAuthorFragment;
    private TextView tvCurrentTime;
    private View playerLine;
    private ArgbEvaluator evaluator;
    private ImageView ivDivider;
    private ImageView ivClosePanel;
    private ImageView ivAlbumPhoto;
    private SeekBar seekBar;
    private TextView tvNowPlaying;
    private MusicPlayerInterface musicPlayer;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int mediaFileLengthInMilliseconds;
    private Handler handler = new Handler();

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabDownload = (FloatingActionButton) view.findViewById(R.id.fab_download);
        tvNameOfSongPlayerLine = (TextView) view.findViewById(R.id.tv_name_of_song_player);
        tvAuthorPlayerLine = (TextView) view.findViewById(R.id.tv_author_name_player);
        tvCurrentTime = (TextView) view.findViewById(R.id.tv_current_time);
        tvNameOfSongFragment = (TextView) view.findViewById(R.id.tv_name_of_song_fragment);
        tvAuthorFragment = (TextView) view.findViewById(R.id.tv_author_name_fragment);
        playerLine = view.findViewById(R.id.player_line);
        ivClosePanel = (ImageView) view.findViewById(R.id.iv_close_panel);
        ivDivider = (ImageView) view.findViewById(R.id.iv_divider);
        ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(99);
        seekBar.setSecondaryProgress(80);
       // seekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.accentColor), PorterDuff.Mode.MULTIPLY));
        tabLayout = (TabLayout) view.findViewById(R.id.tl_main);

        evaluator = new ArgbEvaluator();


        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)view.findViewById(R.id.sliding_up_panel);
        slidingUpPanelLayout.setDragView(playerLine);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                int[]location = new int[2];
                ivAlbumPhoto.getLocationOnScreen(location);
                ((MainActivity) getActivity()).setTranslations(v);
                fabAdd.setScaleX(v);
                fabAdd.setScaleY(v);
                fabAdd.setAlpha(v);
                fabDownload.setScaleX(v);
                fabDownload.setScaleY(v);
                fabDownload.setAlpha(v);
                tvNameOfSongPlayerLine.setAlpha(1 - v);
                tvAuthorPlayerLine.setAlpha(1 - v);
                tvCurrentTime.setAlpha(1 - v);
                playerLine.setBackgroundColor((Integer) evaluator.evaluate(v, getResources().getColor(R.color.listViewItemBackground),
                        getResources().getColor(R.color.accentColor)));
                tvNameOfSongFragment.setAlpha(v);
                tvAuthorFragment.setAlpha(v);
                ivDivider.setAlpha(1 - v);
                ivClosePanel.setImageDrawable(null);
            }

            @Override
            public void onPanelCollapsed(View view) {

            }

            @Override
            public void onPanelExpanded(View view) {
                ivClosePanel.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
            }

            @Override
            public void onPanelAnchored(View view) {
                Log.d("panel", "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d("panel", "onPanelHidden");
            }
        });

        viewPager = (ViewPager) view.findViewById(R.id.vp_main);


        seekBar.setOnTouchListener(new View.OnTouchListener() {
                                               @Override
                                               public boolean onTouch(View v, MotionEvent event) {
                                                   SeekBar sb = (SeekBar) v;
                                                   int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                                                   musicPlayer.setCurrentTrackTime(playPositionInMillisecconds);
                                                   return false;
                                               }
                                           }
        );

        return view;
    }




    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(((AppCompatActivity)getActivity()).getSupportFragmentManager());
        adapter.addFrag(new TabFragment(), "My music");
        adapter.addFrag(new TabFragment(), "Recommended");
        adapter.addFrag(new TabFragment(), "Saved");
        viewPager.setAdapter(adapter);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.primaryColorDark));
        tabLayout.setupWithViewPager(viewPager);
    }

    public void primarySeekBarProgressUpdater() {
        seekBar.setProgress((int) (((float) musicPlayer.getCurrentTrackTime() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (musicPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    public void setMusicPlayer(MusicPlayerInterface musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public SeekBar getSeekBar(){
        return seekBar;
    }

    public void setCurrentTrack(MusicTrackPOJO musicTrack){
        tvNameOfSongPlayerLine.setText(musicTrack.getTitle());
        tvNameOfSongFragment.setText(musicTrack.getTitle());
        tvAuthorPlayerLine.setText(musicTrack.getArtist());
        tvAuthorFragment.setText(musicTrack.getArtist());
    }

    public void setMediaFileLengthInMilliseconds(int mediaFileLengthInMilliseconds) {
        this.mediaFileLengthInMilliseconds = mediaFileLengthInMilliseconds;
    }

    public int getMediaFileLengthInMilliseconds() {
        return mediaFileLengthInMilliseconds;
    }
}
