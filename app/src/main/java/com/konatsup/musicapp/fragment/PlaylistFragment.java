package com.konatsup.musicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.konatsup.musicapp.ListItemClickListener;
import com.konatsup.musicapp.ListPost;
import com.konatsup.musicapp.PlaylistAdapter;
import com.konatsup.musicapp.Post;
import com.konatsup.musicapp.R;
import com.konatsup.musicapp.Tune;
import com.konatsup.musicapp.service.PostService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaylistFragment extends Fragment {

    private Realm realm;
    private ListView listView;
    private PlaylistAdapter adapter;
    private ListItemClickListener mListener;

    private List<Tune> tunes;

    public PlaylistFragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        RealmResults<Tune> result = realm.where(Tune.class).equalTo("isPlayListed", true).findAll();
        tunes = new ArrayList<>();

        fetchPosts();
        adapter = new PlaylistAdapter(getActivity(), R.layout.playlist, tunes);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
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


//    public void add() {
//        realm.beginTransaction();
//        tune.setPlayListed(false);
//        realm.commitTransaction();
//
//    }

    public void deleteAll() {
        final RealmResults<Tune> result = realm.where(Tune.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();
            }
        });
    }

    private void fetchPosts(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://music-app-konatsup.herokuapp.com/api/").addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService service = retrofit.create(PostService.class);

        service.getPosts().enqueue(new Callback<ListPost>() {
            @Override
            public void onResponse(Call<ListPost> call, Response<ListPost> response) {
                List<Post> listPost = response.body().getListPost();
                int s = listPost.size();
                for(int i = 0; i < s; i++){
                    Tune tune = new Tune();
                    tune.setId(1);
                    tune.setTitle(listPost.get(i).getTitle());
                    tune.setArtist(listPost.get(i).getDescription());
                    tune.setImageUrl(listPost.get(i).getImage_url());
                    tune.setMusicUrl(listPost.get(i).getMusic_url());
                    tune.setLength(184.0);
                    tune.setLike(3);
                    tune.setPlayListed(true);
                    tunes.add(tune);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<ListPost> call, Throwable t) {
                Log.d("debug4", t.getMessage());
            }

        });
    }

}
