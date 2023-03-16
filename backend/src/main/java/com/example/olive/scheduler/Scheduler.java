package com.example.olive.scheduler;

public interface Scheduler {
    void scheduleNZScrape();

    void scheduleJapanScrape();

    void scheduleCleanUp();
}
