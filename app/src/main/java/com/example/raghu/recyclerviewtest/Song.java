package com.example.raghu.recyclerviewtest;

/**
 * Created by Raghu on 21-01-2015.
 */
public class Song {
    public Song(String songId, String songTitle, String songArtist, String songInfo){
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songInfo = songInfo;
    }
    private String songId;
    private String songTitle;
    private String songArtist;
    private String songInfo;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(String songInfo) {
        this.songInfo = songInfo;
    }
}
