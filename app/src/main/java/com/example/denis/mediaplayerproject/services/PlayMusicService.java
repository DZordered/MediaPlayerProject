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


/**
 * Service for playing music after choice
 * <p>
 */
public class PlayMusicService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
        ,MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {
    /**
     * Media player instance
     */
    private MediaPlayer mediaPlayer = new MediaPlayer();
    /**
     * Our song path what we get from intent
     */
    private String path;
    /**
     * Current position of song in our listView, get from intent
     */
    private int currentPosition;
    /**
     * List of our songs
     */
    private ArrayList<Song> songs;
    /**
     * Instance what implement IBinder, for correct connection with service
     */
    private final IBinder mBinder = new PlayMusicServiceBinder();


    public PlayMusicService() {}

    /**
     * Set our Listeners
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
     *
     * @param intent for getting our currentPosition, path, and list of songs
     * @param flags NOP
     * @param startId NOP
     * @return start id
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getExtras().getString("path");
        songs = intent.getParcelableArrayListExtra("songs");
        currentPosition = intent.getExtras().getInt("currentPosition");

        startPlay(path);

        return START_STICKY;
    }

    /**
     * That method using for get service connection
     * <p>
     * @param intent NOP
     * @return our binder for that service
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Unbind our service from activity
     * <p>
     * @param intent have reference on service, what need to unBind
     * @return super of that method
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * Stop media player on completion
     * <p>
     * @param mp our media player (NOP)
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

    /**
     * Using for playing song from path
     * <p>
     * @param path of song what we need to play
     */
    public void startPlay(String path){

        // create media player with path
        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));

        //check our player on null
        if(mediaPlayer == null)
            Toast.makeText(this, "Create failed", Toast.LENGTH_SHORT).show();

        mediaPlayer.reset();

        try {
            // set our data source from path
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // start our player
        mediaPlayer.start();

        //setting completion listener for automatically play next song
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer arg) {
                next();
            }
        });
    }

    /**
     * Pause our player
     */
    public void pause(){
        mediaPlayer.pause();
    }

    /**
     * Method what realise playing next song (auto)
     * <p>
     */
    public void next(){
        String nextSong = " ";

        //check our next song on null
        if(songs.get(currentPosition++) != null){

            //if okay - get path
            nextSong = songs.get(currentPosition++).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();
        mediaPlayer.reset();
        try {
            // double check on null next song
            if(nextSong != null)

                // if okay - set data source from path
                mediaPlayer.setDataSource(nextSong);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for playing previous song
     * <p>
     */
    public void prev(){
        String prevSong = " ";

        //check previous song position on null
        if(songs.get(currentPosition--) != null){

            // if okay - get path
            prevSong = songs.get(currentPosition--).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();

        mediaPlayer.reset();
        try {

            //double check on null, if okay - set source from path
            if(prevSong != null) mediaPlayer.setDataSource(prevSong);

            mediaPlayer.prepare();
            mediaPlayer.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start our media player
     */
    public void play(){
        mediaPlayer.start();
    }

    /**
     * Stop our media player and release
     */
    public void stop(){
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * Checking is playing our media player
     * <p>
     * @return true if playing, false if not
     */
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    /**
     * Getting media player
     * <p>
     * @return media player instance ( currently)
     */
    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }


    /**
     * Service binder class
     */
   public class PlayMusicServiceBinder extends Binder{

        /**
         * Get instance of our service, for providing connection
         * <p>
         * @return current service
         */
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }


}
