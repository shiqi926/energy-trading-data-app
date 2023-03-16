package com.example.olive.model;

public class JapanProductModel {
    private double imports;
    private double supplyMeter;
    private double exports;
    private double shipmentTotal;
    private double supply;
    private double demand;


    private double grossBalance;

    public JapanProductModel() {
    }

    public JapanProductModel(double imports, double supplyMeter, double exports, double shipmentTotal, double supply, double demand, double grossBalance) {
        this.imports = imports;
        this.supplyMeter = supplyMeter;
        this.exports = exports;
        this.shipmentTotal = shipmentTotal;
        this.supply = supply;
        this.demand = demand;
        this.grossBalance = grossBalance;
    }

    public double getImports() {
        return imports;
    }

    public void setImports(double imports) {
        this.imports = imports;
    }

    public double getSupplyMeter() {
        return supplyMeter;
    }

    public void setSupplyMeter(double supplyMeter) {
        this.supplyMeter = supplyMeter;
    }

    public double getExports() {
        return exports;
    }

    public void setExports(double exports) {
        this.exports = exports;
    }

    public double getShipmentTotal() {
        return shipmentTotal;
    }

    public void setShipmentTotal(double shipmentTotal) {
        this.shipmentTotal = shipmentTotal;
    }

    public double getSupply() {
        return supply;
    }

    public void setSupply(double supply) {
        this.supply = supply;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public double getGrossBalance() {
        return grossBalance;
    }

    public void setGrossBalance(double grossBalance) {
        this.grossBalance = grossBalance;
    }

    @Override
    public String toString() {
        return "JapanProductModel{" +
                "imports=" + imports +
                ", supplyMeter=" + supplyMeter +
                ", exports=" + exports +
                ", shipmentTotal=" + shipmentTotal +
                '}';
    }
}
