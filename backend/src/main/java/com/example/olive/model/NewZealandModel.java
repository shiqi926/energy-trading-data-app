package com.example.olive.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Document(collection = "NewZealand")
public class NewZealandModel {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @Indexed
    private NewZealandProductModel imports = new NewZealandProductModel();
    private NewZealandProductModel exports = new NewZealandProductModel();
    private NewZealandProductModel demand = new NewZealandProductModel();
    private NewZealandProductModel supply = new NewZealandProductModel();
    private NewZealandProductModel grossBalance = new NewZealandProductModel();
    private NewZealandProductModel netBalance = new NewZealandProductModel();
    private NewZealandProductModel productYield = new NewZealandProductModel();
    private NewZealandProductModel netImports = new NewZealandProductModel();

    public NewZealandModel() {
    }

    public NewZealandModel(Date date) {
        this.date = date;
    }

    public NewZealandModel(Date date, NewZealandProductModel imports, NewZealandProductModel exports, NewZealandProductModel demand, NewZealandProductModel supply,
                           NewZealandProductModel grossBalance, NewZealandProductModel netBalance, NewZealandProductModel productYield, NewZealandProductModel netImports) {
        this.date = date;
        this.imports = imports;
        this.exports = exports;
        this.demand = demand;
        this.supply = supply;
        this.grossBalance = grossBalance;
        this.netBalance = netBalance;
        this.productYield = productYield;
        this.netImports = netImports;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public NewZealandProductModel getImports() {
        return imports;
    }

    public void setImports(NewZealandProductModel imports) {
        this.imports = imports;
    }

    public NewZealandProductModel getExports() {
        return exports;
    }

    public void setExports(NewZealandProductModel exports) {
        this.exports = exports;
    }

    public NewZealandProductModel getDemand() {
        return demand;
    }

    public void setDemand(NewZealandProductModel demand) {
        this.demand = demand;
    }

    public NewZealandProductModel getSupply() {
        return supply;
    }

    public void setSupply(NewZealandProductModel supply) {
        this.supply = supply;
    }

    public NewZealandProductModel getGrossBalance() {
        return grossBalance;
    }

    public void setGrossBalance(NewZealandProductModel grossBalance) {
        this.grossBalance = grossBalance;
    }

    public NewZealandProductModel getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(NewZealandProductModel netBalance) {
        this.netBalance = netBalance;
    }

    public NewZealandProductModel getProductYield() {
        return productYield;
    }

    public void setProductYield(NewZealandProductModel productYield) {
        this.productYield = productYield;
    }

    public NewZealandProductModel getNetImports() {
        return netImports;
    }

    public void setNetImports(NewZealandProductModel netImports) {
        this.netImports = netImports;
    }

    public void calculateNetImportsTotal() {
        netImports.setCrudeOilCondensatesNaphtha(exports.getCrudeOilCondensatesNaphtha() - imports.getCrudeOilCondensatesNaphtha());
        netImports.setLpg(exports.getLpg() - imports.getLpg());
        netImports.setGasoline(exports.getGasoline() - imports.getGasoline());
        netImports.setAviation(exports.getAviation() - imports.getAviation());
        netImports.setDiesel(exports.getDiesel() - imports.getDiesel());
        netImports.setFuelOil(exports.getFuelOil() - imports.getFuelOil());
        netImports.setTotal(exports.getTotal() - imports.getTotal());
    }

    public void calculateGrossBalanceTotal() {
        grossBalance.setCrudeOilCondensatesNaphtha(supply.getCrudeOilCondensatesNaphtha() - demand.getCrudeOilCondensatesNaphtha());
        grossBalance.setLpg(supply.getLpg() - demand.getLpg());
        grossBalance.setGasoline(supply.getGasoline() - demand.getGasoline());
        grossBalance.setAviation(supply.getAviation() - demand.getAviation());
        grossBalance.setDiesel(supply.getDiesel() - demand.getDiesel());
        grossBalance.setFuelOil(supply.getFuelOil() - demand.getFuelOil());
        grossBalance.setTotal(supply.getTotal() - demand.getTotal());
    }

    public void calculateNetBalanceTotal() {
        netBalance.setCrudeOilCondensatesNaphtha(grossBalance.getCrudeOilCondensatesNaphtha() + netImports.getCrudeOilCondensatesNaphtha());
        netBalance.setLpg(grossBalance.getLpg() + netImports.getLpg());
        netBalance.setGasoline(grossBalance.getGasoline() + netImports.getGasoline());
        netBalance.setAviation(grossBalance.getAviation() + netImports.getAviation());
        netBalance.setDiesel(grossBalance.getDiesel() + netImports.getDiesel());
        netBalance.setFuelOil(grossBalance.getFuelOil() + netImports.getFuelOil());
        netBalance.setTotal(grossBalance.getTotal() + netImports.getTotal());
    }

    public void calculateProductYieldTotal() {
        double refineryIntake = demand.getCrudeOilCondensatesNaphtha();

        productYield.setCrudeOilCondensatesNaphtha(supply.getCrudeOilCondensatesNaphtha() / refineryIntake);
        productYield.setLpg(supply.getLpg() / refineryIntake);
        productYield.setGasoline(supply.getGasoline() / refineryIntake);
        productYield.setAviation(supply.getAviation() / refineryIntake);
        productYield.setDiesel(supply.getDiesel() / refineryIntake);
        productYield.setFuelOil(supply.getFuelOil() / refineryIntake);
        productYield.setTotal(supply.getTotal() / refineryIntake);
    }

    @Override
    public String toString() {
        return "NewZealandModel{" +
                "date=" + date +
                ", imports=" + imports +
                ", exports=" + exports +
                ", demand=" + demand +
                ", supply=" + supply +
                '}';
    }
}
