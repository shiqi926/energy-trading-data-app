package com.example.olive.scheduler;

public interface JapanSourceScrape {
    void connect(boolean isLatest);

    void jpDataExtraction();
}
