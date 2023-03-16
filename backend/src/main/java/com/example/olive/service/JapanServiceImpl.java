package com.example.olive.service;

import com.example.olive.exceptions.GetException;
import com.example.olive.exceptions.SaveException;
import com.example.olive.model.JapanModel;
import com.example.olive.repository.JapanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JapanServiceImpl implements JapanService {

    @Autowired
    private JapanRepository japanRepository;

    Logger logger = LoggerFactory.getLogger(JapanServiceImpl.class);

    public JapanModel addJapan(JapanModel japanModel) throws SaveException {
        JapanModel saved = japanRepository.save(japanModel);
        if (saved == null) {
            logger.error("Unable to save JapanModel into database");
            throw new SaveException("Save unsuccessful");
        } else {
            return saved;
        }
    }

    public JapanModel getJapanByMonth(Date date) throws GetException {
        JapanModel japanModel = japanRepository.findByDate(date);
        if (japanModel == null) {
            logger.warn("Unable to get Japan data by month");
            throw new GetException("Get Japan by month unsuccessful");
        }
        return japanModel;
    }

    public List<JapanModel> getJapanByMonthRange(Date startDate, Date endDate) throws GetException {
        int startMonth = startDate.getMonth();
        startDate.setMonth(startMonth - 1);

        int endMonth = endDate.getMonth();
        endDate.setMonth(endMonth + 2);

        List<JapanModel> listRetrieved = japanRepository.findByDateBetween(startDate, endDate);
        if (listRetrieved.isEmpty()) {
            logger.warn("Unable to get Japan data by month range");
            throw new GetException("Get Japan by month range unsuccessful");
        }

        return listRetrieved;
    }

    public List<JapanModel> getAllJapan() {
        return japanRepository.findAll();
    }

    public boolean deleteAllJapan() {
        japanRepository.deleteAll();

        return japanRepository.count() == 0;
    }

}
