package com.company.integer.vkmusic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.adapters.SimpleRecyclerAdapter;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.VersionModel;

import java.util.ArrayList;
import java.util.List;

public class TabFragment extends Fragment {

    SimpleRecyclerAdapter adapter;

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dummy_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        List<MusicTrackPOJO> list = ((MainActivity) getActivity()).getMusicPlayer().getPlaylist();

        adapter = new SimpleRecyclerAdapter(((MainActivity) getActivity()).getMusicPlayer(),list,getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }
}
