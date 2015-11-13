package com.company.integer.vkmusic.notificationPanel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.company.integer.vkmusic.LoginActivity;
import com.company.integer.vkmusic.MainActivity;
import com.company.integer.vkmusic.R;

public class NotificationPanel {

    private Context parent;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private RemoteViews remoteViewPlay;
    private RemoteViews remoteViewPause;
    private RemoteViews imgCloseNotification;

    private boolean isPlaying = false;

    public NotificationPanel(Context parent) {

        this.parent = parent;
        nBuilder = new NotificationCompat.Builder(parent)
                .setContentTitle("Parking Meter")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                ;
        nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViewPlay = new RemoteViews(parent.getPackageName(), R.layout.notificationview);
        remoteViewPause = new RemoteViews(parent.getPackageName(), R.layout.notificationview_pause);

        //set the button listeners
        setListeners(remoteViewPlay);
        nBuilder.setContent(remoteViewPlay);
    }

    public Notification getNotification(boolean play) {
        if (play) {
            nBuilder.setContent(remoteViewPause);
            setListeners(remoteViewPause);
            return nBuilder.build();
        } else {
            nBuilder.setContent(remoteViewPlay);
            setListeners(remoteViewPlay);
            return nBuilder.build();
        }


    }

    public void setListeners(RemoteViews view) {
        Intent playIntent = new Intent("com.example.app.ACTION_PLAY");
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(parent, 100, playIntent, 0);
        view.setOnClickPendingIntent(R.id.play, pendingPlayIntent);

        Intent pauseIntent = new Intent("com.example.app.ACTION_PAUSE");
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(parent, 100, pauseIntent, 0);
        view.setOnClickPendingIntent(R.id.pause, pendingPauseIntent);

        Intent backIntent = new Intent("com.example.app.ACTION_BACK");
        PendingIntent pendingBackIntent = PendingIntent.getBroadcast(parent, 100, backIntent, 0);
        view.setOnClickPendingIntent(R.id.previous, pendingBackIntent);

        Intent nextIntent = new Intent("com.example.app.ACTION_NEXT");
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(parent, 100, nextIntent, 0);
        view.setOnClickPendingIntent(R.id.next, pendingNextIntent);

        Intent openActivity = new Intent(parent, MainActivity.class);
        openActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingOpenActivityIntent = PendingIntent.getActivity(parent, 0, openActivity, 0);
        view.setOnClickPendingIntent(R.id.song_image, pendingOpenActivityIntent);

        Intent closeNotification = new Intent("com.example.app.ACTION_CLOSE_NOTIFICATION");
        PendingIntent pendingCloseNotification = PendingIntent.getBroadcast(parent, 100, closeNotification, 0);
        view.setOnClickPendingIntent(R.id.imgCloseNotification, pendingCloseNotification);

    }

    public void updateToPlay(boolean play) {
        if (!play) {
            Notification mNotify = getNotification(false);
            mNotify.flags |= Notification.FLAG_ONGOING_EVENT;
            nManager.notify(1337, mNotify);
        } else {
            Notification mNotify = getNotification(true);
            mNotify.flags |= Notification.FLAG_ONGOING_EVENT;
            nManager.notify(1337, mNotify);
        }
        isPlaying = play;
    }

    public void notificationCancel() {
        Log.d("Panel","notification cancel");
        nManager.cancel(2);
        nManager = null;
    }

}
