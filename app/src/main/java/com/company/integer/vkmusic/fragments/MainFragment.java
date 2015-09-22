package com.company.integer.vkmusic.fragments;


import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.adapters.SimpleRecyclerAdapter;
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
    ImageView ivDivider;
    ImageView ivClosePanel;

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
        evaluator = new ArgbEvaluator();


        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)view.findViewById(R.id.sliding_up_panel);
        slidingUpPanelLayout.setDragView(playerLine);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                ((MainActivity)getActivity()).setTranslations(v);
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
                ivDivider.setAlpha(1-v);
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
                Log.d("panel","onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View view) {
                Log.d("panel","onPanelHidden");
            }
        });

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp_main);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tl_main);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.primaryColorDark));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public static class DummyFragment extends android.support.v4.app.Fragment {
        int color;
        SimpleRecyclerAdapter adapter;

        public DummyFragment() {
        }

        @SuppressLint("ValidFragment")
        public DummyFragment(int color) {
            this.color = color;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dummy_fragment, container, false);

            final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.dummyfrag_bg);
            frameLayout.setBackgroundColor(color);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

            List<String> list = new ArrayList<String>();
            for (int i = 0; i < VersionModel.data.length; i++) {
                list.add(VersionModel.data[i]);
            }

            adapter = new SimpleRecyclerAdapter(list);
            recyclerView.setAdapter(adapter);

            return view;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(((AppCompatActivity)getActivity()).getSupportFragmentManager());
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.accent_material_light)), "My music");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.ripple_material_light)), "Recommended");
        adapter.addFrag(new DummyFragment(getResources().getColor(R.color.button_material_dark)), "Saved");
        viewPager.setAdapter(adapter);
    }

}
