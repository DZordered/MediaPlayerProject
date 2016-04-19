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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.denis.mediaplayerproject.loader.MediaLoader;
import com.example.denis.mediaplayerproject.utils.MediaUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Class what used for find media resources in special dir
 * <p>
 */
public class ListOfDirActivity extends AppCompatActivity implements MediaUtil {

    /**
     * List of our dirs
     */
    private ArrayList<String> dirs;
    /**
     * Dir path from user
     */
    private File file;
    /**
     * Simply - catalog with music
     */
    private File catalogWithMusic;
    /**
     * Array of files in dir
     */
    private File[] filesInCurrentDir;
    /**
     * View of all dirs what exists media resources
     */
    private ListView dirList;
    /**
     * Adapter what include dirs with music
     */
    private ArrayAdapter<String> dirsWithMusic;
    /**
     * Path for current item
     */
    private String pathForMusicCatalogItem;
    /**
     * Correct song duration
     */
    private String correctDuration;
    /**
     * List of our songs
     */
    private ArrayList<Song> songs;
    /**
     * Song path
     */
    private int path;
    /**
     * Bitmap res for album
     */
    private Bitmap bitmap;
    /**
     * Options for ou bitmap
     */
    private BitmapFactory.Options options;

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

        // finding dirs what exists media resources
        for (File aFilesInCurrentDir : filesInCurrentDir) {
            if (aFilesInCurrentDir.isDirectory()) {
                if (aFilesInCurrentDir.exists()) {
                    if (aFilesInCurrentDir.listFiles(new AudioFileFilter()).length > 0) {
                        dirs.add(aFilesInCurrentDir.getName());
                    }
                }

            }
        }

        dirsWithMusic = new ArrayAdapter<>(this, R.layout.simple_list_of_items_layout, dirs);
        dirList.setAdapter(dirsWithMusic);
        dirList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pathForMusicCatalogItem = dirsWithMusic.getItem(position);

                Uri uri = Uri.parse(pathForMusicCatalogItem);

                Cursor cursor = new MediaLoader(ListOfDirActivity.this, uri).loadInBackground();

                songs = getListOfSongs(cursor);

                Intent intent = new Intent(ListOfDirActivity.this, MainMediaPlayerActivity.class);
                intent.putParcelableArrayListExtra("songsFromUserDir", songs);
                startActivity(intent);
            }
        });
    }

    /**
     * Class what provide audio filter
     */
    class AudioFileFilter implements FilenameFilter {

        /**
         * That method check media resources in dir
         * @param dir file directory
         * @param filename - filename
         * @return true if finds files what ends with current prefix
         */
        @Override
        public boolean accept(File dir, String filename) {
            return (filename.endsWith(".mp3") || filename.endsWith(".MP3"));
        }
    }

    /**
     * Get user songs from some storage
     * <p>
     * @param cursor of songs columns
     * @return list of songs
     */
    @Override
    public ArrayList<Song> getListOfSongs(Cursor cursor) {
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

                    bitmap = BitmapFactory.decodeFile(albumArtUri.getPath());
                    if (bitmap == null) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            bitmap = BitmapFactory.decodeFile(albumArtUri.getPath(), options);
                        } catch (Exception e) {
                        }//NOP
                    }
                } catch (OutOfMemoryError out) {
                    Toast.makeText(this, "Wrong bitmap", Toast.LENGTH_SHORT).show();
                }

                if (cursor.getString(durationColumn) != null) {
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
        return songs;
    }
}
