package com.example.olive.scheduler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class ScraperImpl implements Scraper {
    Logger logger = LoggerFactory.getLogger(ScraperImpl.class);

    /**
     * @param url URL of webpage
     * @return Document object if connection established else null - ref https://jsoup.org/apidocs/org/jsoup/nodes/Document.html
     */
    public Document connect(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            connection.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
            connection.referrer("http://www.google.com");
            return connection.post();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * @param baseUrl        base URL of webpage
     * @param resourceUrl    relative URL of a resource
     * @param outputFileName pretty much explains itself
     * @param folder         "NZ" or "JP" to download the file into different directories
     * @return true if downloads succeeds, false if download fails
     */
    public boolean downloadDataFile(String baseUrl, String resourceUrl, String outputFileName, String folder,
                                    String filetype) {
        String downloadPath = String.format("src/main/resources/downloads/%s/", (folder.equals("NZ") ? "NZDataset" : folder.equals("JP") ? "JPDataset" : "JodiDataset"));
        File directory = new File(downloadPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            URL url = new URL(baseUrl + resourceUrl);
            InputStream in = url.openStream();
            Files.copy(in, Paths.get(downloadPath + outputFileName + filetype), REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

}
