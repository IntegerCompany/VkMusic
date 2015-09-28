package com.company.integer.vkmusic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.adapters.SimpleRecyclerAdapter;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.List;

public class TabFragment extends Fragment {

    SimpleRecyclerAdapter adapter;
    List<MusicTrackPOJO> list;

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dummy_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        list = ((MainActivity) getActivity()).getPlaylist();

        adapter = new SimpleRecyclerAdapter(list,(MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void updateList(){
        adapter.notifyDataSetChanged();
    }

    public void nextTrack(){
        adapter.nextTrack();
    }

    public void previousTrack(){
        adapter.previousTrack();
    }

    public void setCurrentTrackPosition(int position){
        adapter.setCurrentTrackPosition(position);
    }

}
