package com.example.olive.controller;

import com.example.olive.model.HttpResponse;
import com.example.olive.model.JapanModel;
import com.example.olive.scheduler.JapanSourceScrapeImpl;
import com.example.olive.scheduler.JodiSourceScrapeImpl;
import com.example.olive.scheduler.NZSourceScrapeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scrape")
public class ScraperController {
    @Autowired
    NZSourceScrapeImpl nzSourceScrapeImpl;

    @Autowired
    JapanSourceScrapeImpl japanSourceScrapeImpl;

    @Autowired
    JodiSourceScrapeImpl jodiSourceScrapeImpl;

    @CrossOrigin(origins = "*")
    @GetMapping("/latest/")
    public ResponseEntity<HttpResponse> scrapeLatest() {
        nzSourceScrapeImpl.connect(true);
        japanSourceScrapeImpl.connect(true);
        return new ResponseEntity<>(new HttpResponse("Latest data scraped successfully"), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/jodi/")
    public ResponseEntity<HttpResponse> scrapeJodi() {
        jodiSourceScrapeImpl.connect();
        jodiSourceScrapeImpl.jodiFileUnzip();
        return new ResponseEntity<>(new HttpResponse("Data scraped successfully"), HttpStatus.OK);
    }
}
