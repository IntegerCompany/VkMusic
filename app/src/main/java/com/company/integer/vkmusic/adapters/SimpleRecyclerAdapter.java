package com.company.integer.vkmusic.adapters;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.util.List;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.TrackViewHolder> {
    List<MusicTrackPOJO> tracks;
    MainActivity activity;
    int currentTrackPosition = 0;
    ImageView lastTrackPlayPauseButton;


    public SimpleRecyclerAdapter(List<MusicTrackPOJO> tracks, MainActivity activity) {
        this.tracks = tracks;
        this.activity = activity;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        TrackViewHolder viewHolder = new TrackViewHolder(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final TrackViewHolder trackViewHolder, final int i) {
        trackViewHolder.author.setText(tracks.get(i).getArtist());
        trackViewHolder.title.setText(tracks.get(i).getTitle());
        trackViewHolder.duration.setText(getDurationString(tracks.get(i).getDuration()));
        trackViewHolder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentTrackPosition == i & activity.isPlaying()){
                    activity.pauseMusic();
                }else {
                    activity.setPlayingTrack(i);
                    activity.playMusic();
                }
                currentTrackPosition = i;
                notifyDataSetChanged();
            }
        });
        if (currentTrackPosition == i & activity.isPlaying()){
            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.pause_item));
        }else{
            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.play_item));
        }
//        if (trackViewHolder.playPause.getDrawable().getConstantState() == ContextCompat.getDrawable(activity, R.mipmap.play_item).getConstantState()) {
//            activity.setPlayingTrack(i);
//            activity.playMusic();
//            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.pause_item));
//
//        } else {
//            activity.pauseMusic();
//            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.play_item));
//        }
//        if (lastTrackPlayPauseButton != null & !trackViewHolder.playPause.equals(lastTrackPlayPauseButton)){
//            lastTrackPlayPauseButton.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.play_item));
//        }
//        lastTrackPlayPauseButton = trackViewHolder.playPause;

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


    class TrackViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        TextView duration;
        ImageView playPause;

        public TrackViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.tv_author_name_item);
            title = (TextView) itemView.findViewById(R.id.tv_song_name_item);
            duration = (TextView) itemView.findViewById(R.id.tv_duration_item);
            playPause = (ImageView) itemView.findViewById(R.id.btn_play_pause_item);
        }
    }

    private String getDurationString(int durationInSec){
        int minutes = durationInSec/60;
        int seconds = durationInSec-minutes*60;
        if(seconds<10){
            return minutes + ":0" + seconds;
        }else{
            return minutes + ":" + seconds;
        }
    }

}