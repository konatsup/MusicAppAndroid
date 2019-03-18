package com.konatsup.musicapp;

import java.util.List;

public interface ListItemClickListener {
    void setCurrentTune(Tune tune);
    void updateTuneList(List<Tune> tunes);
}