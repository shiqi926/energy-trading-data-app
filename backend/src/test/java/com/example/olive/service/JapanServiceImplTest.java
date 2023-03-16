package com.example.olive.service;

import com.example.olive.exceptions.GetException;
import com.example.olive.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JapanServiceImplTest {


    private final static Utils UTIL = new Utils();

    private final static String START_DATE_STR = "2021-05";
    private final static String END_DATE_STR = "2021-10";
    private final static String INVALID_START_DATE_STR = "2050-06";
    private final static String INVALID_END_DATE_STR = "2050-08";
    private final static Date START_DATE = UTIL.stringToDate(START_DATE_STR);
    private final static Date END_DATE = UTIL.stringToDate(END_DATE_STR);
    private final static Date INVALID_START_DATE = UTIL.stringToDate(INVALID_START_DATE_STR);
    private final static Date INVALID_END_DATE = UTIL.stringToDate(INVALID_END_DATE_STR);

    @Autowired
    private JapanServiceImpl japanServiceImpl;

    @Test
    @DisplayName("Get Japan by valid month")
    void TestGetJapanByMonth() {
        try {
            assertNotNull(japanServiceImpl.getJapanByMonth(START_DATE));
        } catch (GetException e) {
            fail();
        }
    }

    @Test
    @DisplayName("Get Japan by invalid month")
    public void TestGetJapanByInvalidMonth() {
        try {
            assertNotNull(japanServiceImpl.getJapanByMonth(INVALID_START_DATE));
            fail();
        } catch (GetException e) {
            assertEquals("Get Japan by month unsuccessful", e.getMessage());
        }
    }

    @Test
    @DisplayName("Get Japan by valid month range")
    void TestGetJapanByMonthRange() {
        try {
            assertNotNull(japanServiceImpl.getJapanByMonthRange(START_DATE, END_DATE));
        } catch (GetException e) {
            fail();
        }
    }

    @Test
    @DisplayName("Get Japan by invalid month range")
    public void TestGetJapanByInvalidMonthRange() {
        try {
            assertNotNull(japanServiceImpl.getJapanByMonthRange(INVALID_START_DATE, INVALID_END_DATE));
            fail();
        } catch (GetException e) {
            assertEquals("Get Japan by month range unsuccessful", e.getMessage());
        }
    }

    @Test
    @DisplayName("Get all Japan data")
    void TestGetAllJapan() {
        assertNotNull(japanServiceImpl.getAllJapan());
    }
}
