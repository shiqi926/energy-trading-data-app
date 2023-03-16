package com.example.olive.repository;

import com.example.olive.model.NewZealandModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NewZealandRepository extends MongoRepository<NewZealandModel, Date> {
    NewZealandModel findByDate(Date date);

    List<NewZealandModel> findByDateBetween(Date startDate, Date endDate);
}