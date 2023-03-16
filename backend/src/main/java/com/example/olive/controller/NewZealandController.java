package com.example.olive.controller;

import com.example.olive.exceptions.DataParseException;
import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.InvalidDateException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.HttpResponse;
import com.example.olive.model.NewZealandModel;
import com.example.olive.scheduler.JodiSourceScrapeImpl;
import com.example.olive.scheduler.NZSourceScrapeImpl;
import com.example.olive.service.NewZealandServiceImpl;
import com.example.olive.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nz")
public class NewZealandController {
    @Autowired
    NewZealandServiceImpl newZealandService;

    @Autowired
    Utils utils;

    @Autowired
    NZSourceScrapeImpl nzSourceScrapeImpl;

    @Autowired
    JodiSourceScrapeImpl jodiSourceScrapeImpl;

    @CrossOrigin(origins = "*")
    @GetMapping("/data/all/")
    public ResponseEntity<HttpResponse> getAll() {
        List<NewZealandModel> response = newZealandService.getAllNZ();
        return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/data/between/")
    public ResponseEntity<HttpResponse> getNZBetween(@RequestBody Map<String, String> requestBody) throws InvalidDateException {
        Date startDate = utils.stringToDate(requestBody.get("startDate"));
        Date endDate = utils.stringToDate(requestBody.get("endDate"));

        if (startDate == null || endDate == null) {
            throw new NullPointerException("Request body has missing information");
        } else if (startDate.after(endDate)) {
            throw new InvalidDateException("Start date cannot be after end date");
        }
        try {
            List<NewZealandModel> response = newZealandService.getNZByMonthRange(startDate, endDate);
            return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);

        } catch (GetException e) {
            return new ResponseEntity<>(new HttpResponse(e), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/data/yearmonth/")
    public ResponseEntity<HttpResponse> getNZInYearMonth(@RequestBody Map<String, String> requestBody) throws InvalidDateException {
        Date date = utils.stringToDate(requestBody.get("date"));

        if (date == null) {
            throw new NullPointerException("Request body has missing information");
        }
        try {
            NewZealandModel response = newZealandService.getNZByMonth(date);
            return new ResponseEntity<>(new HttpResponse(response), HttpStatus.OK);

        } catch (GetException e) {
            return new ResponseEntity<>(new HttpResponse(e), HttpStatus.BAD_REQUEST);

        }
    }

}
