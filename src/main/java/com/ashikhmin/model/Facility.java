package com.ashikhmin.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.Id;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Facility implements HasImage {
    @Id
    @GeneratedValue
    private int _id;
    private String name;
    private String description;
    private Double lat;
    private Double lng;
    private Integer imageId;

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

    public int get_id() {
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

    public void setCoordinates(double lat, double lng){
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

    public Double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
