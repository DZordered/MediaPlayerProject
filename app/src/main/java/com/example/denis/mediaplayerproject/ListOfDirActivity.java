package com.example.denis.mediaplayerproject;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListOfDirActivity extends AppCompatActivity {
    private ArrayList<String> dirs;
    private File file;
    private File catalogWithMusic;
    private File[] filesInCurrentDir;
    private ListView dirList;
    private ArrayAdapter<String> dirsWithMusic;
    private String pathForMusicCatalogItem;
    private String correctDuration;
    private ArrayList<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_dir);
        dirList = (ListView) findViewById(R.id.listOfDirWithMusic);
        String dirPathFromUser = getIntent().getExtras().getString("dirPathFromUser");
        if (dirPathFromUser != null) {
            file = new File(dirPathFromUser);
        }
        filesInCurrentDir = file.listFiles();
        dirs = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (File aFilesInCurrentDir : filesInCurrentDir) {
            if (aFilesInCurrentDir.isDirectory()) {
                if (aFilesInCurrentDir.exists()) {
                    if (aFilesInCurrentDir.listFiles(new AudioFileFilter()).length > 0) {
                        dirs.add(aFilesInCurrentDir.getName());
                    }
                }

            }
        }
        dirsWithMusic = new ArrayAdapter<>(this, R.layout.simple_list_of_items_layout
                , dirs);
        dirList.setAdapter(dirsWithMusic);
        dirList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pathForMusicCatalogItem = dirsWithMusic.getItem(position);
                Uri uri = Uri.parse(pathForMusicCatalogItem);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor == null) {
                    Toast.makeText(ListOfDirActivity.this, "Query failed", Toast.LENGTH_SHORT).show();
                } else if (!cursor.moveToFirst()) {
                    Toast.makeText(ListOfDirActivity.this, "No media on device", Toast.LENGTH_SHORT).show();
                } else {
                    int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int path = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    long album_artId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    do {
                        final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, album_artId);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumArtUri);
                        } catch (Exception exception) {
                            Toast.makeText(ListOfDirActivity.this, "Wrong bitmap", Toast.LENGTH_SHORT).show();
                        }
                        if (cursor.getString(durationColumn) != null){
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
                    Intent intent = new Intent(ListOfDirActivity.this, MainMediaPlayerActivity.class);
                    intent.putParcelableArrayListExtra("songsFromUserDir", songs);
                    startActivity(intent);
                }
            }
        });

    }
    class AudioFileFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String filename) {
            return (filename.endsWith(".mp3") || filename.endsWith(".MP3"));
        }
    }
}
