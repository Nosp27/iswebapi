package com.ashikhmin.controller;

import java.util.List;

public class FacilityCriterias {
    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public List<Integer> getRegions() {
        return regions;
    }

    public void setRegions(List<Integer> regions) {
        this.regions = regions;
    }

    private List<Integer> categories;
    private List<Integer> regions;
}
