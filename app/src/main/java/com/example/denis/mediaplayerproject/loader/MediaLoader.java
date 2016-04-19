package com.example.denis.mediaplayerproject.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

/**
 * Custom loader what based on CursorLoader,
 * and used for loading media
 * resources in background
 * <p>
 */
public class MediaLoader extends CursorLoader {
    /**
     * Simply context ("this", ".class")
     */
    private Context context;
    /**
     * Our media uri
     */
    private Uri content;


    public MediaLoader(Context context, Uri content) {
        super(context);
        this.context = context;
        this.content = content;
    }

    /**
     * On background we are loading music content, based on our URI
     * <p>
     * @return cursor with all media columns
     */
    @Override
    public Cursor loadInBackground() {
        return context.getContentResolver().query(content, null, null, null, null);
    }
}
