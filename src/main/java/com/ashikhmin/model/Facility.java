package com.ashikhmin.model;

import org.springframework.context.annotation.Primary;
import javax.persistence.Id;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facility")
public class Facility {
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Id
    private int _id;
    private String description;
    private double[] coordinates;

    public Facility() {
        _id = -1;
        description = "asd";
        coordinates = new double[]{0.0, 0.0};
        categories = new HashSet<>(3);
        region = new Region();
    }

    @ManyToMany
    @JoinTable(
            name = "facility_to_category",
            joinColumns = @JoinColumn(name = "catName"),
            inverseJoinColumns = @JoinColumn(name = "_id"))
    private Set<Category> categories;

    @ManyToOne
    @JoinTable(
            name = "facility_to_region",
            joinColumns = @JoinColumn(name = "regionId"),
            inverseJoinColumns = @JoinColumn(name = "_id"))
    private Region region;
}
