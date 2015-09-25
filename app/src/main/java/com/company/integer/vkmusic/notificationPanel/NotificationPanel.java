package com.company.integer.vkmusic.notificationPanel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.company.integer.vkmusic.R;

/**
 * Created by maxvitruk on 25.09.15.
 */
public class NotificationPanel {

    private Context parent;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private RemoteViews remoteView;

    private boolean isPlaing = true;

    public NotificationPanel(Context parent) {
        // TODO Auto-generated constructor stub
        this.parent = parent;
        nBuilder = new NotificationCompat.Builder(parent)
                .setContentTitle("Parking Meter")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false);

        remoteView = new RemoteViews(parent.getPackageName(), R.layout.notificationview);

        //set the button listeners
        setListeners(remoteView);
        nBuilder.setContent(remoteView);
    }

    public Notification getNotification(){
        return nBuilder.build();
    }

    public void updateListeners(){
        Intent playIntent = new Intent("com.example.app.ACTION_PLAY");
        playIntent.putExtra("play",isPlaing);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(parent, 100, playIntent, 0);
        remoteView.setOnClickPendingIntent(R.id.play, pendingPlayIntent);
    }

    public void setListeners(RemoteViews view){
        //listener 1
        Intent playIntent = new Intent("com.example.app.ACTION_PLAY");
        playIntent.putExtra("play",isPlaing);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(parent, 100, playIntent, 0);
        view.setOnClickPendingIntent(R.id.play, pendingPlayIntent);

        //listener 2
        Intent pauseIntent = new Intent("com.example.app.ACTION_BACK");
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(parent, 100, pauseIntent, 0);
        view.setOnClickPendingIntent(R.id.previous, pendingPauseIntent);

        Intent closeIntent = new Intent("com.example.app.ACTION_NEXT");
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(parent, 100, closeIntent, 0);
        view.setOnClickPendingIntent(R.id.next, pendingCloseIntent);

    }

    public void updateToPlay(boolean play){
        int imageID;
        if (play) {
            imageID = R.mipmap.play_item;
            isPlaing = true;
        }else {
            imageID = R.mipmap.pause_item;
            isPlaing = false;
        }
        remoteView.setImageViewResource(R.id.play, imageID);
        updateListeners();
    }

    public void notificationCancel() {
        nManager.cancel(2);
        nManager = null;
    }

}
