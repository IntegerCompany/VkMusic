package com.company.integer.vkmusic.logic;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.company.integer.vkmusic.LoginActivity;
import com.company.integer.vkmusic.R;
import com.company.integer.vkmusic.interfaces.TracksLoaderInterface;
import com.company.integer.vkmusic.interfaces.TracksLoaderListener;
import com.company.integer.vkmusic.pojo.MusicTrackPOJO;
import com.company.integer.vkmusic.supportclasses.AppState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        if (lastSearchQuery.equals("")) lastSearchQuery = context.getSharedPreferences("save", Context.MODE_PRIVATE).getString("searchQuery", "");
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
                if (error != null) {
                    Log.e(LOG_TAG, "" + error.errorMessage);
                    tracksLoaderListener.tracksLoadingError(error.errorMessage);
                }

            }
        });
    }

    @Override
    public void getSavedTracks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Environment.getExternalStorageDirectory().canWrite()) return;
                ArrayList<MusicTrackPOJO> savedTracks = new ArrayList<MusicTrackPOJO>();

                File vkMusicDirectory = new File(Environment
                        .getExternalStorageDirectory().toString()
                        + AppState.FOLDER);

                if (!vkMusicDirectory.exists() && !vkMusicDirectory.isDirectory()) {
                    vkMusicDirectory.mkdirs();

                }

                ArrayList<MusicTrackPOJO> tracksFromSharedPreferences = initTrackData();
                    for (MusicTrackPOJO track : tracksFromSharedPreferences) {
                        if (new File(track.getPath()).exists()) savedTracks.add(track);
                    }



                tracksLoaderListener.tracksLoaded(savedTracks, SAVED);
            }
        }).start();




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
    public void getTrackLyrics(String id) {
        if (id.equals("0")) {
            tracksLoaderListener.tracksLoadingError("Track have no lyrics");
            return;
        }
        VKRequest requestAudio = new VKRequest("audio.getLyrics", VKParameters.from("lyrics_id", id));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    tracksLoaderListener.trackLyricsReceived(response.json.getJSONObject("response").getString("text"));
                } catch (JSONException e) {
                    tracksLoaderListener.tracksLoadingError(e.getMessage());
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }

    @Override
    public ArrayList<MusicTrackPOJO> getTracksFromSource(int source) {
        //Unused
        return null;
    }

    @TargetApi(16)
    @Override
    public void downloadTrack(final MusicTrackPOJO trackToDownload) {
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getString(R.string.downloading_notification))
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
                                    + AppState.FOLDER);

                            vkMusicDirectory.mkdir();

                            File path = new File(vkMusicDirectory + "/" + trackToDownload.getArtist() + "-" + trackToDownload.getTitle() + ".mp3");

                            if (path.exists()) {
                                tracksLoaderListener.tracksLoadingError(context.getString(R.string.file_exists));
                                return;
                            }
                            int id = getNewID();
                            mNotifyManager.notify(id, mBuilder.build());
                            URL url = new URL(trackToDownload.getPath());
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
                                saveTrack(trackToDownload, path);
                                tracksLoaderListener.trackDownloadFinished(trackToDownload);
                                // When the loop is finished, updates the notification
                                mBuilder.setContentTitle(context.getString(R.string.download_complete_notification));
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



    @Override
    public void addTrackToVkPlaylist(final MusicTrackPOJO track) {
       // AppState.getLoggedUser().getUserId();
        VKRequest requestAudio = new VKRequest("audio.add", VKParameters.from(VKApiConst.OWNER_ID, track.getOwnerId(), "audio_id", track.getId()));
        requestAudio.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                tracksLoaderListener.tracksLoadingError(error.errorMessage);
            }
        });
    }



    private synchronized int getNewID() {
        return notificationID++;
    }

    private String getFileExtension(File file) {
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            return file.getName().substring(i+1);
        }
        return "error";
    }

    private ArrayList<MusicTrackPOJO> initTrackData() {


        SharedPreferences sharedPreferences = context.getSharedPreferences("tracksNames", Context.MODE_PRIVATE);
        HashMap<String, MusicTrackPOJO> tracks;
        Type musicTracksType = new TypeToken<HashMap<String, MusicTrackPOJO>>() {
        }.getType();
        tracks = gson.fromJson(sharedPreferences.getString("tracks", ""), musicTracksType);
        if (tracks == null) return new ArrayList<>();
        return new ArrayList<>(tracks.values());

    }



    private void saveTrack(MusicTrackPOJO track, File file)  {

        SharedPreferences sharedPreferences = context.getSharedPreferences("tracksNames", Context.MODE_PRIVATE);
        HashMap<String, MusicTrackPOJO> tracks;
        Type musicTracksType = new TypeToken<HashMap<String, MusicTrackPOJO>>() {
        }.getType();
        track.setIsFromFile(true);
        track.setFileCreatingTime(file.lastModified());
        track.setUrl(file.getPath());
        tracks = gson.fromJson(sharedPreferences.getString("tracks", ""), musicTracksType);
        if (tracks == null) tracks = new HashMap<>();
        tracks.put(track.getArtist() + "-" + track.getTitle() + ".mp3", track);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tracks", gson.toJson(tracks));
        editor.apply();

    }

}
