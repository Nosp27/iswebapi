package com.ashikhmin.controller;

import com.ashikhmin.model.Category;
import com.ashikhmin.model.Region;

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
