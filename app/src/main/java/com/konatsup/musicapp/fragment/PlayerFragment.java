package com.konatsup.musicapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.konatsup.musicapp.PlayerFragmentListener;
import com.konatsup.musicapp.R;
import com.konatsup.musicapp.Tune;

import org.parceler.Parcels;

public class PlayerFragment extends Fragment {

    private ImageView jacketImage;
    private TextView titleTextView;
    private TextView positionTextView;
    private TextView durationTextView;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton nextButton;
    private SeekBar seekBar;

    private PlayerFragmentListener listener;
    private MediaControllerCompat.Callback callback;
    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        jacketImage = (ImageView) view.findViewById(R.id.jacketImage);
        positionTextView = (TextView) view.findViewById(R.id.position);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        durationTextView = (TextView) view.findViewById(R.id.duration);
        prevButton = (ImageButton) view.findViewById(R.id.buttonPrev);
        playButton = (ImageButton) view.findViewById(R.id.buttonPlay);
        nextButton = (ImageButton) view.findViewById(R.id.buttonNext);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.skipToPrevious();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.skipToNext();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    listener.pause();
                    playButton.setImageResource(R.drawable.exo_controls_play);
                    isPlaying = !isPlaying;
                } else {
                    listener.play();
                    playButton.setImageResource(R.drawable.exo_controls_pause);
                    isPlaying = !isPlaying;
                }
            }
        });


        playButton.setImageResource(R.drawable.exo_controls_play);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.seekTo(seekBar.getProgress());
            }
        });

        Tune tune = Parcels.unwrap(getArguments().getParcelable("tune"));
        Glide.with(getContext()).load(tune.getImageUrl()).into(jacketImage);
        titleTextView.setText(tune.getTitle());

        callback = new MediaControllerCompat.Callback() {
            //再生中の曲の情報が変更された際に呼び出される
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                titleTextView.setText(metadata.getDescription().getTitle());
                jacketImage.setImageBitmap(metadata.getDescription().getIconBitmap());
                durationTextView.setText(Long2TimeString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
                seekBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            }

            //プレイヤーの状態が変更された時に呼び出される
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                //プレイヤーの状態によってボタンの挙動とアイコンを変更する
                isPlaying =  state.getState() == PlaybackStateCompat.STATE_PLAYING;
                positionTextView.setText(Long2TimeString(state.getPosition()));
                seekBar.setProgress((int) state.getPosition());
            }
        };

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (PlayerFragmentListener) context;
        listener.switchBottomNavigationVisibility(false);
        listener.setControllerCallBack(callback);
    }

    @Override
    public void onDetach() {
        listener.switchBottomNavigationVisibility(true);
        listener = null;
        super.onDetach();
    }

    //Long値をm:ssの形式の文字列にする
    private String Long2TimeString(long src) {
        String mm = String.valueOf(src / 1000 / 60);
        String ss = String.valueOf((src / 1000) % 60);

        //秒は常に二桁じゃないと変
        if (ss.length() == 1) ss = "0" + ss;

        return mm + ":" + ss;
    }
}
