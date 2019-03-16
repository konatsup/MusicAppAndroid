package com.konatsup.musicapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konatsup.musicapp.fragment.HomeFragment;
import com.konatsup.musicapp.fragment.PlayerFragment;
import com.konatsup.musicapp.fragment.PlaylistFragment;
import com.konatsup.musicapp.fragment.SearchFragment;

import org.parceler.Parcels;


public class MainActivity extends AppCompatActivity implements ListItemClickListener, BottomNavigationVisibilityListener {

    Tune currentTune;
    BottomNavigationView bottomNavigationView;

    private ViewPager viewPager;
    private LinearLayout summaryBar;
    private TextView titleTextView;
    private TextView artistTextView;

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    PlaylistFragment playlistFragment;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        summaryBar = (LinearLayout) findViewById(R.id.summaryBar);
        titleTextView = (TextView) summaryBar.findViewById(R.id.title);
        artistTextView = (TextView) summaryBar.findViewById(R.id.artist);

        initializeCurrentTune();
        setupSummaryBar(currentTune);

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

        summaryBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayer(currentTune);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeCurrentTune() {
        currentTune = new Tune();
        currentTune.setTitle("タイトル");
        currentTune.setArtist("アーティスト名");
    }

    private void setupSummaryBar(Tune tune) {
        titleTextView.setText(tune.getTitle());
        artistTextView.setText(tune.getArtist());
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
    public void openPlayer(Tune tune) {
        setupSummaryBar(tune);
        currentTune = tune;
        PlayerFragment fragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("tune", Parcels.wrap(tune));
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.playerContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setBottomNavigationVisibility(boolean isVisible) {
        if (isVisible) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }
}
