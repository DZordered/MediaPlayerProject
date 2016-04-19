package com.example.denis.mediaplayerproject.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;


/**
 * Service for playing music on background
 * <p>
 */
public class BackgroundPlayService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        ,MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {
    /**
     * Media player instance
     */
    private MediaPlayer mediaPlayer = new MediaPlayer();
    /**
     * Our song path, what we get from intent
     */
    private String path;


    public BackgroundPlayService() {}

    /**
     * Set all our listeners
     * <p>
     */
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

    /**
     * Service method what we using for getting some path
     * of media, and play it instantly (if our MP is not active)
     * <p>
     * @param intent intent what we get (with some extras)
     * @param flags NOP
     * @param startId NOP
     * @return start id
     */
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

    /**
     * Just stop our MP when service be destroyed
     * <p>
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * Play action
     */
    public void play(){
        if(!mediaPlayer.isPlaying()) mediaPlayer.start();
    }

    /**
     * Stop action
     */
    public  void stop(){
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    /**
     *
     * @param intent NOP
     * @return  nothing, cause we didn't use bind on that service
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * This method stop our MP on completion
     * <p>
     * @param mp our media player
     */
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
