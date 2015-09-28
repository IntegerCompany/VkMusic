package com.company.integer.vkmusic.adapters;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.MusicPlayerInterface;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;

import java.io.IOException;
import java.util.List;


public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.TrackViewHolder> {
    List<MusicTrackPOJO> tracks;
    MusicPlayerInterface musicPlayer;
    Context ctx;
    ImageView lastTrackPlayPauseButton;

    public SimpleRecyclerAdapter(List<MusicTrackPOJO> tracks, Context ctx) {
        this.tracks = tracks;
        this.ctx = ctx;
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
        if (1 == i) {
            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pause_item));
        } else {
            trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.play_item));
        }
        trackViewHolder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (trackViewHolder.playPause.getDrawable().getConstantState() == ContextCompat.getDrawable(ctx, R.mipmap.play_item).getConstantState()) {
                    try {
                        musicPlayer.setCurrentTrackPosition(i);
                        musicPlayer.play();
                        trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.pause_item));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    musicPlayer.pause();
                    trackViewHolder.playPause.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.play_item));
                }
                if (lastTrackPlayPauseButton != null & !trackViewHolder.playPause.equals(lastTrackPlayPauseButton)) {
                    lastTrackPlayPauseButton.setImageDrawable(ContextCompat.getDrawable(ctx, R.mipmap.play_item));
                }
                lastTrackPlayPauseButton = trackViewHolder.playPause;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks == null ? 0 : tracks.size();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
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

    private String getDurationString(int durationInSec) {
        int minutes = durationInSec / 60;
        int seconds = durationInSec - minutes * 60;
        if (seconds < 10) {
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

}