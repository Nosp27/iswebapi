package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RestController
public class FacilityController {
    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private RegionRepo regionRepo;

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
        if(facilityRepo.existsById(facility.get_id()))
            throw IswebapiApplication.valueError("Id exists. Trying to repeat primary key").get();
        return facilityRepo.save(facility);
    }

    @PutMapping(path = "/facility")
    Facility updateFacility(@RequestBody Facility facility) {
        if (facilityRepo.findById(facility.get_id()).isPresent())
            return facilityRepo.save(facility);

        throw IswebapiApplication.valueError("No facility with id " + facility.get_id()).get();
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
