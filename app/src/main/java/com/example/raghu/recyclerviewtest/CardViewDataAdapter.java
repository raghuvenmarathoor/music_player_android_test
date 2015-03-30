package com.example.raghu.recyclerviewtest;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raghu on 21-01-2015.
 */
public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {
    private static List<Song> songList;
    public CardViewDataAdapter(List<Song> songList){
        this.songList = songList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.cardview_layout, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Song song = songList.get(i);
         viewHolder.songId.setText(song.getSongId());
        viewHolder.songArtist.setText(song.getSongArtist());
        viewHolder.songTitle.setText(song.getSongTitle());
        viewHolder.songInfo.setText(song.getSongInfo());
        viewHolder.songId.setTag(i);
        viewHolder.songAlbumArtInfo.setText(song.getAlbumArtPath());

    }

    @Override
    public int getItemCount() {
        if (songList == null) {
            Log.d("CardViewDataAdapter", "songList is null");
        }
        return songList.size();
    }


    public List<Song> getSongList() {
        return songList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
     public TextView songId;
     public TextView songArtist;
     public TextView songTitle;
     public TextView songInfo;
     public TextView songAlbumArtInfo;
     public ViewHolder(View itemLayoutView) {
         super(itemLayoutView);
         songId = (TextView) itemLayoutView.findViewById(R.id.song_id);
         songArtist = (TextView) itemLayoutView.findViewById(R.id.song_artist);
         songTitle = (TextView) itemLayoutView.findViewById(R.id.song_title);
         songInfo = (TextView) itemLayoutView.findViewById(R.id.song_info);
         songAlbumArtInfo = (TextView) itemLayoutView.findViewById(R.id.song_album_art);
     }
 }
}
