package com.example.olive.model;


public class JodiOilModel {

    private double lpg;
    private double gasoline;
    private double jetFuel;
    private double diesel;
    private double fuelOil;

    public JodiOilModel() {
    }

    public JodiOilModel(double lpg, double gasoline, double jetFuel, double diesel, double fuelOil) {
        this.lpg = lpg;
        this.gasoline = gasoline;
        this.jetFuel = jetFuel;
        this.diesel = diesel;
        this.fuelOil = fuelOil;
    }

    public double getLpg() {
        return lpg;
    }

    public void setLpg(Double lpg) {
        this.lpg = lpg;
    }

    public double getGasoline() {
        return gasoline;
    }

    public void setGasoline(Double gasoline) {
        this.gasoline = gasoline;
    }

    public double getJetFuel() {
        return jetFuel;
    }

    public void setJetFuel(Double jetFuel) {
        this.jetFuel = jetFuel;
    }

    public double getDiesel() {
        return diesel;
    }

    public void setDiesel(Double diesel) {
        this.diesel = diesel;
    }

    public double getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(Double fuelOil) {
        this.fuelOil = fuelOil;
    }
}
