package com.ashikhmin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;

import javax.persistence.Id;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Facility implements HasImage {
    @Id
    @GeneratedValue
    private Integer _id;
    private String name;
    private String description;

    @Column(columnDefinition = "double precision not null default 0")
    private Double lat;

    @Column(columnDefinition = "double precision not null default 0")
    private Double lng;
    private Integer imageId;

    public Facility() {
        _id = 0;
        lat = 0.0;
        lng = 0.0;
    }

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
}
