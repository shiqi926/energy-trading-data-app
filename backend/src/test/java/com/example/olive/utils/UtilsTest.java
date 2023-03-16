package com.example.olive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {

    private final static Utils UTIL = new Utils();

    private final static String JAP_WORD = "プロパン";
    private final static String JODI_WORD = "GASDIES";
    private final static double SHIPMENT_TOTAL = 23.5;
    private final static double EXPORTS = 20.1;
    private final static double SUPPLY_METER = 10.5;
    private final static double IMPORTS = 10.0;
    private final static double TONNES = 11.0;
    private final static String INVALID_DATE_STR = "12345";

    @Test
    @DisplayName("Translation Mapping")
    public void TestTranslate() {
        assertEquals("propane", UTIL.translate(JAP_WORD));
    }

    @Test
    @DisplayName("Jodi Mapping")
    public void TestJodiMapping() {
        assertEquals("diesel", UTIL.jodiMapping(JODI_WORD));
    }

    @Test
    @DisplayName("Demand calculation")
    public void TestCalculateDemand() {
        assertEquals(SHIPMENT_TOTAL - EXPORTS, UTIL.calculateDemand(SHIPMENT_TOTAL, EXPORTS));
    }

    @Test
    @DisplayName("Supply calculation")
    public void TestCalculateSupply() {
        assertEquals(SUPPLY_METER - IMPORTS, UTIL.calculateSupply(SUPPLY_METER, IMPORTS));
    }

    @Test
    @DisplayName("Thousand tonnes to metric tonnes calculation")
    public void TestConvertThousandTonnesToMetricTonnes() {
        assertEquals(TONNES * 907.185, UTIL.convertThousandTonnesToMetricTonnes(TONNES));
    }

    @Test
    @DisplayName("Tonnes to metric tonnes calculation")
    public void TestConvertTonnesToMetricTonnes() {
        assertEquals(TONNES * 0.907185, UTIL.convertTonnesToMetricTonnes(TONNES));
    }

    @Test
    @DisplayName("Invalid string to date conversion")
    public void TestInvalidStringToDate() {
        assertEquals(null, UTIL.stringToDate(INVALID_DATE_STR));
    }


}
