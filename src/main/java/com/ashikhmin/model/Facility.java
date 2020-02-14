package com.ashikhmin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;

import javax.persistence.Id;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Facility implements HasImage {
    @Id
    @GeneratedValue
    private Integer _id;
    private String name;
    private String description;
    private Double lat;
    private Double lng;
    private Integer imageId;
    private String utility = "Unavailable";
    private Integer employees;
    private Double investmentSize;
    private Double profitability;

    public Facility() {
        _id = 0;
        lat = 0.0;
        lng = 0.0;
    }

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "facility_to_category",
            joinColumns = @JoinColumn(name = "_id"),
            inverseJoinColumns = @JoinColumn(name = "cat_id"))
    private Set<Category> categories;

    @ManyToOne
    @JoinTable(
            name = "facility_to_region",
            joinColumns = @JoinColumn(name = "_id"),
            inverseJoinColumns = @JoinColumn(name = "regionId"))
    private Region region;

    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteFacilities")
    private Set<Actor> subscribedActors;

    public Set<Actor> getSubscribedActors() {
        if (subscribedActors == null)
            subscribedActors = new HashSet<>();
        return subscribedActors;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(getClass()))
            return false;
        return get_id().equals(((Facility) obj).get_id());
    }

    @Override
    public int hashCode() {
        return get_id().hashCode();
    }

    //<editor-fold desc="Accessors">
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(int id) {
        _id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getUtility() {
        return utility;
    }


    private static final List<String> POSSIBLE_UTILITIES = Arrays.asList(
            "Available",
            "Unavailable",
            "Under Construction"
    );

    public void setUtility(String utility) {
        if (!POSSIBLE_UTILITIES.contains(utility))
            throw new IllegalArgumentException(
                    String.format(
                            "Unavailable value for utility: %s." +
                                    "available values: %s",
                            utility,
                            Arrays.toString(POSSIBLE_UTILITIES.toArray())
                    )
            );
        this.utility = utility;
    }

    public Integer getEmployees() {
        return employees;
    }

    public void setEmployees(Integer employees) {
        this.employees = employees;
    }

    public Double getInvestmentSize() {
        return investmentSize;
    }

    public void setInvestmentSize(Double investmentSize) {
        this.investmentSize = investmentSize;
    }

    public Double getProfitability() {
        return profitability;
    }

    public void setProfitability(Double profitability) {
        this.profitability = profitability;
    }
    //</editor-fold>
}
