package com.example.olive.scheduler;

import com.example.olive.model.NewZealandProductModel;

import java.io.File;
import java.util.Date;
import java.util.Map;

public interface JodiSourceScrape {
    void connect();

    void jodiFileUnzip();

    Map<Date, NewZealandProductModel> processJodiInputFile(File file);
}
