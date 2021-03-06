package com.konatsup.musicapp;

import java.util.List;

public interface ListItemClickListener {
    void setCurrentTune(int position, Tune tune, boolean isPlaylistInitialized);
    void updateTuneList(List<Tune> tunes);
}