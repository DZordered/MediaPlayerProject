package com.example.denis.mediaplayerproject.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.denis.mediaplayerproject.R;
import com.example.denis.mediaplayerproject.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for setting songs in list view
 */
public class SongAdapter extends BaseAdapter implements Filterable{
    /**
     * Instance for filtering items
     */
    private ItemFilter itemFilter = new ItemFilter();
    /**
     * List of songs what need filtered
     */
    private ArrayList<Song> list;
    /**
     * List of songs what be filtered
     */
    private ArrayList<Song> filtered;
    /**
     * List for publish results
     */
    private ArrayList<Song> songList;
    private LayoutInflater li;

    public SongAdapter(Context context, ArrayList<Song> list){
        this.list = list;
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * This method used for getting new view for each song
     * <p>
     * @param position our song position
     * @param convertView view, what we will convert
     * @param parent parent viewGroup
     * @return new view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) view = li.inflate(R.layout.item_layout, parent, false);
        Song song = getSongPosition(position);
        TextView item = (TextView) view.findViewById(R.id.textView);
        item.setText("Title: " + song.getTitle() + "\n" +"Artist: " +  song.getArtist() + " Duration: " +
                song.getDuration() + " Album:  " + song.getAlbum());
        return view;
    }
    private Song getSongPosition(int position){
        return (Song) getItem(position);
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    class ItemFilter extends Filter{

        /**
         * Method realise filtering (search tool)
         * <p>
         * @param constraint chars, what user input
         * @return our filter results
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            int count = list.size();

            Song filterableSong;

            for (int i = 0; i < count; i++) {
                filterableSong = list.get(i);
                if(filterableSong.toString().toLowerCase().contains(filterString)){
                    filtered.add(filterableSong);
                }
            }

            results.values = filtered;
            results.count = filtered.size();

            return results;
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songList = (ArrayList<Song>) results.values;
            notifyDataSetChanged();
        }
    }
}
