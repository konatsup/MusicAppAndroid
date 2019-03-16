package com.konatsup.musicapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.konatsup.musicapp.R;
import com.konatsup.musicapp.Tune;

import org.parceler.Parcels;


public class PlayerFragment extends Fragment {

    private ImageView jacketImage;
    private TextView titleTextView;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        jacketImage = (ImageView) view.findViewById(R.id.jacketImage);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        prevButton= (Button)view.findViewById(R.id.buttonPrev);
        playButton= (Button)view.findViewById(R.id.buttonPlay);
        nextButton= (Button)view.findViewById(R.id.buttonNext);

        Tune tune = Parcels.unwrap(getArguments().getParcelable("tune"));
        Glide.with(getContext()).load(tune.getImageUrl()).into(jacketImage);
        titleTextView.setText(tune.getTitle());

        return view;
    }

}
