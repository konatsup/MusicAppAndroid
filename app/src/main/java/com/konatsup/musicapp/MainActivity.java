package com.konatsup.musicapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private ListView listView;
    private PlayListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        listView = (ListView) findViewById(R.id.listView);

        deleteAll();
        add();
        add();
        add();

        RealmResults<Tune> result = realm.where(Tune.class).findAll();
        adapter = new PlayListAdapter(result);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                PlayerFragment fragment = new PlayerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("selected", position);
//                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    public void add() {
        realm.beginTransaction();
        Tune tune = realm.createObject(Tune.class);
        tune.setId(1);
        tune.setTitle("愛迷エレジー");
        tune.setArtist("DECO*27");
        tune.setLength(184.0);
        tune.setLike(20);
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
