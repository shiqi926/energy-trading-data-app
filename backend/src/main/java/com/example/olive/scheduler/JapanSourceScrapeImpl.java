package com.example.olive.scheduler;

import com.example.olive.exceptions.GetException;
import com.example.olive.model.JapanModel;
import com.example.olive.model.JapanProductModel;
import com.example.olive.service.IOServiceImpl;
import com.example.olive.service.JapanServiceImpl;
import com.example.olive.utils.Utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JapanSourceScrapeImpl implements JapanSourceScrape {

    private static String pageUrl = "https://www.j-lpgas.gr.jp/stat/geppou/";
    private static String webUrl = "https://www.j-lpgas.gr.jp";

    @Autowired
    ScraperImpl scraperImpl;

    @Autowired
    IOServiceImpl ioServiceImpl;

    @Autowired
    Utils utils;

    @Autowired
    JapanServiceImpl japanServiceImpl;

    Logger logger = LoggerFactory.getLogger(JapanSourceScrapeImpl.class);
    private String country = "JP";

    public void connect(boolean isLatest) {
        Document doc = scraperImpl.connect(pageUrl);
        Elements statList = doc.getElementsByClass("stat-list");

        scraper(statList, isLatest);
    }

    private void scraper(Elements statList, boolean latest) {
        if (latest) {
            Element sl = statList.get(0);
            monthlyScrape(sl);
        } else {
            for (Element sl : statList) {
                monthlyScrape(sl);
            }
        }
        jpDataExtraction();
    }

    private boolean monthlyScrape(Element sl) {
        Element link = sl.getElementsByTag("a").get(0);
        String linkHref = link.attr("href");
        Element day = sl.getElementsByClass("day").get(0);
        String date = day.text();
        String[] dates = date.split("/");
        String printDate = dates[0] + "-" + dates[1];
        boolean downloaded = scraperImpl.downloadDataFile(linkHref, "", printDate, country, ".xls");
        return downloaded;
    }

    public void jpDataExtraction() {
        Map<String, Double> butane = new HashMap<>();
        Map<String, Double> propane = new HashMap<>();
        File[] files = ioServiceImpl.getDataFiles(country);

        for (File f : files) {
            try {
                // Initialize index and metric variables
                int propaneIndex = -1;
                int butaneIndex = -1;
                int rowIndex = -1;
                String rowMetrics = "";

                // Getting month and year from file name, and notebook parsing to get the sheet
                Date formattedDate = utils.stringToDate(f.getName());
                HSSFWorkbook wb = ioServiceImpl.xlsWorkbookParsing(f);
                Sheet sheet = wb.getSheet("資料月報");

                // Iterate through rows in sheets
                for (Row row : sheet) {
                    // Loop through every cell in the row
                    for (int c = 0; c < row.getLastCellNum(); c++) {
                        Cell cell = row.getCell(c);
                        String cellValue = "";
                        if (cell != null) {
                            /*
                             * Chunk of code aims to detect cell value of type STRING
                             * Check if value is in utils.translate hashmap in the utils class
                             * If it is, either save the column or row index to extract the numerals of the different metrics
                             * */
                            if (cell.getCellType().equals(CellType.STRING)) {
                                cellValue = cell.getStringCellValue();
                                if (utils.translate(cellValue) != null) {
                                    // Extracts column index of butane and propane
                                    if (propaneIndex == -1 || butaneIndex == -1) {
                                        if (utils.translate(cellValue).equals("propane")) {
                                            propaneIndex = cell.getColumnIndex();
                                        } else if (utils.translate(cellValue).equals("butane")) {
                                            butaneIndex = cell.getColumnIndex();
                                        }
                                    }
                                    // Extracts row index of metrics
                                    if (cell.getRowIndex() == rowIndex || utils.translate(cellValue) != null) {
                                        rowIndex = cell.getRowIndex();
                                        rowMetrics = utils.translate(cellValue);
                                    }
                                }
                            }

                            /*
                             * Chunk of code aims to detect cell value of type NUMERIC
                             * Check if row and column index of cell is equal to the set indexes
                             * If it is, put the metric and value into the HashMap
                             * */
                            if (cell.getRowIndex() == rowIndex && cell.getCellType().equals(CellType.NUMERIC)) {
                                if (cell.getColumnIndex() == butaneIndex) {
                                    System.out.println(cell.getNumericCellValue());
                                    butane.put(
                                            rowMetrics,
                                            utils.convertTonnesToMetricTonnes(cell.getNumericCellValue())
                                    );
                                } else if (cell.getColumnIndex() == propaneIndex) {
                                    propane.put(
                                            rowMetrics,
                                            utils.convertTonnesToMetricTonnes(cell.getNumericCellValue())
                                    );
                                }
                            }
                        }
                    }
                }
                // Create new productModel Objects, Butane & Propane model objects, and call save method to save to DB
                JapanProductModel butaneModel = getProductModel(butane);
                JapanProductModel propaneModel = getProductModel(propane);

                try {
                    japanServiceImpl.getJapanByMonth(formattedDate);
                    logger.error("JP data for month " + formattedDate + " already exists");
                } catch (GetException e) {
                    japanServiceImpl.addJapan(new JapanModel(formattedDate, butaneModel, propaneModel));
                    logger.info("JP data for month " + formattedDate + " inserted into database");
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private JapanProductModel getProductModel(Map<String, Double> japanProductData) {
        double supply = utils.calculateSupply(japanProductData.get("supplyMeter"), japanProductData.get("imports"));
        double demand = utils.calculateDemand(japanProductData.get("totalShipment"), japanProductData.get("exports"));
        double grossBalance = utils.calculateGrossBalance(supply, demand);

        return new JapanProductModel(
                japanProductData.get("imports"),
                japanProductData.get("supplyMeter"),
                japanProductData.get("exports"),
                japanProductData.get("totalShipment"),
                supply,
                demand,
                grossBalance
        );
    }

}
