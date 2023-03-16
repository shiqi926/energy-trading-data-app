package com.example.olive.service;

import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.NewZealandModel;

import java.util.Date;
import java.util.List;

public interface NewZealandService {
    NewZealandModel addNewZealand(NewZealandModel supplyModel) throws SaveException;

    NewZealandModel getNZByMonth(Date date) throws GetException;

    List<NewZealandModel> getNZByMonthRange(Date startDate, Date endDate) throws GetException;

    List<NewZealandModel> getAllNZ();
}
