package com.company.integer.vkmusic.adapters;


import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.vk.sdk.VKSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.TrackViewHolder> {
    List<MusicTrackPOJO> tracks;
    MainActivity activity;
    int currentTrackPosition = 0;
    int currentSource = TracksLoaderInterface.MY_TRACKS;

    public SimpleRecyclerAdapter(List<MusicTrackPOJO> tracks, MainActivity activity) {
        this.tracks = tracks;
        this.activity = activity;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TrackViewHolder trackViewHolder, final int i) {
        trackViewHolder.author.setText(tracks.get(i).getArtist());

        trackViewHolder.title.setText(tracks.get(i).getTitle());
        trackViewHolder.duration.setText(getDurationString(tracks.get(i).getDuration()));
        trackViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTrackPosition == i & activity.isPlaying()){
                    activity.pauseMusic();
                }else {
                    activity.setCurrentPlaylist(activity.getCurrentPlaylist());
                    activity.setPlayingTrack(i);
                    activity.playMusic();
                }
                currentTrackPosition = i;
                notifyDataSetChanged();
            }
        });
        if (currentTrackPosition == i){
            if (activity.isPlaying()) {
                trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.pause_item));
            }
            trackViewHolder.itemView.setBackgroundColor(AppState.getColors().getColorAccentID());
        }else{
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
        File path = new File(vkMusicDirectory + "/" + tracks.get(i).getArtist() + "-" + tracks.get(i).getTitle() + ".mp3");

        if (!path.exists()) {
            trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.download));
        }else{
            trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));
        }
        trackViewHolder.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.downloadTrack(tracks.get(i));
                trackViewHolder.downloadImage.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.ok));
                
            }
        });

    }

    @Override
    public int getItemCount() {
        return tracks == null ? 0 : tracks.size();
    }

    public void setCurrentTrackPosition(int currentTrackPosition){
        this.currentTrackPosition = currentTrackPosition;
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
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView duration;
        ImageView playPause;
        ImageView downloadImage;

        public TrackViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.tv_author_name_item);
            title = (TextView) itemView.findViewById(R.id.tv_song_name_item);
            duration = (TextView) itemView.findViewById(R.id.tv_duration_item);
            playPause = (ImageView) itemView.findViewById(R.id.btn_play_pause_item);
            downloadImage = (ImageView) itemView.findViewById(R.id.btn_download_item);
        }
    }


    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }
}