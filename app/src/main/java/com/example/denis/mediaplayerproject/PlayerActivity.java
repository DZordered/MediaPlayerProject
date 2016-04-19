package com.example.denis.mediaplayerproject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.mediaplayerproject.services.BackgroundPlayService;
import com.example.denis.mediaplayerproject.services.PlayMusicService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Activity what present player
 * <p>
 * Include next, stop, start, previous buttons. Seekbar, duration
 */
public class PlayerActivity extends AppCompatActivity {
    /**
     * Current song position
     */
    private int currentPosition;
    /**
     * List of songs
     */
    private ArrayList<Song> songs;
    /**
     * Seekbar what present duration progress
     */
    private SeekBar seekBar;
    /**
     * Values for converting in normal duration (minutes, seconds)
     */
    private double finalTime, startTime =  0;
    /**
     * Handler for our seekbar
     */
    private Handler durationHandler = new Handler();
    /**
     * Song duration
     */
    private TextView duration;
    /**
     * Path for current song
     */
    private String path;
    /**
     * Service for playing music (we are bind it)
     */
    private PlayMusicService pms;
    /**
     * Instance for connection with our service
     */
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        currentPosition = getIntent().getExtras().getInt("currentPosition");
        songs = getIntent().getParcelableArrayListExtra("songs");
        path = getIntent().getExtras().getString("path");

        // set fields for current song
        initFields();

        Intent playServiceIntent = new Intent(this, PlayMusicService.class);

        //start service
        startService(playServiceIntent.putExtra("songs", songs)
                .putExtra("currentPosition", currentPosition).putExtra("path", path));

        //get service connection
         serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                pms = ((PlayMusicService.PlayMusicServiceBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                pms = null;
            }
        };

        // bind our service for music playing
        onBind();

        //stop our service if it playing
        this.stopService(new Intent(this, BackgroundPlayService.class));
    }


    /**
     * Stop service "music playing"
     * and if it active - start background play
     * <p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pms.isPlaying()){
            pms.pause();
            startService(new Intent(this, BackgroundPlayService.class).putExtra("path", path));
        }
        stopService(new Intent(this, PlayMusicService.class));

    }


    // update our seekbar
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
                startTime = pms.getMediaPlayer().getCurrentPosition();
                seekBar.setProgress((int) startTime);
                long timeRemaining = (long) (finalTime - startTime);
                duration.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(timeRemaining),
                        TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining))
                ));
                durationHandler.postDelayed(this, 1000);
            }
    };

    public void initFields(){
        TextView title = (TextView) findViewById(R.id.song_title);
        title.setText(songs.get(currentPosition).getTitle());

        TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(songs.get(currentPosition).getArtist());

        TextView album = (TextView) findViewById(R.id.album);
        album.setText(songs.get(currentPosition).getAlbum());

        TextView fullDuration = (TextView)findViewById(R.id.fullduration);
        fullDuration.setText(songs.get(currentPosition).getDuration());

        duration = (TextView) findViewById(R.id.duration);

        final ImageButton prev = (ImageButton) findViewById(R.id.previous);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pms.prev();
            }
        });
        ImageButton pause = (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pms.pause();
            }
        });
        ImageButton play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pms.play();
            }
        });
        final ImageButton next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pms.next();
            }
        });
        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pms.stop();
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    pms.getMediaPlayer().seekTo(progress * 1000);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        if(songs.get(currentPosition).getBitmap() == null)
            Toast.makeText(this, "Null in image", Toast.LENGTH_SHORT).show();
        album_image.setImageBitmap(songs.get(currentPosition).getBitmap());
    }

    public void initFieldsForNextSong(){
        TextView title = (TextView) findViewById(R.id.song_title);
        title.setText(songs.get(++currentPosition).getTitle());

        TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(songs.get(++currentPosition).getArtist());

        TextView album = (TextView) findViewById(R.id.album);
        album.setText(songs.get(++currentPosition).getAlbum());

        TextView fullDuration = (TextView)findViewById(R.id.fullduration);
        fullDuration.setText(songs.get(++currentPosition).getDuration());

        duration = (TextView) findViewById(R.id.duration);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        album_image.setImageBitmap(songs.get(++currentPosition).getBitmap());
    }

    public void initFieldsForPrevSong(){
        TextView title = (TextView) findViewById(R.id.song_title);
        title.setText(songs.get(--currentPosition).getTitle());

        TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(songs.get(--currentPosition).getArtist());

        TextView album = (TextView) findViewById(R.id.album);
        album.setText(songs.get(--currentPosition).getAlbum());

        TextView fullDuration = (TextView)findViewById(R.id.fullduration);
        fullDuration.setText(songs.get(--currentPosition).getDuration());

        duration = (TextView) findViewById(R.id.duration);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        album_image.setImageBitmap(songs.get(--currentPosition).getBitmap());
    }

    public void onBind(){
       this.bindService(new Intent(this, PlayMusicService.class),serviceConnection, Context.BIND_AUTO_CREATE);
    }


}


