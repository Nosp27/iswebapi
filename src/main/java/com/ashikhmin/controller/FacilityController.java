package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@RestController
public class FacilityController {
    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private RegionRepo regionRepo;

    @GetMapping(path = "/facilities")
    Iterable<Facility> getAllFacilities() {
        return facilityRepo.findAll();
    }

    @PostMapping(path = "/facilities")
    Iterable<Facility> getFacilities(@RequestBody FacilityCriterias criterias) {
        Set<Category> cats;
        Set<Region> regions;
        boolean emptyRegions = isEmpty(criterias.getRegions());
        boolean emptyCategories = isEmpty(criterias.getCategories());
        if (emptyCategories && emptyRegions) {
            return facilityRepo.findAll();
        }
        if (emptyCategories) {
            cats = new HashSet<>();
            categoryRepo.findAll().iterator().forEachRemaining(cats::add);
        } else cats = categoryRepo.findAllByCatNameIn(criterias.getCategories());
        if (emptyRegions) {
            regions = new HashSet<>();
            regionRepo.findAll().iterator().forEachRemaining(regions::add);
        } else regions = regionRepo.getAllByRegionIdIn(criterias.getRegions());

        return facilityRepo.getAllByCategoriesIsInAndRegionIn(cats, regions);
    }

    private <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    @PostMapping(path = "/facility")
    Facility addFacility(@RequestBody Facility facility) {
        if (facilityRepo.existsById(facility.get_id()))
            throw IswebapiApplication.valueError("Id exists. Trying to repeat primary key").get();
        return facilityRepo.save(facility);
    }

    @PutMapping(path = "/facility")
    Facility updateFacility(@RequestBody Facility facility) {
        Supplier<? extends RuntimeException> exceptionSupplier =
                IswebapiApplication.valueError("No facility with id " + facility.get_id());
        Facility dbFacility = facilityRepo.findById(facility.get_id()).orElseThrow(exceptionSupplier);
        dbFacility.setName(facility.getName());
        dbFacility.setDescription(facility.getDescription());
        dbFacility.setLat(facility.getLat());
        dbFacility.setLng(facility.getLng());
        dbFacility.setRegion(regionRepo.findById(facility.getRegion().getRegionId()).orElseThrow(exceptionSupplier));
        dbFacility.setCategories(facility.getCategories());
        return facilityRepo.save(dbFacility);
    }

    @DeleteMapping(path = "/facility/{facilityId}")
    Facility deleteFacility(@PathVariable int facilityId) {
        Facility facility =
                facilityRepo.findById(facilityId)
                        .orElseThrow(IswebapiApplication.valueError("No facility with id " + facilityId));
        facilityRepo.delete(facility);
        return facility;
    }
}
