package com.example.olive.scheduler;

import com.example.olive.exceptions.DataParseException;
import com.example.olive.model.NewZealandProductModel;
import com.example.olive.service.IOServiceImpl;
import com.example.olive.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class JodiSourceScrapeImpl implements JodiSourceScrape {
    private static final String BASE_URL = "https://www.jodidata.org";
    private static final String PAGE_URL = "https://www.jodidata.org/oil/database/data-downloads.aspx/";
    private final String FOLDER = "Jodi";

    @Autowired
    ScraperImpl scraperImpl;

    @Autowired
    IOServiceImpl ioServiceImpl;

    @Autowired
    Utils utils;

    Logger logger = LoggerFactory.getLogger(JodiSourceScrapeImpl.class);

    public Function<String, Map<String, String>> mapToObject = (line) -> {
        List<String> countries = new ArrayList<>();
        countries.add("NZ");
        String[] row = line.split(","); // a CSV has comma separated lines

        String refArea = row[0];
        String yearMonth = row[1];
        String oilProduct = row[2];
        String flowBreakdown = row[3];
        String unitMeasure = row[4];
        String value = row[5];

        if (countries.contains(refArea) &&
                utils.jodiMapping(flowBreakdown) != null &&
                utils.jodiMapping(oilProduct) != null &&
                unitMeasure.equals("KTONS") &&
                !value.equals("")) {
            return Stream.of(new String[][]{
                    {"yearMonth", yearMonth},
                    {"oilProduct", utils.jodiMapping(oilProduct)},
                    {"value", value}
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        }
        return null;
    };

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }

    public void connect() {
        Document doc = scraperImpl.connect(PAGE_URL);
        Element mainContent = doc.getElementById("maincontent");
        Element link =
                mainContent.getElementsByTag("ul").get(0).getElementsByTag("li").get(0).getElementsByTag("a").get(1);
        String linkHref = link.attr("href");
        Boolean isDownload = scraperImpl.downloadDataFile(BASE_URL, linkHref, "JodiData", "Jodi", ".zip");

        if (isDownload) {
            jodiFileUnzip();
        } else {
            logger.error("Jodi file download failed");
        }
    }

    public void jodiFileUnzip() {
        String fileZip = "src/main/resources/downloads/JodiDataset/JodiData.zip";
        File destDir = new File("src/main/resources/downloads/JodiDataset/Unzip");
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (FileNotFoundException e) {
            logger.error("Zip file not found");
        } catch (IOException e) {
            logger.error("Unable to read/write file content");
        }

    }

    public Map<Date, NewZealandProductModel> csvExtraction() throws DataParseException {
        try {
            connect(); // scrapes jodi, download into JodiDataset and unzips folder

            List<File> fileList = ioServiceImpl.listFilesForFolderWithFilter(FOLDER, "csv");
            Map<Date, NewZealandProductModel> result = processJodiInputFile(fileList.get(0));

            // Delete large jodi file after extraction
            boolean deleted = fileList.get(0).delete();
            if (deleted) {
                return result;
            } else {
                return null;
            }

        } catch (Exception e) {
            logger.error("Unable to extract Jodi data");
            throw new DataParseException("Unable to extract Jodi data");

        }
    }

    public Map<Date, NewZealandProductModel> processJodiInputFile(File file) {
        List<Map<String, String>> inputMap;
        Map<Date, NewZealandProductModel> mapping = new HashMap<>();

        try {
            InputStream inputFS = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            // pass each row to the mapToObject method, returns a List of Map<String, String>
            inputMap = br.lines()
                    .skip(1)
                    .map(mapToObject)
                    .collect(Collectors.toList());

            // close reader, amd remove null values from inputMap
            br.close();
            inputMap.removeAll(Collections.singleton(null));

            // Loop through list and instantiate NewZealandProductModel object to its corresponding yearMonth
            for (Map<String, String> output : inputMap) {
                String key = output.get("yearMonth");
                Date keyDate = utils.stringToDate(key);
                String oilType = output.get("oilProduct");
                double value = utils.convertThousandTonnesToMetricTonnes(Double.parseDouble(output.get("value")));

                if (mapping.get(keyDate) == null) {
                    mapping.put(keyDate, new NewZealandProductModel());
                }

                NewZealandProductModel newZealandProductModel = mapping.get(keyDate);

                switch (oilType) {
                    case "lpg":
                        newZealandProductModel.setLpg(value);
                        break;

                    case "gasoline":
                        newZealandProductModel.setGasoline(value);
                        break;

                    case "jetFuel":
                        newZealandProductModel.setAviation(value);
                        break;

                    case "diesel":
                        newZealandProductModel.setDiesel(value);
                        break;

                    case "fuelOil":
                        newZealandProductModel.setFuelOil(value);
                        break;
                }
            }

        } catch (IOException e) {
            logger.error("Unable to read jodi data file");
        }
        return mapping;
    }
}
















