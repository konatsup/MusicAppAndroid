package com.konatsup.musicapp.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konatsup.musicapp.ListItemClickListener;
import com.konatsup.musicapp.PlaylistAdapter;
import com.konatsup.musicapp.R;
import com.konatsup.musicapp.Tune;

import io.realm.Realm;
import io.realm.RealmResults;


public class HomeFragment extends Fragment {

    private Realm realm;
    private ListView listView;
    private PlaylistAdapter adapter;
    private ListItemClickListener mListener;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
//        deleteAll();
        add();
        RealmResults<Tune> result = realm.where(Tune.class).findAll();
        adapter = new PlaylistAdapter(result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mListener.openPlayer();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ListItemClickListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void add() {
        realm.beginTransaction();
        Tune tune = realm.createObject(Tune.class);
        tune.setId(1);
        tune.setTitle("愛迷エレジー");
        tune.setArtist("DECO*27");
        tune.setLength(184.0);
        tune.setLike(20);
        tune.setPlayListed(false);
        realm.commitTransaction();

    }

    public void add2() {
        realm.beginTransaction();
        Tune tune = realm.createObject(Tune.class);
        tune.setId(1);
        tune.setTitle("愛迷エレジー");
        tune.setArtist("DECO*27");
        tune.setLength(184.0);
        tune.setLike(3);
        tune.setPlayListed(true);
        realm.commitTransaction();

    }

    public void deleteAll() {
        final RealmResults<Tune> result = realm.where(Tune.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });
    }

}
