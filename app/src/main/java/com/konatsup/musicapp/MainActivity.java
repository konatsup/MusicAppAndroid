package com.konatsup.musicapp;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konatsup.musicapp.fragment.HomeFragment;
import com.konatsup.musicapp.fragment.PlayerFragment;
import com.konatsup.musicapp.fragment.PlaylistFragment;

import org.parceler.Parcels;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ListItemClickListener, PlayerFragmentListener {

    MediaBrowserCompat mBrowser;
    MediaControllerCompat mController;
    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        //再生中の曲の情報が変更された際に呼び出される
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) { }

        //プレイヤーの状態が変更された時に呼び出される
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) { }
    };

    Tune currentTune;
    BottomNavigationView bottomNavigationView;

    private ViewPager viewPager;
    private LinearLayout summaryBar;
    private TextView titleTextView;
    private TextView artistTextView;

    HomeFragment homeFragment;
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

        //サービスは開始しておく
        //Activity破棄と同時にServiceも停止して良いならこれは不要
        startService(new Intent(this, MusicService.class));

        //MediaBrowserを初期化
        mBrowser = new MediaBrowserCompat(this, new ComponentName(this, MusicService.class), connectionCallback, null);
        //接続(サービスをバインド)
        mBrowser.connect();

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
        playlistFragment = new PlaylistFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(playlistFragment);
        viewPager.setAdapter(adapter);
    }

    //接続時に呼び出されるコールバック
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            try {
                //接続が完了するとSessionTokenが取得できるので
                //それを利用してMediaControllerを作成
                mController = new MediaControllerCompat(MainActivity.this, mBrowser.getSessionToken());
                //サービスから送られてくるプレイヤーの状態や曲の情報が変更された際のコールバックを設定
                mController.registerCallback(controllerCallback);

                //既に再生中だった場合コールバックを自ら呼び出してUIを更新
                if (mController.getPlaybackState() != null && mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    controllerCallback.onMetadataChanged(mController.getMetadata());
                    controllerCallback.onPlaybackStateChanged(mController.getPlaybackState());
                }


            } catch (RemoteException ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            //サービスから再生可能な曲のリストを取得
            mBrowser.subscribe(mBrowser.getRoot(), subscriptionCallback);
        }
    };

    //Subscribeした際に呼び出されるコールバック
    private MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            //既に再生中でなければ初めの曲を再生をリクエスト
            if (mController.getPlaybackState() == null)
                Play(children.get(0).getMediaId());
        }
    };


    private void Play(String id) {
        //MediaControllerからサービスへ操作を要求するためのTransportControlを取得する
        //playFromMediaIdを呼び出すと、サービス側のMediaSessionのコールバック内のonPlayFromMediaIdが呼ばれる
        mController.getTransportControls().playFromMediaId(id, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBrowser.disconnect();
        if (mController.getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING)
            stopService(new Intent(this, MusicService.class));
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
    public void switchBottomNavigationVisibility(boolean isVisible) {
        if (isVisible) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setControllerCallBack(MediaControllerCompat.Callback callback) {
        controllerCallback = callback;
    }

    @Override
    public void skipToPrevious() {
        mController.getTransportControls().skipToPrevious();
    }

    @Override
    public void skipToNext() {
        mController.getTransportControls().skipToNext();
    }

    @Override
    public void pause() {
        mController.getTransportControls().pause();
    }

    @Override
    public void play() {
        mController.getTransportControls().play();
    }

    @Override
    public void seekTo(int progress) {
        mController.getTransportControls().seekTo(progress);
    }
}
