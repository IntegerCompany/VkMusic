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
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.List;

public class TabFragment extends Fragment {

    SimpleRecyclerAdapter adapter;
    LinearLayoutManager lm;
    List<MusicTrackPOJO> list;
    int tracksSource = TracksLoaderInterface.MY_TRACKS;
    boolean scrollDownLock;
    int position = 0;

    public TabFragment(){
        System.out.print("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dummy_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new SimpleRecyclerAdapter(list,(MainActivity) getActivity());
        adapter.setCurrentTrackPosition(position);
        adapter.setCurrentSource(tracksSource);
        recyclerView.setAdapter(adapter);
        lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (((MainActivity) getActivity()).getPlaylistByName(tracksSource).size() != 0) {
                    if (lm.findLastVisibleItemPosition() > ((MainActivity) getActivity()).getPlaylistByName(tracksSource).size() - 2) {
                        if (!scrollDownLock)
                            ((MainActivity) getActivity()).uploadMore(tracksSource);
                        scrollDownLock = true;
                    } else {
                        scrollDownLock = false;
                    }
                }
            }
        });

        return view;
    }

    public void setupWith(List<MusicTrackPOJO> list, int source){
        this.list = list;
        this.tracksSource = source;
    }

    public void updateList(){
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void nextTrack(){
        adapter.nextTrack();
    }

    public void previousTrack(){
        adapter.previousTrack();
    }

    public void setCurrentTrackPosition(int position){
        this.position = position;
        if (adapter != null) {
            adapter.setCurrentTrackPosition(position);
        }
    }

}
