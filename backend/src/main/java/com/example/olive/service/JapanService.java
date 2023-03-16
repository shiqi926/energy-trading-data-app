package com.example.olive.service;

import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.JapanModel;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface JapanService {
    JapanModel addJapan(JapanModel japanModel) throws SaveException;

    JapanModel getJapanByMonth(Date date) throws GetException;

    List<JapanModel> getJapanByMonthRange(Date startDate, Date endDate) throws GetException;

    List<JapanModel> getAllJapan();

    boolean deleteAllJapan();
}
