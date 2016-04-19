package com.example.denis.mediaplayerproject.utils;

import android.database.Cursor;

import com.example.denis.mediaplayerproject.Song;

import java.util.ArrayList;

/**
 * Simple media util interface
 *<p>
 * Have one method for get list of songs
 */
public interface MediaUtil {

    /**
     * Get user songs from some storage
     * <p>
     * @param cursor of songs columns
     * @return ArrayList of our songs
     */
    ArrayList<Song> getListOfSongs(Cursor cursor);
}
