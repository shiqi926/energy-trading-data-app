package com.example.olive.service;

import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.NewZealandModel;
import com.example.olive.repository.NewZealandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NewZealandServiceImpl implements NewZealandService {

    @Autowired
    private NewZealandRepository newZealandRepository;

    Logger logger = LoggerFactory.getLogger(NewZealandServiceImpl.class);

    public NewZealandModel addNewZealand(NewZealandModel supplyModel) throws SaveException {
        NewZealandModel saved = newZealandRepository.save(supplyModel);

        if (saved == null) {
            logger.error("Unable to save NewZealandModel into database");
            throw new SaveException("Save unsuccessful");
        } else {
            return saved;
        }
    }

    public NewZealandModel getNZByMonth(Date date) throws GetException {
        NewZealandModel retrieved = newZealandRepository.findByDate(date);

        if (retrieved == null) {
            logger.warn("Unable to get New Zealand data by month");
            throw new GetException("Get New Zealand by month unsuccessful");
        }
        return retrieved;
    }

    public List<NewZealandModel> getNZByMonthRange(Date startDate, Date endDate) throws GetException {
//        int startMonth = startDate.getMonth();
//        startDate.setMonth(startMonth - 1);

        int endMonth = endDate.getMonth();
        endDate.setMonth(endMonth + 2);

        List<NewZealandModel> listRetrieved = newZealandRepository.findByDateBetween(startDate, endDate);

        if (listRetrieved.isEmpty()) {
            logger.warn("Unable to get New Zealand data by month range");
            throw new GetException("Get New Zealand by month range unsuccessful");
        }
        return listRetrieved;
    }

    public List<NewZealandModel> getAllNZ() {
        return newZealandRepository.findAll();
    }

    public boolean deleteAllNZ() {
        newZealandRepository.deleteAll();

        return newZealandRepository.count() == 0;
    }

}
