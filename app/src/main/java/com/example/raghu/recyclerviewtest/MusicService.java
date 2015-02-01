package com.example.raghu.recyclerviewtest;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;

    private int pausedLength = 0;

    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private String TAG = "MusicService";
    private boolean isPaused = false;
    public MusicService() {
    }
    public boolean isPlaying() {
        return player.isPlaying();
    }
    public void pause() {
        if(isPlaying()) {
            player.pause();
            isPaused = true;
            pausedLength = player.getCurrentPosition();
        }
    }
    public boolean isPaused() {
        return isPaused;
    }
    public void resume() {
        if(isPaused()) {
            //player.seekTo(pausedLength);
            player.start();
        }
    }
    public void previous() {
        if (songPosn >0) {
            songPosn--;
            playSong();
        }
        //TODO: notification that this is the last song
    }

    public void next() {
        if (songPosn < songs.size()) {
            songPosn ++;
            playSong();
        }
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG,"Song Completed");
        if (songPosn < songs.size()) {
            songPosn ++;

        } else if(songPosn == songs.size()){
            songPosn = 0;
        }
        playSong();

    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //initialize position
        songPosn=0;
//create player
        player = new MediaPlayer();
        initMusicPlayer();
    }
    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //player.setOnPreparedListener(this);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.e(TAG, "onPrepared call");
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.start();

            }
        });
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }


    }
    /**
     * Called to indicate an error.
     *
     * @param mp    the MediaPlayer the error pertains to
     * @param what  the type of error that has occurred:
     *              <ul>
     *              <li>{ #MEDIA_ERROR_UNKNOWN}
     *              <li>{MEDIA_ERROR_SERVER_DIED}
     *              </ul>
     * @param extra an extra code, specific to the error. Typically
     *              implementation dependent.
     *              <ul>
     *              <li>{ MEDIA_ERROR_IO}
     *              <li>{ MEDIA_ERROR_MALFORMED}
     *              <li>{ MEDIA_ERROR_UNSUPPORTED}
     *              <li>{ MEDIA_ERROR_TIMED_OUT}
     *              </ul>
     * @return True if the method handled the error, false if it didn't.
     * Returning false, or not having an OnErrorListener at all, will
     * cause the OnCompletionListener to be called.
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
       mp.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void playSong(){
        player.reset();
        //get song
        //Log.e(TAG, songs.toString());
        Song playSong = songs.get(songPosn);
        //get id

        //Log.e(TAG," songPosition:" + String.valueOf(songPosn));
        long currSong = Long.valueOf(playSong.getSongId());
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        Log.e(TAG,"SONG:" + trackUri.toString());
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        try {
            player.prepare();
        } catch (IOException e) {
            Log.e(TAG,"Error preparing", e);
        }

    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }


}
