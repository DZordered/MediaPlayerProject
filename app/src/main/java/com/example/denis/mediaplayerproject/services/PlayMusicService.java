package com.example.denis.mediaplayerproject.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.denis.mediaplayerproject.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayMusicService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        ,MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String path;
    private int currentPosition;
    private ArrayList<Song> songs;
    private final IBinder mBinder = new PlayMusicServiceBinder();

    public PlayMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getExtras().getString("path");
        songs = intent.getParcelableArrayListExtra("songs");
        currentPosition = intent.getExtras().getInt("currentPosition");

        startPlay(path);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public void startPlay(String path){
        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
        if(mediaPlayer == null)
            Toast.makeText(this, "Create failed", Toast.LENGTH_SHORT).show();

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer arg) {
                next();
            }
        });
    }

    public void pause(){
        mediaPlayer.pause();
    }
    public void next(){
        String nextSong = " ";
        if(songs.get(currentPosition++) != null){
            nextSong = songs.get(currentPosition++).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();
        mediaPlayer.reset();
        try {
            if(nextSong != null)
                mediaPlayer.setDataSource(nextSong);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void prev(){
        String prevSong = " ";
        if(songs.get(currentPosition--) != null){
            prevSong = songs.get(currentPosition--).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();

        mediaPlayer.reset();
        try {
            if(prevSong != null) mediaPlayer.setDataSource(prevSong);

            mediaPlayer.prepare();
            mediaPlayer.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        mediaPlayer.start();
    }
    public void stop(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }



   public class PlayMusicServiceBinder extends Binder{
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }


}
