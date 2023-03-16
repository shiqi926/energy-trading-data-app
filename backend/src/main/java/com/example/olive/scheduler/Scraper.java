package com.example.olive.scheduler;

import org.jsoup.nodes.Document;

public interface Scraper {
    Document connect(String url);

    boolean downloadDataFile(String baseUrl, String resourceUrl, String outputFileName, String folder, String filetype);
}
