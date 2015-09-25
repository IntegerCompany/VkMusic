package com.company.integer.vkmusic.notificationPanel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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

    public void buildNotification(){

        if (nManager == null) {
            nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(2, nBuilder.build());
        }
    }
    public Notification getNotification(){
        return nBuilder.build();
    }

    public void setListeners(RemoteViews view){
        //listener 1
        Intent playIntent = new Intent("com.example.app.ACTION_PLAY");
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(parent, 100, playIntent, 0);
        view.setOnClickPendingIntent(R.id.play, pendingPlayIntent);

        //listener 2
        Intent pauseIntent = new Intent("com.example.app.ACTION_PAUSE");
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(parent, 100, pauseIntent, 0);
        view.setOnClickPendingIntent(R.id.pause, pendingPauseIntent);

        Intent closeIntent = new Intent("com.example.app.ACTION_CLOSE");
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(parent, 100, closeIntent, 0);
        view.setOnClickPendingIntent(R.id.close, pendingCloseIntent);

    }

    public void updateToPlay(boolean play){
        int playColor;
        int pauseColor;
        if (play) {
            playColor = R.color.accentColor;
            pauseColor = R.color.vk_white;
        }else {
            playColor = R.color.vk_white;
            pauseColor = R.color.accentColor;
        }
        remoteView.setTextColor(R.id.play, ContextCompat.getColor(parent, playColor));
        remoteView.setTextColor(R.id.pause, ContextCompat.getColor(parent, pauseColor));
    }

    public void notificationCancel() {
        nManager.cancel(2);
        nManager = null;
    }

}
