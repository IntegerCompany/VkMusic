package com.company.integer.vkmusic.adapters;


import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.TrackViewHolder> {
    List<MusicTrackPOJO> tracks;
    MainActivity activity;
    int currentTrackPosition = 0;
    int adapterSource = TracksLoaderInterface.MY_TRACKS;
    InterstitialAd interstitial;

    public SimpleRecyclerAdapter(List<MusicTrackPOJO> tracks, MainActivity activity) {
        this.tracks = tracks;
        this.activity = activity;
        interstitial = new InterstitialAd(activity);
        interstitial.setAdUnitId("ca-app-pub-7672991449155931/5396554003");

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrackViewHolder trackViewHolder, final int i) {
            trackViewHolder.author.setText(getDurationString(tracks.get(i).getDuration()) + " | " +tracks.get(i).getArtist());
            trackViewHolder.title.setText(tracks.get(i).getTitle());
            trackViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentTrackPosition == i & activity.isPlaying() & adapterSource == activity.getCurrentPlaylist()) {
                        activity.pauseMusic();
                    } else {
                        activity.setCurrentPlaylist(adapterSource);
                        activity.setPlayingTrack(i);
                        activity.playMusic();
                    }
                    currentTrackPosition = i;
                    notifyDataSetChanged();

                }
            });
            if (currentTrackPosition == i & adapterSource == activity.getCurrentPlaylist()) {
                if (activity.isPlaying()) {
                    trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.pause_item));
                }
                trackViewHolder.itemView.setBackgroundColor(AppState.getColors().getColorAccentID());
            } else {
                trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.play_item));
                trackViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.listViewItemBackground));
            }
//        if (!Environment.getExternalStorageDirectory().canWrite()) {
//            ActivityCompat.requestPermissions(activity,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    0);
//        }
            File vkMusicDirectory = new File(Environment
                    .getExternalStorageDirectory().toString()
                    + AppState.FOLDER);
            final File path = new File(vkMusicDirectory + "/" + tracks.get(i).getArtist() + "-" + tracks.get(i).getTitle() + ".mp3");


            if (!path.exists()) {
                trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.download));
            } else {
                trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));
            }
        if (adapterSource == TracksLoaderInterface.SAVED) {
            trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.remove));
            trackViewHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Adapter", "Deleting" + path.getName() + " Title: " + tracks.get(i).getTitle() + " Artist " + tracks.get(i).getArtist());
                    Log.d("Adapter", "Path exists " + path.exists() + path.getAbsolutePath());
                    if (path.delete()) Toast.makeText(activity, "File succesfully deleted", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(activity, "Error! File can't be deleted, maybe file already deleted", Toast.LENGTH_SHORT).show();

                    activity.getSavedTracks();
                }
            });
        }else{
            trackViewHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppState.adclick++;
                    if (AppState.adclick >= 3) {
                        AppState.adclick = 0;
                        showAd(tracks.get(i), trackViewHolder);
                    }
                    activity.downloadTrack(tracks.get(i));
                    trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));

                }
            });
        }
        if (adapterSource == TracksLoaderInterface.RECOMMENDATIONS | adapterSource == TracksLoaderInterface.SEARCH){
            trackViewHolder.addToVkPlaylist.setVisibility(View.VISIBLE);
            for (int id : AppState.getSavedIds()){
                if (Integer.parseInt(tracks.get(i).getId()) == id){
                    trackViewHolder.addToVkPlaylist.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));
                    trackViewHolder.addToVkPlaylist.setClickable(false);
                    break;
                }else {
                    trackViewHolder.addToVkPlaylist.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.add));
                    trackViewHolder.addToVkPlaylist.setClickable(true);
                }
            }
        }else{
            trackViewHolder.addToVkPlaylist.setVisibility(View.GONE);
        }





        trackViewHolder.addToVkPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.addTrackToVkPlaylist(tracks.get(i));
                AppState.saveTrackId(Integer.parseInt(tracks.get(i).getId()));
                trackViewHolder.addToVkPlaylist.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));
                trackViewHolder.addToVkPlaylist.setClickable(false);
            }
        });



    }

    private void showAd(final MusicTrackPOJO track, final TrackViewHolder trackViewHolder) {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }


    }


    @Override
    public int getItemCount() {
        return tracks == null ? 0 : tracks.size();
    }

    public void setCurrentTrackPosition(int currentTrackPosition){

        this.currentTrackPosition = currentTrackPosition;
        Log.d("debug", "currentTrackPosition " + currentTrackPosition);
        notifyDataSetChanged();

    }


    public void nextTrack(){
        currentTrackPosition++;
        notifyDataSetChanged();
    }

    public void previousTrack(){
        currentTrackPosition++;
        notifyDataSetChanged();
    }

    private String getDurationString(int durationInSec) {
        int minutes = durationInSec / 60;
        int seconds = durationInSec - minutes * 60;
        if (seconds < 10) {
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

    public void updateTracks(ArrayList<MusicTrackPOJO> tracks){
        this.tracks = tracks;
        if (adapterSource == TracksLoaderInterface.SAVED) sortTracksByAddingTime();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        ImageView playPause;
        ImageView downloadImage;
        ImageView addToVkPlaylist;

        public TrackViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.tv_author_name_item);
            addToVkPlaylist = (ImageView) itemView.findViewById(R.id.btn_add_item);
            title = (TextView) itemView.findViewById(R.id.tv_song_name_item);
            playPause = (ImageView) itemView.findViewById(R.id.btn_play_pause_item);
            downloadImage = (ImageView) itemView.findViewById(R.id.btn_download_item);
        }
    }


    public void setAdapterSource(int adapterSource) {
        this.adapterSource = adapterSource;
        if (adapterSource == TracksLoaderInterface.SAVED) sortTracksByAddingTime();
    }

    public void sortTracksByAddingTime(){
        Collections.sort(tracks, new Comparator<MusicTrackPOJO>() {
            @Override
            public int compare(MusicTrackPOJO lhs, MusicTrackPOJO rhs) {
                if (lhs.getFileCreatingTime() < rhs.getFileCreatingTime()) return 1;
                else if (lhs.getFileCreatingTime() == rhs.getFileCreatingTime()) return 0;
                else return -1;
            }
        });
    }

    public void checkCurrentTrack(){
        if (activity.getCurrentMusicTrack() != null) {
            if (!tracks.get(currentTrackPosition).getPath().equals(activity.getCurrentMusicTrack().getPath())) {
                if (tracks.get(currentTrackPosition + 1).getPath().equals(activity.getCurrentMusicTrack().getPath())) {
                    setCurrentTrackPosition(currentTrackPosition + 1);
                }
            }
        }
    }









}