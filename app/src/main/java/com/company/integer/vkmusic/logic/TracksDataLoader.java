package com.company.integer.vkmusic.logic;

import android.annotation.TargetApi;
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
                    JSONObject jsonObject = new JSONObject(response.responseString);
                    jsonObject.getJSONArray("response").remove(0);
                    musicTracks = gson.fromJson(jsonObject.getJSONArray("response").toString(), musicTracksType);
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
                    JSONObject jsonObject = new JSONObject(response.responseString);
                    jsonObject.getJSONArray("response").remove(0);
                    musicTracks = gson.fromJson(jsonObject.getJSONArray("response").toString(), musicTracksType);
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

    private ArrayList<MusicTrackPOJO> removeObjectAtZeroPosition(Type musicTracksType, JSONArray response) {
        ArrayList<Object> list = gson.fromJson(response.toString(), musicTracksType);
        list.remove(0);
        ArrayList<MusicTrackPOJO> tracks = new ArrayList<>();
        for (Object object : list){
            tracks.add((MusicTrackPOJO) object);
        }
        return tracks;
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
                    JSONObject jsonObject = new JSONObject(response.responseString);
                    jsonObject.getJSONArray("response").remove(0);
                    musicTracks = gson.fromJson(jsonObject.getJSONArray("response").toString(), musicTracksType);
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
    public void getSavedTracks() {
        if (!Environment.getExternalStorageDirectory().canWrite()) return;
        ArrayList<MusicTrackPOJO> savedTracks = new ArrayList<>();

        File vkMusicDirectory = new File(Environment
                .getExternalStorageDirectory().toString()
                + AppState.FOLDER);
        FileFilter mp3Filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isDirectory() && getFileExtension(pathname).equals("mp3")) return true;
                return false;
            }
        };
        if (!vkMusicDirectory.exists() && !vkMusicDirectory.isDirectory() || vkMusicDirectory.listFiles(mp3Filter) == null ) {
            vkMusicDirectory.mkdirs();

        }


        for (File trackFile : vkMusicDirectory.listFiles(mp3Filter)){
            MusicTrackPOJO savedTrack = new MusicTrackPOJO();
            savedTrack.setIsFromFile(true);
            savedTrack.setFileCreatingTime(trackFile.lastModified());
            savedTrack.setUrl(trackFile.getPath());
            initFromMetadata(savedTrack);
            savedTracks.add(savedTrack);
        }

        tracksLoaderListener.tracksLoaded(savedTracks, SAVED);



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
                                saveTrackMetadata(trackToDownload, path);
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

    private void initFromMetadata(MusicTrackPOJO track) {

        Mp3File song = null;
        try {
            song = new Mp3File(track.getPath());
            if (song.hasId3v2Tag()) {
                ID3v2 tag = song.getId3v2Tag();
                track.setTitle(tag.getTitle());
                track.setArtist(tag.getArtist());
                track.setDuration((int) song.getLengthInMilliseconds() / 1000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }



    private void saveTrackMetadata(MusicTrackPOJO track, File file)  {

        Mp3File song = null;
        RandomAccessFile var2 = null;
        try {
            song = new Mp3File(file.getPath());
            song.removeId3v2Tag();
            ID3v2 id3v2tag = new ID3v24Tag();
            id3v2tag.setTitle(track.getTitle());
            id3v2tag.setArtist(track.getArtist());
            song.setId3v2Tag(id3v2tag);
            var2 = new RandomAccessFile(file, "rw");
            var2.write(song.getId3v2Tag().toBytes());



    } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (NotSupportedException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (var2 != null) {
                    var2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
