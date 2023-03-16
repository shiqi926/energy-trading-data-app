package com.example.olive.utils;

import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Utils {
    Logger logger = LoggerFactory.getLogger(Utils.class);

    private final static Map<String, String> TRANSLATE_MAP = Stream.of(new String[][] {
            { "輸入", "imports" },
            { "供給計", "supplyMeter" },
            { "輸出", "exports" },
            { "出荷計", "totalShipment" },
            { "プロパン", "propane" },
            { "ブタン", "butane" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private final static Map<String, String> JODI_MAP =Stream.of(new String[][] {
            { "LPG", "lpg" },
            { "GASOLINE", "gasoline" },
            { "JETKERO", "jetFuel" },
            { "GASDIES", "diesel" },
            { "RESFUEL", "fuelOil" },
            { "TOTDEMO", "demand" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public String translate(String jap) {
        return TRANSLATE_MAP.get(jap); // returns null if not found
    }

    public String jodiMapping(String word) {
        return JODI_MAP.get(word); // returns null if not found
    }

    public double calculateDemand(double shipmentTotal, double exports) {
        return shipmentTotal - exports;
    }

    public double calculateSupply(double supplyMeter, double imports) {
        return supplyMeter - imports;
    }

    // For NZ primary source
    public double convertThousandTonnesToMetricTonnes(double tt) {
        return tt * 907.185;
    }

    // For Japan primary source
    public double convertTonnesToMetricTonnes(double t) {
        return t * 0.907185;
    }

    public double calculateGrossBalance(double supply, double demand) {
        return supply - demand;
    }

    public double calculateNetBalance(double supply, double demand, double exports, double imports) {
        return supply - demand + exports - imports;
    }

    public double calculateNetBalance(double grossBalance, double netImports) {
        return grossBalance + netImports;
    }

    public double calculateProductYield(double supply, double refineryInput) {
        return supply / refineryInput;
    }

    public double calculateNetImports(double exports, double imports) {
        return exports - imports;
    }

    public Date stringToDate(String date) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM");
            return formatter.parse(date);
        } catch (ParseException e) {
            logger.error("Date parse failed");
            return null;
        }
    }

    public Date numberToDate(Double date) {
        return DateUtil.getJavaDate(date);
    }

    public boolean deleteDirectory(File targetDirectory) {
        File[] allContents = targetDirectory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return targetDirectory.delete();
    }
}
