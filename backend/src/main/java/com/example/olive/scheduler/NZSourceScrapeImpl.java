package com.example.olive.scheduler;

import com.example.olive.exceptions.DataParseException;
import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.NewZealandModel;
import com.example.olive.model.NewZealandProductModel;
import com.example.olive.service.IOServiceImpl;
import com.example.olive.service.NewZealandServiceImpl;
import com.example.olive.utils.Utils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class NZSourceScrapeImpl implements NZSourceScrape {

    private static final String PAGE_URL = "https://www.mbie.govt.nz/building-and-energy/energy-and-natural-resources/energy-statistics-and-modelling/energy-statistics/oil-statistics/";
    private static final String BASE_URL = "https://www.mbie.govt.nz";
    private static final String COUNTRY = "NZ";
    private static final String SHEET_NAME = "Monthly_kt";
    private static final String MONTHS_KEYWORD = "Months";
    private static final String SUPPLY_CRUDE_OIL_KEYWORD = "Indigenous Production";
    private static final String DEMAND_KEYWORD = "Refinery Intake";
    private static final String SUPPLY_KEYWORD = "Refinery Output";
    private static final String IMPORTS_KEYWORD = "Imports";
    private static final String EXPORTS_KEYWORD = "Exports";
    private static final String STOCK_CHANGE_KEYWORD = "Stock Change";
    private static final String CRUDE_OIL_KEYWORD = "Crude Oil, Condensate and Naphtha";
    private static final String LPG_KEYWORD = "LPG";
    private static final String PETROL_KEYWORD = "Petrol";
    private static final String DIESEL_KEYWORD = "Diesel";
    private static final String AVIATION_FUEL_KEYWORD = "Aviation Fuels";
    private static final String FUEL_OIL_KEYWORD = "Fuel Oil";
    private static final int START_COL = 290;

    @Autowired
    ScraperImpl scraperImpl;
    @Autowired
    IOServiceImpl ioServiceImpl;
    @Autowired
    Utils utils;
    @Autowired
    NewZealandServiceImpl newZealandServiceImpl;
    @Autowired
    JodiSourceScrapeImpl jodiSourceScrapeImpl;
    Logger logger = LoggerFactory.getLogger(ScraperImpl.class);

    public void connect(boolean isLatest) {
        Document doc = scraperImpl.connect(PAGE_URL);
        Element content = doc.getElementsByClass("content-area").get(0);
        Elements links = content.getElementsByTag("a");
        for (Element link : links) {
            if (link.text().contains("Monthly")) {
                String linkHref = link.attr("href");
                boolean isDownloaded = scraperImpl.downloadDataFile(BASE_URL, linkHref, "Monthly data table", COUNTRY
                        , ".xlsx");
                if (isDownloaded) {
                    // extract files
                    try {
                        fileExtraction(isLatest);
                    } catch (SaveException | DataParseException e) {
                        logger.error("Unable to extract NZ data");
                    }
                } else {
                    logger.error("Unable to download file");
                }
            }
        }
    }

    public void fileExtraction(boolean isLatest) throws SaveException, DataParseException {
        File[] fileList = ioServiceImpl.getDataFiles(COUNTRY);
        XSSFWorkbook wb = ioServiceImpl.xlsxWorkbookParsing(fileList[0]);
        if (isLatest){
            newZealandLatestMonthExtraction(wb);
        } else {
            newZealandDataExtraction(wb);
        }
    }

    public void newZealandDataExtraction(XSSFWorkbook workbook) throws SaveException, DataParseException {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        boolean isImports = false;
        boolean isExports = false;
        boolean isDemand = false;
        boolean isSupplyCrudeOil = false;
        boolean isSupply = false;

        Map<Integer, NewZealandModel> newZealandModelMap = new HashMap<>();
        Map<Integer, Date> intToDateMap = new HashMap<>();

        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            String firstCellStringValue = firstCell.getStringCellValue();

            if (firstCellStringValue.equals(MONTHS_KEYWORD)) {
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    Date date = utils.numberToDate(row.getCell(c).getNumericCellValue());
                    NewZealandModel tempSupplyModel = new NewZealandModel(date);

                    newZealandModelMap.put(c, tempSupplyModel);
                    intToDateMap.put(c, date);
                }
            } else if (firstCellStringValue.equals(SUPPLY_CRUDE_OIL_KEYWORD)) {
                isSupplyCrudeOil = true;
            } else if (firstCellStringValue.equals(IMPORTS_KEYWORD)) {
                isImports = true;
            } else if (firstCellStringValue.equals(EXPORTS_KEYWORD)) {
                isExports = true;
                isImports = false;
            } else if (firstCellStringValue.equals(STOCK_CHANGE_KEYWORD)) {
                isExports = false;
            } else if (firstCellStringValue.equals(DEMAND_KEYWORD)) {
                isDemand = true;
            } else if (firstCellStringValue.equals(SUPPLY_KEYWORD)) {
                isDemand = false;
                isSupply = true;
            } else if (isSupplyCrudeOil) {
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    NewZealandProductModel supply = newZealandModelMap.get(c).getSupply();
                    double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(c)!=null ? row.getCell(c).getNumericCellValue() : 0);

                    supply.setCrudeOilCondensatesNaphtha(cellNumericValue);
                }
                isSupplyCrudeOil = false;
            } else if (isImports) {
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    NewZealandProductModel imports = newZealandModelMap.get(c).getImports();
                    double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(c)!=null ? row.getCell(c).getNumericCellValue() : 0);

                    switch (firstCellStringValue) {
                        case CRUDE_OIL_KEYWORD:
                            imports.setCrudeOilCondensatesNaphtha(cellNumericValue);
                            break;

                        case LPG_KEYWORD:
                            imports.setLpg(cellNumericValue);
                            break;

                        case PETROL_KEYWORD:
                            imports.setGasoline(cellNumericValue);
                            break;

                        case DIESEL_KEYWORD:
                            imports.setDiesel(cellNumericValue);
                            break;

                        case AVIATION_FUEL_KEYWORD:
                            imports.setAviation(cellNumericValue);
                            break;

                        case FUEL_OIL_KEYWORD:
                            imports.setFuelOil(cellNumericValue);
                            break;
                    }
                }
            } else if (isExports) {
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    NewZealandProductModel exports = newZealandModelMap.get(c).getExports();
                    double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(c)!=null ? row.getCell(c).getNumericCellValue() : 0);

                    switch (firstCellStringValue) {
                        case CRUDE_OIL_KEYWORD:
                            exports.setCrudeOilCondensatesNaphtha(cellNumericValue);
                            break;

                        case LPG_KEYWORD:
                            exports.setLpg(cellNumericValue);
                            break;

                        case PETROL_KEYWORD:
                            exports.setGasoline(cellNumericValue);
                            break;

                        case DIESEL_KEYWORD:
                            exports.setDiesel(cellNumericValue);
                            break;

                        case AVIATION_FUEL_KEYWORD:
                            exports.setAviation(cellNumericValue);
                            break;

                        case FUEL_OIL_KEYWORD:
                            exports.setFuelOil(cellNumericValue);
                            break;
                    }
                }
            } else if (isDemand) {
                // add conversion
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    NewZealandProductModel demand = newZealandModelMap.get(c).getDemand();
                    double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(c)!=null ? row.getCell(c).getNumericCellValue() : 0);

                    demand.setCrudeOilCondensatesNaphtha(cellNumericValue);
                }
                isDemand = false;
            } else if (isSupply) {
                for (int c = START_COL; c < row.getLastCellNum(); c++) {
                    NewZealandProductModel supply = newZealandModelMap.get(c).getSupply();
                    double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(c)!=null ? row.getCell(c).getNumericCellValue() : 0);

                    switch (firstCellStringValue) {
                        case LPG_KEYWORD:
                            supply.setLpg(cellNumericValue);
                            break;

                        case PETROL_KEYWORD:
                            supply.setGasoline(cellNumericValue);
                            break;

                        case DIESEL_KEYWORD:
                            supply.setDiesel(cellNumericValue);
                            break;

                        case AVIATION_FUEL_KEYWORD:
                            supply.setAviation(cellNumericValue);
                            break;

                        case FUEL_OIL_KEYWORD:
                            supply.setFuelOil(cellNumericValue);
                            break;
                    }
                }
            }
        }

        Map<Date, NewZealandProductModel> jodiMap = jodiSourceScrapeImpl.csvExtraction();

        for (Map.Entry<Integer, NewZealandModel> e : newZealandModelMap.entrySet()) {
            Date date = e.getValue().getDate();
            NewZealandModel value = e.getValue();
            NewZealandProductModel nzDemand = value.getDemand();

            if (jodiMap.containsKey(date)) {
                NewZealandProductModel jodiDemand = jodiMap.get(date);

                // set demand values
                nzDemand.setLpg(jodiDemand.getLpg());
                nzDemand.setGasoline(jodiDemand.getGasoline());
                nzDemand.setDiesel(jodiDemand.getDiesel());
                nzDemand.setAviation(jodiDemand.getAviation());
                nzDemand.setFuelOil(jodiDemand.getFuelOil());

                // set total values
                nzDemand.calculateTotal();
                value.getSupply().calculateTotal();
                value.getImports().calculateTotal();
                value.getExports().calculateTotal();
                value.calculateNetImportsTotal();
                value.calculateGrossBalanceTotal();
                value.calculateNetBalanceTotal();
                value.calculateProductYieldTotal();
            }
        }

        System.out.println(newZealandModelMap);

        for (Map.Entry<Integer, NewZealandModel> entry : newZealandModelMap.entrySet()) {
            NewZealandModel newZealandModel = entry.getValue();
            try {
                newZealandServiceImpl.getNZByMonth(newZealandModel.getDate());
                logger.error("NZ data for month " + newZealandModel.getDate() + " already exists");
            } catch (GetException e) {
                newZealandServiceImpl.addNewZealand(newZealandModel);
                logger.info("NZ data for month " + newZealandModel.getDate() + " inserted into database");
            }
        }
    }

    public void newZealandLatestMonthExtraction(XSSFWorkbook workbook) throws SaveException, DataParseException {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        boolean isImports = false;
        boolean isExports = false;
        boolean isDemand = false;
        boolean isSupplyCrudeOil = false;
        boolean isSupply = false;

        int lastCellNum = 0;

        NewZealandModel newZealandModel = new NewZealandModel();

        for (Row row : sheet) {
            Cell firstCell = row.getCell(0);
            String firstCellStringValue = firstCell.getStringCellValue();

            if (firstCellStringValue.equals(MONTHS_KEYWORD)) {
                lastCellNum = row.getLastCellNum() - 1;
                Date date = utils.numberToDate(row.getCell(lastCellNum).getNumericCellValue());
                newZealandModel.setDate(date);
            } else if (firstCellStringValue.equals(SUPPLY_CRUDE_OIL_KEYWORD)) {
                isSupplyCrudeOil = true;
            } else if (firstCellStringValue.equals(IMPORTS_KEYWORD)) {
                isImports = true;
            } else if (firstCellStringValue.equals(EXPORTS_KEYWORD)) {
                isExports = true;
                isImports = false;
            } else if (firstCellStringValue.equals(STOCK_CHANGE_KEYWORD)) {
                isExports = false;
            } else if (firstCellStringValue.equals(DEMAND_KEYWORD)) {
                isDemand = true;
            } else if (firstCellStringValue.equals(SUPPLY_KEYWORD)) {
                isDemand = false;
                isSupply = true;
            } else if (isSupplyCrudeOil) {
                double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(lastCellNum)!=null ? row.getCell(lastCellNum).getNumericCellValue() : 0);
                newZealandModel.getSupply().setCrudeOilCondensatesNaphtha(cellNumericValue);

                isSupplyCrudeOil = false;
            } else if (isImports) {
                double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(lastCellNum)!=null ? row.getCell(lastCellNum).getNumericCellValue() : 0);
                NewZealandProductModel imports = newZealandModel.getImports();

                switch (firstCellStringValue) {
                    case CRUDE_OIL_KEYWORD:
                        imports.setCrudeOilCondensatesNaphtha(cellNumericValue);
                        break;

                    case LPG_KEYWORD:
                        imports.setLpg(cellNumericValue);
                        break;

                    case PETROL_KEYWORD:
                        imports.setGasoline(cellNumericValue);
                        break;

                    case DIESEL_KEYWORD:
                        imports.setDiesel(cellNumericValue);
                        break;

                    case AVIATION_FUEL_KEYWORD:
                        imports.setAviation(cellNumericValue);
                        break;

                    case FUEL_OIL_KEYWORD:
                        imports.setFuelOil(cellNumericValue);
                        break;
                }
            } else if (isExports) {
                double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(lastCellNum)!=null ? row.getCell(lastCellNum).getNumericCellValue() : 0);
                NewZealandProductModel exports = newZealandModel.getExports();

                switch (firstCellStringValue) {
                    case CRUDE_OIL_KEYWORD:
                        exports.setCrudeOilCondensatesNaphtha(cellNumericValue);
                        break;

                    case LPG_KEYWORD:
                        exports.setLpg(cellNumericValue);
                        break;

                    case PETROL_KEYWORD:
                        exports.setGasoline(cellNumericValue);
                        break;

                    case DIESEL_KEYWORD:
                        exports.setDiesel(cellNumericValue);
                        break;

                    case AVIATION_FUEL_KEYWORD:
                        exports.setAviation(cellNumericValue);
                        break;

                    case FUEL_OIL_KEYWORD:
                        exports.setFuelOil(cellNumericValue);
                        break;
                }
            } else if (isDemand) {
                double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(lastCellNum)!=null ? row.getCell(lastCellNum).getNumericCellValue() : 0);
                newZealandModel.getDemand().setCrudeOilCondensatesNaphtha(cellNumericValue);

                isDemand = false;
            } else if (isSupply) {
                double cellNumericValue = utils.convertThousandTonnesToMetricTonnes(row.getCell(lastCellNum)!=null ? row.getCell(lastCellNum).getNumericCellValue() : 0);
                NewZealandProductModel supply = newZealandModel.getSupply();
                switch (firstCellStringValue) {
                    case LPG_KEYWORD:
                        supply.setLpg(cellNumericValue);
                        break;

                    case PETROL_KEYWORD:
                        supply.setGasoline(cellNumericValue);
                        break;

                    case DIESEL_KEYWORD:
                        supply.setDiesel(cellNumericValue);
                        break;

                    case AVIATION_FUEL_KEYWORD:
                        supply.setAviation(cellNumericValue);
                        break;

                    case FUEL_OIL_KEYWORD:
                        supply.setFuelOil(cellNumericValue);
                        break;
                }
            }
        }

        Map<Date, NewZealandProductModel> jodiMap = jodiSourceScrapeImpl.csvExtraction();

        if (jodiMap.containsKey(newZealandModel.getDate())) {
            NewZealandProductModel jodiDemand = jodiMap.get(newZealandModel.getDate());
            NewZealandProductModel nzDemand = newZealandModel.getDemand();

            nzDemand.setLpg(jodiDemand.getLpg());
            nzDemand.setGasoline(jodiDemand.getGasoline());
            nzDemand.setDiesel(jodiDemand.getDiesel());
            nzDemand.setAviation(jodiDemand.getAviation());
            nzDemand.setFuelOil(jodiDemand.getFuelOil());

            // set total values
            nzDemand.calculateTotal();
            newZealandModel.getSupply().calculateTotal();
            newZealandModel.getImports().calculateTotal();
            newZealandModel.getExports().calculateTotal();
            newZealandModel.calculateNetImportsTotal();
            newZealandModel.calculateGrossBalanceTotal();
            newZealandModel.calculateNetBalanceTotal();
            newZealandModel.calculateProductYieldTotal();
        }
        try {
            newZealandServiceImpl.getNZByMonth(newZealandModel.getDate());
            logger.error("NZ data for month " + newZealandModel.getDate() + " already exists");
        } catch (GetException e) {
            newZealandServiceImpl.addNewZealand(newZealandModel);
            logger.info("NZ data for month " + newZealandModel.getDate() + " inserted into database");
        }
    }
}