package com.example.olive.scheduler;

import com.example.olive.exceptions.DataParseException;
import com.example.olive.exceptions.SaveException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public interface NZSourceScrape {
    public void connect(boolean isLatest);

    public void newZealandDataExtraction(XSSFWorkbook workbook) throws SaveException, DataParseException;

    public void newZealandLatestMonthExtraction(XSSFWorkbook workbook) throws SaveException, DataParseException;

}
