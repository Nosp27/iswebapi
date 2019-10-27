package com.ashikhmin.controller;

import com.ashikhmin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
public class ISAppController {
    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private RegionRepo regionRepo;

    @PostMapping(path="/facilities")
    Iterable<Facility> getFacilities(@RequestBody FacilityCriterias criterias) {
        Set<Category> cats = categoryRepo.findAllByCatNameIn(criterias.getCategories());
        Set<Region> regions = regionRepo.getAllByRegionIdIn(criterias.getRegions());
        return null;
    }

    @GetMapping(path="/")
    Facility ping() {
        return new Facility();
    }

    @GetMapping(path="/all")
    Iterable<Facility> all() {
        return facilityRepo.findAll();
    }

    @PostMapping(path="/facility")
    Facility addFacility(@RequestBody Facility facility) {
        return facilityRepo.save(facility);
    }
}
