package com.example.denis.mediaplayerproject.sevices;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class BackgroundPlayService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        ,MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String path;
    public BackgroundPlayService() {
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
        mediaPlayer.reset();
        if(!mediaPlayer.isPlaying()){
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void play(){
        if(!mediaPlayer.isPlaying()) mediaPlayer.start();
    }
    public  void stop(){
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
}
