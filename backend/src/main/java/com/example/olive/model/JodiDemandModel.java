package com.example.olive.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class JodiDemandModel {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date date;

    @Indexed
    private Object oilData;

    public JodiDemandModel(Date date, Object oilData) {
        this.date = date;
        this.oilData = oilData;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Object getOilData() {
        return oilData;
    }

    public void setOilData(Object oilData) {
        this.oilData = oilData;
    }

}
