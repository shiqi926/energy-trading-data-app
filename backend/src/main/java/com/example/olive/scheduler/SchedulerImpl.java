package com.example.olive.scheduler;

import com.example.olive.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/*
 * This class serves to schedule batch jobs for all scrapings and file clean-ups.
 * */

@Component
public class SchedulerImpl implements Scheduler {
    private final File directory = new File("src/main/resources/downloads");
    @Autowired
    NZSourceScrapeImpl nzSourceScrapeImpl;
    @Autowired
    JapanSourceScrapeImpl japanSourceScrapeImpl;
    @Autowired
    Utils utils;

    // Scrapes NZ source, parses the xlsx notebook, extracts data, and updates database
    // Scrapes Jodi source, unzips the file, extracts data, and updates database
    // On the 1st of every month, at 0000 hrs
    @Async
    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleNZScrape() {
        nzSourceScrapeImpl.connect(true);
    }

    // Scrapes JP source, parses the xls notebook, extracts data, and updates database
    // On the 1st of every month, at 0000 hrs
    @Async
    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleJapanScrape() {
        japanSourceScrapeImpl.connect(true);
    }

    // Project cleanup
    // On the 1st of every month, at 0100 hrs
    @Async
    @Scheduled(cron = "0 0 1 1 * *")
    public void scheduleCleanUp() {
        if (directory.listFiles() != null && directory.listFiles().length > 0) {
            for (File countryDatasets : directory.listFiles()) {
                for (File file : countryDatasets.listFiles()) {
                    utils.deleteDirectory(file);
                }
            }
        }
    }

}
