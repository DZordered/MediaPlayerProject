package com.example.denis.mediaplayerproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.mediaplayerproject.sevices.BackgroundPlayService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    private MediaPlayer mp;
    private int currentPosition;
    private ArrayList<Song> songs;
    private SeekBar seekBar;
    double finalTime, startTime =  0;
    private Handler durationHandler = new Handler();
    private TextView duration;
    private String path;
    private BackgroundPlayService bps;
    //private long resId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        currentPosition = getIntent().getExtras().getInt("currentPosition");
        songs = getIntent().getParcelableArrayListExtra("songs");
        path = getIntent().getExtras().getString("path");
        initFields();
        this.stopService(new Intent(this, BackgroundPlayService.class));
        startPlay(path);

    }

    private void startPlay(String path){
        mp = MediaPlayer.create(this, Uri.parse(path));
        if(mp == null)
            Toast.makeText(this, "Create failed", Toast.LENGTH_SHORT).show();

       mp.reset();
        try {
            mp.setDataSource(path);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        startTime = mp.getCurrentPosition();
        seekBar.setMax((int) startTime);
        durationHandler.postDelayed(updateSeekBarTime, 100);


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer arg) {
                nextSong();
            }
        });
    }

        public void pause(){
        mp.pause();
    }
    public void next(){
        String nextSong = " ";
        if(songs.get(currentPosition++) != null){
            nextSong = songs.get(currentPosition--).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();
        mp.reset();
            try {
                if(nextSong != null)
                mp.setDataSource(nextSong);
                mp.prepare();
                mp.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        initFieldsForNextSong();
    }
    public void prev(){
        String prevSong = " ";
        if(songs.get(currentPosition--) != null){
            prevSong = songs.get(currentPosition--).getPath();
        }else Toast.makeText(this, "End of list", Toast.LENGTH_SHORT).show();

        mp.reset();
        try {
            if(prevSong != null) mp.setDataSource(prevSong);

            mp.prepare();
            mp.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
        initFieldsForPrevSong();
    }

    public void play(){
        mp.start();
    }
    public void stop(){
        mp.stop();
    }

    public void nextSong(){
        if (++currentPosition >= songs.size()) {
            currentPosition = 0;
        } else {
            startPlay( Environment.DIRECTORY_MUSIC + songs.get(currentPosition));
        }
    }
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
                prev();
            }
        });
        ImageButton pause = (ImageButton) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        ImageButton play = (ImageButton) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        final ImageButton next = (ImageButton) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        if(songs.get(currentPosition).getBitmap() == null)
            Toast.makeText(this, "Null in image", Toast.LENGTH_SHORT).show();
        album_image.setImageBitmap(songs.get(currentPosition).getBitmap());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pause();
        startService(new Intent(this, BackgroundPlayService.class).putExtra("path", path));


    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            startTime = mp.getCurrentPosition();
            seekBar.setProgress((int) startTime);
            long timeRemaining = (long) (finalTime - startTime);
            duration.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeRemaining))
            ));
            durationHandler.postDelayed(this, 100);
        }
    };

    public void initFieldsForNextSong(){
        TextView title = (TextView) findViewById(R.id.song_title);
        title.setText(songs.get(currentPosition++).getTitle());

        TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(songs.get(currentPosition++).getArtist());

        TextView album = (TextView) findViewById(R.id.album);
        album.setText(songs.get(currentPosition++).getAlbum());

        TextView fullDuration = (TextView)findViewById(R.id.fullduration);
        fullDuration.setText(songs.get(currentPosition++).getDuration());

        duration = (TextView) findViewById(R.id.duration);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        album_image.setImageBitmap(songs.get(currentPosition++).getBitmap());
    }

    public void initFieldsForPrevSong(){
        TextView title = (TextView) findViewById(R.id.song_title);
        title.setText(songs.get(currentPosition--).getTitle());

        TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(songs.get(currentPosition--).getArtist());

        TextView album = (TextView) findViewById(R.id.album);
        album.setText(songs.get(currentPosition--).getAlbum());

        TextView fullDuration = (TextView)findViewById(R.id.fullduration);
        fullDuration.setText(songs.get(currentPosition--).getDuration());

        duration = (TextView) findViewById(R.id.duration);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        ImageView album_image = (ImageView)findViewById(R.id.album_image);
        album_image.setImageBitmap(songs.get(currentPosition--).getBitmap());
    }
}


