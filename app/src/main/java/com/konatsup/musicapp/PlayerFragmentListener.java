package com.konatsup.musicapp;

import android.support.v4.media.session.MediaControllerCompat;

public interface PlayerFragmentListener {
    void switchBottomNavigationVisibility(boolean visibility);
    void setControllerCallBack(MediaControllerCompat.Callback callback);
    void skipToPrevious();
    void skipToNext();
    void pause();
    void play();
    void seekTo(int progress);
}
