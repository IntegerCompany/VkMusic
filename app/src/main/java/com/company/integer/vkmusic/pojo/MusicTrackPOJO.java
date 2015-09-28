package com.company.integer.vkmusic.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicTrackPOJO implements Parcelable {

    private String id;
    private String ownerId;
    private String artist;
    private String title;
    private String url;
    private int duration;
    private int genreId;
    private long date;
    private long lyricsId;

    public MusicTrackPOJO(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        artist = in.readString();
        title = in.readString();
        url = in.readString();
        duration = in.readInt();
        genreId = in.readInt();
        date = in.readLong();
        lyricsId = in.readLong();
    }

    public MusicTrackPOJO(){

    }

    public static final Creator<MusicTrackPOJO> CREATOR = new Creator<MusicTrackPOJO>() {
        @Override
        public MusicTrackPOJO createFromParcel(Parcel in) {
            return new MusicTrackPOJO(in);
        }

        @Override
        public MusicTrackPOJO[] newArray(int size) {
            return new MusicTrackPOJO[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getLyricsId() {
        return lyricsId;
    }

    public void setLyricsId(long lyricsId) {
        this.lyricsId = lyricsId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeInt(duration);
        dest.writeInt(genreId);
        dest.writeLong(date);
        dest.writeLong(lyricsId);
    }
}
