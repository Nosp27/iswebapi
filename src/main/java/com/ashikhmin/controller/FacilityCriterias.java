package com.ashikhmin.controller;

import java.util.List;

public class FacilityCriterias {
    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Integer> getRegions() {
        return regions;
    }

    public void setRegions(List<Integer> regions) {
        this.regions = regions;
    }

    private List<String> categories;
    private List<Integer> regions;
}
