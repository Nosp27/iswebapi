package com.ashikhmin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Region implements HasImage {
    @Id
    @GeneratedValue
    private int regionId;
    private String regionName;
    private Integer imageId;
    private Double area;
    private Integer population;
    private Integer unemployed;
    private Integer totalLabourForce;
    private Double gdp;
    private Double avgPropertyPrice;
    private Double avgFamilyIncome;

    // <editor-fold desc = "Accessors">
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getUnemployed() {
        return unemployed;
    }

    public void setUnemployed(Integer unemployed) {
        this.unemployed = unemployed;
    }

    public Integer getTotalLabourForce() {
        return totalLabourForce;
    }

    public void setTotalLabourForce(Integer totalLabourForce) {
        this.totalLabourForce = totalLabourForce;
    }

    public Double getGdp() {
        return gdp;
    }

    public void setGdp(Double gdp) {
        this.gdp = gdp;
    }

    public Double getAvgPropertyPrice() {
        return avgPropertyPrice;
    }

    public void setAvgPropertyPrice(Double avgPropertyPrice) {
        this.avgPropertyPrice = avgPropertyPrice;
    }

    public Double getAvgFamilyIncome() {
        return avgFamilyIncome;
    }

    public void setAvgFamilyIncome(Double avgFamilyIncome) {
        this.avgFamilyIncome = avgFamilyIncome;
    }
    // </editor-fold>
}
