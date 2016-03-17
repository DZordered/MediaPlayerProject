package com.example.denis.mediaplayerproject;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.denis.mediaplayerproject.adapter.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MainMediaPlayerActivity extends AppCompatActivity {
    private ListView songList;
    private ArrayList<Song> songs;
    private Cursor cursor;
    int currentPosition = 0;
    private int path;
    private String correctDuration;
    private SongAdapter songAdapter = null;
    private BitmapFactory.Options options;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_media_player);
        songList = (ListView) findViewById(R.id.listOfMusic);
        EditText filter = (EditText) findViewById(R.id.filter);
        final Button sortByArtists = (Button)findViewById(R.id.sortByArtists);
        sortByArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByArtists();
            }
        });

        final Button sortByTitles = (Button)findViewById(R.id.sortByTitle);
        sortByTitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByTitles();
            }
        });

        final Button sortByAlbums = (Button)findViewById(R.id.sortByAlbums);
        sortByAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAlbums();
            }
        });

        final Button sortByDuration = (Button)findViewById(R.id.sortByDuration);
        sortByDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDuration();
            }
        });

        Button browser = (Button) findViewById(R.id.browser);
        browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllDirWithMusic();
            }
        });

        if(getIntent().hasExtra("songsFromUserDir")){
            ArrayList<Song> userMusicFromDir = getIntent().getExtras().getParcelableArrayList("songsFromUserDir");
            songList.setAdapter(new SongAdapter(this, userMusicFromDir));
        }

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = getContentResolver().query(uri, null, null, null, null);

         songs = new ArrayList<>();
        if (cursor == null) {
            Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No media on device", Toast.LENGTH_SHORT).show();
        } else {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            path = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));


            do {
                final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");
                Uri albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);
               bitmap = null;
                try {
                    //MediaStore.Images.Media.getBitmap(getContentResolver(), albumArtUri);
                    bitmap = BitmapFactory.decodeFile(albumArtUri.getPath());
                    if(bitmap == null) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            bitmap = BitmapFactory.decodeFile(albumArtUri.getPath(),options);
                        }catch (Exception e){}//NOP
                    }
                } catch (OutOfMemoryError out) {
                    Toast.makeText(this, "Wrong bitmap", Toast.LENGTH_SHORT).show();
                }

                if (cursor != null && cursor.getString(durationColumn) != null){
                    correctDuration = String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(cursor.getString(durationColumn))),
                            TimeUnit.MILLISECONDS.toSeconds(Integer.parseInt(cursor.getString(durationColumn))) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Integer.parseInt(cursor.getString(durationColumn))))
                    );
                }
                songs.add(new Song(
                        cursor.getString(titleColumn),
                        cursor.getString(artistColumn),
                        correctDuration,
                        cursor.getString(albumColumn),
                        bitmap,
                        cursor.getString(path)));
            } while (cursor.moveToNext());
        }
        songAdapter = new SongAdapter(this, songs);
        songList.setAdapter(songAdapter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                songAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainMediaPlayerActivity.this, PlayerActivity.class);
                currentPosition = position;
                intent.putExtra("currentPosition", currentPosition);
                intent.putParcelableArrayListExtra("songs", songs);
                intent.putExtra("path", songs.get(currentPosition).getPath());
                startActivity(intent);
            }
        });
    }
    public void sortByTitles(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });
        songList.setAdapter(new SongAdapter(this, songs));
    }
    public void sortByArtists(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getArtist().compareToIgnoreCase(rhs.getArtist());
            }
        });
        songList.setAdapter(new SongAdapter(this, songs));
    }
    public void sortByAlbums(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getAlbum().compareToIgnoreCase(rhs.getAlbum());
            }
        });
        songList.setAdapter(new SongAdapter(this, songs));
    }
    public void sortByDuration(){
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getDuration().compareToIgnoreCase(rhs.getDuration());
            }
        });
        songList.setAdapter(new SongAdapter(this, songs));
    }

    public void getAllDirWithMusic(){
        Intent intent = new Intent(this, EditDirActivity.class);
        startActivity(intent);
    }

}

