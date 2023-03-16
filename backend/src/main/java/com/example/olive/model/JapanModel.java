package com.example.olive.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import java.util.Date;

@Document(collection = "Japan")
public class JapanModel {

    @DateTimeFormat(iso = ISO.DATE)
    private Date date;

    @Indexed
    private JapanProductModel butane;
    private JapanProductModel propane;

    public JapanModel(Date date, JapanProductModel butane, JapanProductModel propane) {
        this.date = date;
        this.butane = butane;
        this.propane = propane;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JapanProductModel getButane() {
        return butane;
    }

    public void setButane(JapanProductModel butane) {
        this.butane = butane;
    }

    public JapanProductModel getPropane() {
        return propane;
    }

    public void setPropane(JapanProductModel propane) {
        this.propane = propane;
    }

    @Override
    public String toString() {
        return "JapanModel{" +
                "date=" + date +
                ", butane=" + butane +
                ", propane=" + propane +
                '}';
    }
}
