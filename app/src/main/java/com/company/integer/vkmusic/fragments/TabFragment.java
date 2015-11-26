package com.company.integer.vkmusic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    boolean loading = false, error = false;

    LinearLayout errorContainer;
    Button btnTryAgain;
    ProgressBar pbReconnect;

    public TabFragment(){
        System.out.print("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dummy_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
        btnTryAgain = (Button) view.findViewById(R.id.btn_try_again);
        errorContainer = (LinearLayout) view.findViewById(R.id.error_container);
        pbReconnect = (ProgressBar) view.findViewById(R.id.pb_reconnect);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new SimpleRecyclerAdapter(list,(MainActivity) getActivity());
        adapter.setCurrentTrackPosition(position);
        adapter.setAdapterSource(tracksSource);
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
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        ((MainActivity) getActivity()).attemptToGetTracks();
                    }
            });
        showError(error);
        showLoading(loading);

        return view;
    }

    public void setupWith(List<MusicTrackPOJO> list, int source){
        this.list = list;
        this.tracksSource = source;
    }

    public void updateList(){
        if (adapter != null) {
            adapter.notifyDataSetChanged();

            if (tracksSource == TracksLoaderInterface.SAVED) {
                adapter.sortTracksByAddingTime();
                adapter.checkCurrentTrack();
            }
        }

    }

    public void nextTrack(){
        adapter.nextTrack();
    }

    public void previousTrack(){
        adapter.previousTrack();
    }

    public void setCurrentTrackPosition(int position){
        this.position = position;
        Log.d("debug", "setCurrentTrackPosition " + position);
        if (adapter != null) {
            Log.d("debug", "adapter not null, setting " + position);
            adapter.setCurrentTrackPosition(position);
        }
    }

    public void showError(boolean showError){
        error = showError;
        if (errorContainer == null) return;
        if (showError){
                errorContainer.setVisibility(View.VISIBLE);
                showLoading(false);
            }else{
                errorContainer.setVisibility(View.GONE);
            }

        }

        public void showLoading(boolean showLoading){
        loading = showLoading;
        if (pbReconnect == null) return;
        if (showLoading){
                pbReconnect.setVisibility(View.VISIBLE);
            }else{
                pbReconnect.setVisibility(View.GONE);
            }
    }


}
