package com.example.olive.repository;

import com.example.olive.model.JapanModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JapanRepository extends MongoRepository<JapanModel, Date> {
    JapanModel findByDate(Date date);

    List<JapanModel> findByDateBetween(Date startDate, Date endDate);
}
