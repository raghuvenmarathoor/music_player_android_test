package com.example.raghu.recyclerviewtest;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class Recyclerview extends ActionBarActivity implements SongFragment.OnFragmentInteractionListener
        , MediaController.MediaPlayerControl, SampleFragment.OnFragmentInteractionListener {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private static ArrayList<Song> songList;
    private static Song currentSong;
    private String TAG= "MainActivity";
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    SongFragment fragment;
    ViewPager viewPager;
    boolean isRestarting = false;
    int restartSongId = -1;
    int restartSeekPosition = -1;
    private MusicController controller;


    public class MusicController extends MediaController {

        public MusicController(Context c){
            super(c);
        }

        public void hide(){}

    }

    private void setController(){
        //set the controller up

        controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.recycler_list_view));
        controller.setEnabled(true);
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
         musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {

        if(musicSrv!=null && musicBound && musicSrv.isPng())
        return musicSrv.getDur();
        else return 0;

    }

    @Override
    public int getCurrentPosition() {
        if(musicSrv!=null && musicBound && musicSrv.isPng())
          return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
       musicSrv.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicSrv!=null && musicBound)
        return musicSrv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    /**
     * Get the audio session id for the player used by this VideoView. This can be used to
     * apply audio effects to the audio track of a video.
     *
     * @return The audio session, or 0 if there was an error.
     */
    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Called on Create");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview);
        //Log.d(TAG,"setContentView");

        songList = getSongList();
        if (savedInstanceState != null) {
            //Toast toast1 = Toast.makeText(this, "savedInstanceState not null", Toast.LENGTH_SHORT);
            //toast1.show();
            restartSongId = savedInstanceState.getInt("SONG_ID");
            restartSeekPosition = savedInstanceState.getInt("SONG_POSITION");
            isRestarting = true;
            //Toast toast = Toast.makeText(this,"Restored songPostn:" + String.valueOf(restartSongId),Toast.LENGTH_SHORT);
            //toast.show();
            //musicSrv.setSongId(songId);
            //musicSrv.seekTo(seekPosition);
            //musicSrv.playSong();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment = (SongFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_layout);

        if (fragment == null) {
            if (songList == null) {
                songList = getSongList();
            }
            fragment = SongFragment.newInstance(songList);
            //Log.d(TAG,"fragment created");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_layout, fragment).commit();
        }
        setController();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(this));
    }

    private void playNext(){
        musicSrv.next();
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicSrv.previous();
        controller.show(0);
    }

    /**
     * Called after {@link #onStop} when the current activity is being
     * re-displayed to the user (the user has navigated back to it).  It will
     * be followed by {@link #onStart} and then {@link #onResume}.
     * <p/>
     * <p>For activities that are using raw {@link android.database.Cursor} objects (instead of
     * creating them through
     * {@link #managedQuery(android.net.Uri, String[], String, String[], String)},
     * this is usually the place
     * where the cursor should be requeried (because you had deactivated it in
     * {@link #onStop}.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onStop
     * @see #onStart
     * @see #onResume
     */
    @Override
    protected void onRestart() {
        super.onRestart();


        Log.d(TAG,"onRestart");
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();


        Log.d(TAG,"onPause");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"onBackPressed");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"onConfiguration changed");
    }

    public void onFragmentInteraction(Uri uri) {

    }
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();

            //pass list
            musicSrv.setList(songList);
            musicBound = true;
            if (isRestarting = true) {
                if (restartSongId != -1) {
                    musicSrv.setSong(restartSongId);
                    musicSrv.playSong();
                    musicSrv.seekTo(restartSeekPosition);
                    isRestarting = false;
                }


            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

       /* Bundle bundle = new Bundle();
        bundle.putInt("SONG_POSITION", musicSrv.getCursorPosition());
        bundle.putInt("SONG_ID", musicSrv.getCurrentSongId());
        super.onSaveInstanceState(bundle);*/
        Log.d(TAG,"onStart");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recyclerview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_end) {
            stopService(playIntent);
            musicSrv=null;
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG,"onStop");
       /* Bundle bundle = new Bundle();
        bundle.putInt("SONG_POSITION", musicSrv.getCursorPosition());
        bundle.putInt("SONG_ID", musicSrv.getCurrentSongId());
        super.onSaveInstanceState(bundle);*/
    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG,"onSaveInstanceState");

        //Bundle bundle = new Bundle();
        outState.putInt("SONG_POSITION", musicSrv.getCursorPosition());
        outState.putInt("SONG_ID", musicSrv.getSongPosn());
        //Toast toast = Toast.makeText(this,"position saved:" + musicSrv.getSongPosn() + ":" + musicSrv.getCursorPosition() , Toast.LENGTH_SHORT);
        //toast.show();
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.  Most implementations will simply use {@link #onCreate}
     * to restore their state, but it is sometimes convenient to do it here
     * after all of the initialization has been done or to allow subclasses to
     * decide whether to use your default implementation.  The default
     * implementation of this method performs a restore of any view state that
     * had previously been frozen by {@link #onSaveInstanceState}.
     * <p/>
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"onRestoreInstanceState");
    }

    public void onListenButtonClicked(View view) {
        if (!mIslistening)
        {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        stopService(playIntent);
        musicSrv=null;
        playIntent = null;
        unbindService(musicConnection);
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }

        super.onDestroy();
    }
    public List<Song> generateRandomSongInfo() {
        List<Song> songList = new ArrayList<Song>();
        RandomString random = new RandomString(30);
        for (int i = 0; i<30; i++){
            Song song = new Song(random.nextString(),random.nextString(),random.nextString(),random.nextString());
            songList.add(song);
        }
        return songList;
    }

    private ArrayList<Song> getSongList() {
        ContentResolver musicResolver = getContentResolver();
        ArrayList<Song> songList = new ArrayList<Song>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int bitmapColumn = musicCursor.getColumnIndex((MediaStore.Audio.Albums.ALBUM_ART));
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(String.valueOf(thisId), thisTitle, thisArtist, ""));
                //songList.add(new Song(String.valueOf(thisId), thisTitle, thisArtist, ""),thisId);
            }
            while (musicCursor.moveToNext());
        }
        return songList;
    }

    public void songPicked(View view){
        Log.e(TAG, "Song picked: " + ((TextView) view.findViewById(R.id.song_id)).getTag().toString());
        int sng = Integer.parseInt(((TextView) view.findViewById(R.id.song_id)).getTag().toString());
        //Toast tst = Toast.makeText(this, String.valueOf(sng), Toast.LENGTH_SHORT);
        //tst.show();
        musicSrv.setSong(sng);
        musicSrv.playSong();

        /*try {
            this.wait(1000);
        } catch (InterruptedException e) {

        }*/
        ((ImageView)findViewById(R.id.play_pause_button)).setImageResource(R.drawable.ic_av_pause_edit);
        //onButtonClicked(findViewById(R.id.play_pause_button));
        Log.e(TAG, "Song Played");
    }

    public void onButtonClicked(View view) {
        Log.e(TAG, "Button Clicked :" + view.getId());
        ImageView imgView = (ImageView) view;
        if (view.getId() == R.id.play_pause_button) {
            if (musicSrv.isPlaying()) {
               musicSrv.pause();
               //view.setBackgroundResource(R.drawable.ic_av_pause);
                imgView.setImageResource(R.drawable.ic_av_play_arrow_edit);

            }else if (musicSrv.isPaused()){
                musicSrv.resume();
                //view.setBackgroundResource(R.drawable.ic_av_play_arrow);
                //imgView.setImageResource(R.drawable.ic_av_play_arrow_edit);
                imgView.setImageResource(R.drawable.ic_av_pause_edit);
                //view.setBackground();
            } else {
                musicSrv.setSong(0);
                musicSrv.playSong();
                imgView.setImageResource(R.drawable.ic_av_pause_edit);
            }
        } else if(view.getId() == R.id.previous_button) {
              musicSrv.previous();
        } else if (view.getId() == R.id.next_button) {
              musicSrv.next();
        }
    }

       protected class SpeechRecognitionListener implements RecognitionListener
    {
        Context context;
        SpeechRecognitionListener(Context context) {
            this.context = context;
        }

        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            Log.d(TAG, "error = " + error);
            Toast toast = Toast.makeText(context,"Error:" + String.valueOf(error), Toast.LENGTH_SHORT);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {
           Set<String> keySet = partialResults.keySet();

            /*for (String key: keySet) {
                Log.d(TAG,"key:" + key + "  value:" + partialResults.get(key));
            }*/
        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "no of words :" + matches.size());
            for (String song: matches) {
                Log.d(TAG,song);
                Toast toast =  Toast.makeText(context,song,Toast.LENGTH_SHORT);
                toast.show();
            }
            if (matches.size() > 0) {
                String action = matches.get(0);
                if (action.equals("play")) {
                    onButtonClicked (findViewById(R.id.play_pause_button));
                } else if (action.equals("pause")) {
                    onButtonClicked(findViewById(R.id.play_pause_button));
                } else if (action.equals("next")) {
                    onButtonClicked(findViewById(R.id.next_button));
                } else if (action.equals("previous")) {
                    onButtonClicked(findViewById(R.id.previous_button));
                }
                String[] stringArray = action.split("\\s+");
                if (stringArray[0].equalsIgnoreCase("find") || stringArray[0].equalsIgnoreCase("search")) {
                    String filter = "";
                    for (int i = 1; i<stringArray.length; i++) {
                        filter += stringArray[i] +  " ";

                    }
                    filter = filter.trim();
                    fragment.setFilter(filter);
                }
            }
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {

        }

    }
}
