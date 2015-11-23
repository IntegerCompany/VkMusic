package com.company.integer.vkmusic.fragments;


import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.adapters.SimpleRecyclerAdapter;
import com.company.integer.vkmusic.adapters.ViewPagerAdapter;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.company.integer.vkmusic.supportclasses.VkMusicAnalytic;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    LinearLayoutManager lm;
    TabFragment myMusicFragment, recommendedFragment, savedFragment;
    SimpleRecyclerAdapter adapter;
    boolean scrollDownLock = false;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabDownload;
    private TextView tvNameOfSongPlayerLine;
    private TextView tvAuthorPlayerLine;
    private TextView tvNameOfSongFragment;
    private TextView tvAuthorFragment;
    private TextView tvCurrentTimePlayer;
    private TextView tvCurrentTimePlayerLine;
    private View playerLine;
    private ArgbEvaluator evaluator;
    private ImageView ivDivider;
    private ImageView ivClosePanel;
  //  private ImageView ivAlbumPhoto;
    private ImageView imgSearchByArtist, imgLyrics;
    private SeekBar seekBar;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int mediaFileLengthInMilliseconds;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private View view;
    private AdView playerAdvert;
    private boolean isSearch;
    public MainFragment() {
        // Required empty public constructor
    }
    AlertDialog lyricsDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_main, container, false);
        myMusicFragment = new TabFragment();
        recommendedFragment = new TabFragment();
        savedFragment = new TabFragment();
        myMusicFragment.setupWith(((MainActivity) getActivity()).getMyTracksPlaylist(), TracksLoaderInterface.MY_TRACKS);
        recommendedFragment.setupWith(((MainActivity) getActivity()).getRecommendationsPlaylist(), TracksLoaderInterface.RECOMMENDATIONS);
        savedFragment.setupWith(((MainActivity) getActivity()).getSavedPlaylist(), TracksLoaderInterface.SAVED);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher));

        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabDownload = (FloatingActionButton) view.findViewById(R.id.fab_download);
        fabAdd.setBackgroundTintList(ColorStateList.valueOf(AppState.getColors().getColorAccentID()));
        fabDownload.setBackgroundTintList(ColorStateList.valueOf(AppState.getColors().getColorAccentID()));
        tvNameOfSongPlayerLine = (TextView) view.findViewById(R.id.tv_name_of_song_player);
        tvAuthorPlayerLine = (TextView) view.findViewById(R.id.tv_author_name_player);
        tvCurrentTimePlayer = (TextView) view.findViewById(R.id.tv_current_time_player);
        tvCurrentTimePlayerLine = (TextView) view.findViewById(R.id.tv_current_time_player_line);
        tvNameOfSongFragment = (TextView) view.findViewById(R.id.tv_name_of_song_fragment);
        tvAuthorFragment = (TextView) view.findViewById(R.id.tv_author_name_fragment);
        playerLine = view.findViewById(R.id.player_line);
        ivClosePanel = (ImageView) view.findViewById(R.id.iv_close_panel);
        ivDivider = (ImageView) view.findViewById(R.id.iv_divider);
      //  ivAlbumPhoto = (ImageView) view.findViewById(R.id.some_id);
        imgSearchByArtist = (ImageView) view.findViewById(R.id.imgSearchByArtist);
        imgLyrics = (ImageView) view.findViewById(R.id.img_lyrics);
      //  ivAlbumPhoto.setImageDrawable(ContextCompat.getDrawable(getContext(), AppState.getColors().getImageDrawableID()));

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setSecondaryProgress(0);
        seekBar.setProgress(0);
        tabLayout = (TabLayout) view.findViewById(R.id.tl_main);

        evaluator = new ArgbEvaluator();
        recyclerView = (RecyclerView) view.findViewById(R.id.searchList);
        adapter = new SimpleRecyclerAdapter(new ArrayList<MusicTrackPOJO>(),(MainActivity) getActivity());
        adapter.setAdapterSource(TracksLoaderInterface.SEARCH);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (((MainActivity) getActivity()).getSearchPlaylist().size() != 0) {
                    if (lm.findLastVisibleItemPosition() > ((MainActivity) getActivity()).getSearchPlaylist().size() - 2) {
                        if (!scrollDownLock)
                            ((MainActivity) getActivity()).uploadMore(TracksLoaderInterface.SEARCH);
                        scrollDownLock = true;
                    } else {
                        scrollDownLock = false;
                    }
                }
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).addTrackToVkPlaylist(((MainActivity) getActivity()).getCurrentMusicTrack());
                AppState.saveTrackId(Integer.parseInt(((MainActivity) getActivity()).getCurrentMusicTrack().getId()));
                fabAdd.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ok));
                fabAdd.setClickable(false);
                VkMusicAnalytic.getInstance().addToPlaylistPressed();
            }
        });
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabDownload.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ok));
                ((MainActivity) getActivity()).downloadTrack(((MainActivity) getActivity()).getCurrentMusicTrack());
            }
        });

        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_up_panel);
        slidingUpPanelLayout.setDragView(playerLine);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                int[] location = new int[2];
           //     ivAlbumPhoto.getLocationOnScreen(location);
                ((MainActivity) getActivity()).setTranslations(v);
                fabAdd.setScaleX(v);
                fabAdd.setScaleY(v);
                fabAdd.setAlpha(v);
                fabDownload.setScaleX(v);
                fabDownload.setScaleY(v);
                fabDownload.setAlpha(v);
                tvNameOfSongPlayerLine.setAlpha(1 - v);
                tvAuthorPlayerLine.setAlpha(1 - v);
                tvCurrentTimePlayerLine.setAlpha(1 - v);
                playerLine.setBackgroundColor((Integer) evaluator.evaluate(v, ContextCompat.getColor(getContext(), R.color.listViewItemBackground),
                        AppState.getColors().getColorPrimaryID()));
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
                ivClosePanel.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_close_clear_cancel));
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
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position){
//                    case 0:
//                        ((MainActivity) getActivity()).setCurrentPlaylist(TracksLoaderInterface.MY_TRACKS);
//                        break;
//                    case 1:
//                        ((MainActivity) getActivity()).setCurrentPlaylist(TracksLoaderInterface.RECOMMENDATIONS);
//                        break;
//                    case 2:
//                        ((MainActivity) getActivity()).setCurrentPlaylist(TracksLoaderInterface.SAVED);
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int playPositionInMilliseconds = (mediaFileLengthInMilliseconds / 100) * seekBar.getProgress();
                Intent in = new Intent("com.example.app.ACTION_TIME_CHANGED");
                in.putExtra("CurrentTrackTime", playPositionInMilliseconds);
                getActivity().sendBroadcast(in);
            }

        });
        initAdverts();

        imgSearchByArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).makeSearch(((MainActivity) getActivity()).getCurrentMusicTrack().getArtist());
            }
        });

        imgLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyricsDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(((MainActivity) getActivity()).getCurrentMusicTrack().getTitle())
                        .setView(new ProgressBar(getActivity())).show();
                ((MainActivity) getActivity()).getTrackLyrics(String.valueOf(((MainActivity) getActivity()).getCurrentMusicTrack().getLyricsId()));
            }
        });

        return view;
    }


    public SlidingUpPanelLayout.PanelState getSlidingUpPanelLayoutPanelState() {
        return slidingUpPanelLayout.getPanelState();
    }

    public void setSlidingUpPanelLayoutPanelState(SlidingUpPanelLayout.PanelState state) {
        slidingUpPanelLayout.setPanelState(state);
    }

    public void lyricsReceived(String lyrics){
        if (lyricsDialog.isShowing()){
            lyricsDialog.dismiss();
            new AlertDialog.Builder(getActivity())
                    .setTitle(((MainActivity) getActivity()).getCurrentMusicTrack().getTitle())
                    .setMessage(lyrics)
                    .show();
        }
    }

    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        adapter.addFrag(myMusicFragment, getActivity().getString(R.string.my_music));
        adapter.addFrag(recommendedFragment, getActivity().getString(R.string.recommended));
        adapter.addFrag(savedFragment, getActivity().getString(R.string.saved));
        viewPager.setAdapter(adapter);


        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.primaryColorDark));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                makeSearchUIActions(false);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void updateList() {
        myMusicFragment.updateList();
        recommendedFragment.updateList();
        savedFragment.updateList();
     //   if (adapter != null) adapter.notifyDataSetChanged();
    }
    
    public void updateSeekBarAndTextViews(int time) {
        seekBar.setProgress((int) (((float) time / mediaFileLengthInMilliseconds) * 100));
        tvCurrentTimePlayerLine.setText(getDurationString(time / 1000));
        tvCurrentTimePlayer.setText(getDurationString(time / 1000));
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void setCurrentTrack(MusicTrackPOJO musicTrack, int position) {
        if(musicTrack!=null){
            Log.d("debug", "position" + position);
            if (lyricsDialog != null && lyricsDialog.isShowing()) lyricsDialog.dismiss();
            if (((MainActivity) getActivity()).getCurrentMusicTrack() == null && ((MainActivity) getActivity()).getCurrentMusicTrack().getLyricsId() == 0) imgLyrics.setVisibility(View.INVISIBLE);
            else imgLyrics.setVisibility(View.VISIBLE);
            tvNameOfSongPlayerLine.setText(musicTrack.getTitle());
            tvNameOfSongFragment.setText(musicTrack.getTitle());
            tvAuthorPlayerLine.setText(musicTrack.getArtist());
            tvAuthorFragment.setText(musicTrack.getArtist());
            switch (((MainActivity) getActivity()).getCurrentPlaylist()){
                case TracksLoaderInterface.MY_TRACKS:
                    myMusicFragment.setCurrentTrackPosition(position);
                    fabAdd.setVisibility(View.VISIBLE);
                    break;
                case TracksLoaderInterface.RECOMMENDATIONS:
                    Log.d("debug", "set recommendations track" + position);
                    recommendedFragment.setCurrentTrackPosition(position);
                    fabAdd.setVisibility(View.VISIBLE);
                    break;
                case TracksLoaderInterface.SAVED:
                    savedFragment.setCurrentTrackPosition(position);
                    fabAdd.setVisibility(View.INVISIBLE);
                    break;
                case TracksLoaderInterface.SEARCH:
                    adapter.setCurrentTrackPosition(position);
                    fabAdd.setVisibility(View.VISIBLE);
                    break;
            }

            File vkMusicDirectory = new File(Environment
                    .getExternalStorageDirectory().toString()
                    + AppState.FOLDER);
            final File path = new File(vkMusicDirectory + "/" + musicTrack.getArtist() + "-" + musicTrack.getTitle() + ".mp3");

            if (!path.exists()) {
                fabDownload.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.download));
            } else {
                fabDownload.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ok));
            }

            if (((MainActivity) getActivity()).getCurrentPlaylist() == TracksLoaderInterface.RECOMMENDATIONS | ((MainActivity) getActivity()).getCurrentPlaylist() == TracksLoaderInterface.SEARCH) {
                for (int id : AppState.getSavedIds()) {
                    if (Integer.parseInt(musicTrack.getId()) == id) {
                        fabAdd.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.ok));
                        fabAdd.setClickable(false);
                        break;
                    } else {
                        fabAdd.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.add));
                        fabAdd.setClickable(true);
                    }
                }
            }

        }
    }

    public int getMediaFileLengthInMilliseconds() {
        return mediaFileLengthInMilliseconds;
    }

    public void setMediaFileLengthInMilliseconds(int mediaFileLengthInMilliseconds) {
        this.mediaFileLengthInMilliseconds = mediaFileLengthInMilliseconds;
    }

    private String getDurationString(int durationInSec) {
        int minutes = durationInSec / 60;
        int seconds = durationInSec - minutes * 60;
        if (seconds < 10) {
            if (seconds == 0) return getActivity().getString(R.string.loading);
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

    public void searchCompleted(ArrayList<MusicTrackPOJO> searchPlaylist) {
        ((MainActivity) getActivity()).setCurrentPlaylist(TracksLoaderInterface.SEARCH);
        adapter.updateTracks(searchPlaylist);
        adapter.notifyDataSetChanged();


    }

    public void searchCompleted(ArrayList<MusicTrackPOJO> searchPlaylist, int position) {
        ((MainActivity) getActivity()).setCurrentPlaylist(TracksLoaderInterface.SEARCH);
        adapter.updateTracks(searchPlaylist);
        adapter.setCurrentTrackPosition(position);
        adapter.notifyDataSetChanged();


    }



    public void makeSearchUIActions(boolean isSearch) {
        this.isSearch = isSearch;
        if (isSearch) {
            viewPager.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).clearSearchQuery();
            viewPager.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void switchToTab(int tab, boolean smooth) {
        switch (tab) {
            case TracksLoaderInterface.MY_TRACKS:
                viewPager.setCurrentItem(0, smooth);
                break;
            case TracksLoaderInterface.RECOMMENDATIONS:
                viewPager.setCurrentItem(1, smooth);
                break;
            case TracksLoaderInterface.SAVED:
                viewPager.setCurrentItem(2, smooth);
                break;

        }
    }



    private void initAdverts() {
        AdView mAdView = (AdView) view.findViewById(R.id.player_advert);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public boolean isSearchEnabled(){
        return viewPager.getVisibility()==View.GONE;
    }

    public int getCurrentTab(){
       return viewPager.getCurrentItem();
    }

    public void setLoading(){
        tvCurrentTimePlayerLine.setText(getActivity().getString(R.string.loading));
        tvCurrentTimePlayer.setText(getActivity().getString(R.string.loading));
    }

    public boolean isOnSearchScreen(){
        return isSearch;
    }



}
