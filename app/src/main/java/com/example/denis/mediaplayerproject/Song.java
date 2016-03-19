package com.example.denis.mediaplayerproject;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String artist ;
    private String  duration ;
    private String album ;
    private Bitmap bitmap;
    private String path;


    public Song(){}

    public Song(String title, String artist, String duration, String album, Bitmap bitmap,
                String path){
        this.album = album;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.bitmap = bitmap;
        this.path = path;
    }

    public Song(Parcel source) {
        album = source.readString();
        title = source.readString();
        artist = source.readString();
        duration = source.readString();
        bitmap = (Bitmap) source.readValue(Bitmap.class.getClassLoader());
        path = source.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String  getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return  title + " "  + artist+ " " + duration + " " + album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(album);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(duration);
        dest.writeValue(bitmap);
        dest.writeString(path);
    }
}
