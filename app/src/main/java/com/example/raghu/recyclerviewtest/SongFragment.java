package com.example.raghu.recyclerviewtest;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<Song> songList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView.Adapter mAdapter;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * songs = songsList;
     * @return A new instance of fragment SongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongFragment newInstance(ArrayList<Song> songs) {
        SongFragment fragment = new SongFragment();
        fragment.setSongList(songs);
        fragment.setRetainInstance(true);
        return fragment;
    }

    public SongFragment() {
        // Required empty public constructor
    }


    public void setSongList(ArrayList<Song> songList) {
       this.songList = songList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ArrayList<Song> getSongList() {
        /*ContentResolver musicResolver = getActivity().getContentResolver();
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
        }*/
        return songList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //setContentView(R.layout.activity_recyclerview);
         mAdapter = new CardViewDataAdapter(songList);
        mRecyclerView.setAdapter(mAdapter);
        //ListView listView = (ListView) findViewById(android.R.id.list);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (fab != null)
            fab.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
             * called after the scroll has completed.
             *
             * @param recyclerView The RecyclerView which scrolled.
             * @param dx           The amount of horizontal scroll.
             * @param dy           The amount of vertical scroll.
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //fab.show();
            }

            /**
             * Callback method to be invoked when RecyclerView's scroll state changes.
             *
             * @param recyclerView The RecyclerView whose scroll state has changed.
             * @param newState     The updated scroll state. One of { #SCROLL_STATE_IDLE},
             *                     {SCROLL_STATE_DRAGGING} or { #SCROLL_STATE_SETTLING}.
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    fab.hide();
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    //fab.show();
                }
            }
        });

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setFilter(String filter) {
        ArrayList<Song> songArrayList = new ArrayList<Song>();
        if (filter.equals("")) {
            songArrayList = songList;
        } else {
            for (Song song: songList) {
                String title = song.getSongTitle().toLowerCase();//.replaceAll("\\P{L}", "");
                String artist = song.getSongTitle().toLowerCase();//.replaceAll("\\P{L}", "");
                if (title.contains(filter.toLowerCase())|| artist.contains(filter.toLowerCase())) {
                    songArrayList.add(song);
                }

            }
        }


        RecyclerView rView = (RecyclerView) getActivity().findViewById(R.id.recycler_list_view);
        CardViewDataAdapter adapter = new CardViewDataAdapter(songArrayList);
        Log.d("SongFragment", "adapter swapped with " + songArrayList.size());
        rView.swapAdapter(adapter,false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
