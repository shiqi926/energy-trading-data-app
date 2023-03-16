package com.example.olive.model;

public class NewZealandProductModel {
    private double total;
    private double crudeOilCondensatesNaphtha;
    private double lpg;
    private double gasoline; // petrol
    private double aviation;
    private double diesel;
    private double fuelOil;

    public NewZealandProductModel() {
    }

    public NewZealandProductModel(double total, double crudeOilCondensatesNaphtha,
                                  double lpg, double gasoline, double aviation, double diesel, double fuelOil) {
        this.total = total;
        this.crudeOilCondensatesNaphtha = crudeOilCondensatesNaphtha;
        this.lpg = lpg;
        this.gasoline = gasoline;
        this.aviation = aviation;
        this.diesel = diesel;
        this.fuelOil = fuelOil;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCrudeOilCondensatesNaphtha() {
        return crudeOilCondensatesNaphtha;
    }

    public void setCrudeOilCondensatesNaphtha(double crudeOilCondensatesNaphtha) {
        this.crudeOilCondensatesNaphtha = crudeOilCondensatesNaphtha;
    }

    public double getLpg() {
        return lpg;
    }

    public void setLpg(double lpg) {
        this.lpg = lpg;
    }

    public double getGasoline() {
        return gasoline;
    }

    public void setGasoline(double gasoline) {
        this.gasoline = gasoline;
    }

    public double getAviation() {
        return aviation;
    }

    public void setAviation(double aviation) {
        this.aviation = aviation;
    }

    public double getDiesel() {
        return diesel;
    }

    public void setDiesel(double diesel) {
        this.diesel = diesel;
    }

    public double getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(double fuelOil) {
        this.fuelOil = fuelOil;
    }

    public void calculateTotal() {
        setTotal(crudeOilCondensatesNaphtha + lpg + gasoline + aviation + diesel + fuelOil);
    }

    @Override
    public String toString() {
        return "Product{" +
                "total=" + total +
                ", crudeOilCondensatesNaphtha=" + crudeOilCondensatesNaphtha +
                ", lpg=" + lpg +
                ", gasoline=" + gasoline +
                ", aviation=" + aviation +
                ", diesel=" + diesel +
                ", fuelOil=" + fuelOil +
                '}';
    }

}
