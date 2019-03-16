package com.konatsup.musicapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.konatsup.musicapp.fragment.HomeFragment;
import com.konatsup.musicapp.fragment.PlayerFragment;
import com.konatsup.musicapp.fragment.PlaylistFragment;
import com.konatsup.musicapp.fragment.SearchFragment;
import com.konatsup.musicapp.service.PostService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    BottomNavigationView bottomNavigationView;

    private ViewPager viewPager;

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    PlaylistFragment playlistFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

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
                    Log.d("debug3", listPost.get(i).getTitle());
                    Log.d("debug3", listPost.get(i).getDescription());
                    Log.d("debug3", listPost.get(i).getImage_url());
                    Log.d("debug3", listPost.get(i).getMusic_url());
                }
            }
            @Override
            public void onFailure(Call<ListPost> call, Throwable t) {
                Log.d("debug4", t.getMessage());
            }

        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_call:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_chat:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_contact:
                                viewPager.setCurrentItem(2);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        playlistFragment = new PlaylistFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(searchFragment);
        adapter.addFragment(playlistFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void openPlayer() {
        PlayerFragment fragment = new PlayerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("selected", position);
//                fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
