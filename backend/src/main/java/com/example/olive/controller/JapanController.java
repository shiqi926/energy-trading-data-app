package com.example.olive.controller;

import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.InvalidDateException;
import com.example.olive.model.HttpResponse;
import com.example.olive.model.JapanModel;
import com.example.olive.scheduler.JapanSourceScrapeImpl;
import com.example.olive.scheduler.JodiSourceScrapeImpl;
import com.example.olive.service.JapanServiceImpl;
import com.example.olive.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jp")
public class JapanController {

    @Autowired
    private JapanServiceImpl japanServiceImpl;

    @Autowired
    private JapanSourceScrapeImpl japanSourceScrapeImpl;

    @Autowired
    private JodiSourceScrapeImpl jodiSourceScrapeImpl;

    @Autowired
    private Utils utils;

    @CrossOrigin(origins = "*")
    @GetMapping("/data/all/")
    public ResponseEntity<HttpResponse> getAll() {
        List<JapanModel> response = japanServiceImpl.getAllJapan();
        return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/data/between/")
    public ResponseEntity<HttpResponse> getJapanBetween(@RequestBody Map<String, String> requestBody) throws InvalidDateException {
        Date startDate = utils.stringToDate(requestBody.get("startDate"));
        Date endDate = utils.stringToDate(requestBody.get("endDate"));

        if (startDate == null || endDate == null) {
            throw new NullPointerException("Request body has missing information");
        } else if (startDate.after(endDate)) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        try {
            List<JapanModel> response = japanServiceImpl.getJapanByMonthRange(startDate, endDate);
            return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);
        } catch (GetException e) {
            return new ResponseEntity<>(new HttpResponse(e), HttpStatus.BAD_REQUEST);

        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/data/yearmonth/")
    public ResponseEntity<HttpResponse> getJapanInYearMonth(@RequestBody Map<String, String> requestBody) throws InvalidDateException {
        Date date = utils.stringToDate(requestBody.get("date"));

        if (date == null) {
            throw new NullPointerException("Request body has missing information");
        }
        try {
            JapanModel response = japanServiceImpl.getJapanByMonth(date);
            return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);

        } catch (GetException e) {
            return new ResponseEntity<>(new HttpResponse(e), HttpStatus.BAD_REQUEST);

        }
    }


}
