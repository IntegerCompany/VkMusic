package com.company.integer.vkmusic.logic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.company.integer.vkmusic.LoginActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class TracksDataLoader implements TracksLoaderInterface {
    private static final String LOG_TAG = "TracksDataLoader";

    TracksLoaderListener tracksLoaderListener;
    Gson gson;
    String lastSearchQuery = "";
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    Context context;
    int notificationID = 1;

    public TracksDataLoader(Context context) {
        this.context = context;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public String getLastSearchQuery() {
        return lastSearchQuery;
    }

    @Override
    public void search(String query, int from, int count) {
        lastSearchQuery = query;
        VKRequest requestAudio = new VKRequest("audio.search", VKParameters.from(VKApiConst.Q, query, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.SEARCH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(LOG_TAG, error.errorMessage);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }

    @Override
    public void getTracksByUserId(String userId, int from, int count) {
        VKRequest requestAudio = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, userId, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.MY_TRACKS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(LOG_TAG, error.errorMessage);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }

    @Override
    public void getRecommendationsByUserID(String userId, int from, int count) {
        VKRequest requestAudio = new VKRequest("audio.getRecommendations", VKParameters.from(VKApiConst.OWNER_ID, userId, VKApiConst.OFFSET, from, VKApiConst.COUNT, count));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ArrayList<MusicTrackPOJO> musicTracks;
                    Type musicTracksType = new TypeToken<ArrayList<MusicTrackPOJO>>() {
                    }.getType();
                    musicTracks = gson.fromJson(response.json.getJSONObject("response").getJSONArray("items").toString(), musicTracksType);
                    tracksLoaderListener.tracksLoaded(musicTracks, TracksLoaderListener.RECOMMENDATIONS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.e(LOG_TAG, error.errorMessage);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }

    @Override
    public void setTracksLoadingListener(TracksLoaderListener tracksLoaderListener) {
        this.tracksLoaderListener = tracksLoaderListener;
    }

    @Override
    public void uploadMore(int source) {
        //Unused
    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        //Unused
        return null;
    }

    @Override
    public void downloadTrack(final MusicTrackPOJO trackToDownload) {
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Downloading track")
                .setContentText(trackToDownload.getTitle())
                .setSmallIcon(R.mipmap.ic_launcher);
        Intent resultIntent = new Intent(context, LoginActivity.class);
        resultIntent.putExtra("tab", 3);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int count;
                        try {
                            File vkMusicDirectory = new File(Environment
                                    .getExternalStorageDirectory().toString()
                                    + "/VkMusic/");

                            vkMusicDirectory.mkdir();
                            File path = new File(vkMusicDirectory + "/" + trackToDownload.getArtist() + "-" + trackToDownload.getTitle() + ".mp3");
                            if (path.exists()) {
                                tracksLoaderListener.tracksLoadingError("File already exists");
                                return;
                            }
                            int id = getNewID();
                            mNotifyManager.notify(id, mBuilder.build());
                            URL url = new URL(trackToDownload.getUrl());
                            URLConnection connection = url.openConnection();
                            connection.connect();
                            // this will be useful so that you can show a typical 0-100%
                            // progress bar
                            int lengthOfFile = connection.getContentLength();
                            // download the file
                            InputStream input = new BufferedInputStream(url.openStream(),
                                    8192);
                            // Output stream
                            OutputStream output = new FileOutputStream(path);
                            byte data[] = new byte[1024];
                            long total = 0;
                            int lastPercentage = 0;
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                // writing data to file
                                output.write(data, 0, count);
                                // publishing the progress....
                                if (lastPercentage != (int) (total * 100) / lengthOfFile) {
                                    mBuilder.setContentText(trackToDownload.getArtist() + " - " + trackToDownload.getTitle());
                                    mBuilder.setProgress(100, (int) ((total * 100) / lengthOfFile), false);
                                    mNotifyManager.notify(id, mBuilder.build());
                                    tracksLoaderListener.trackDownloadingProgress(trackToDownload, (int) ((total * 100) / lengthOfFile));
                                    lastPercentage = (int) ((total * 100) / lengthOfFile);
                                }

                            }
                            if (total == lengthOfFile) {
                                tracksLoaderListener.trackDownloadFinished(trackToDownload);
                                // When the loop is finished, updates the notification
                                mBuilder.setContentTitle("Download complete");
                                mBuilder.setContentText(trackToDownload.getArtist() + " - " + trackToDownload.getTitle())
                                        // Removes the progress bar
                                        .setProgress(0, 0, false);
                                mNotifyManager.notify(id, mBuilder.build());
                            }
                        } catch (IOException e) {
                            tracksLoaderListener.tracksLoadingError(e.getMessage());
                        }


                    }
                }
        ).start();
    }

    private synchronized int getNewID() {
        return notificationID++;
    }



}
