package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Region;
import com.ashikhmin.model.RegionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class RegionController {
    @Autowired
    SecurityController securityController;

    @Autowired
    RegionRepo regionRepo;

    @GetMapping(path = "/region/{id}")
    Region getRegion(@PathVariable("id") int id) {
        return regionRepo.findById(id).orElseThrow(IswebapiApplication.valueErrorSupplier("No region with id " + id));
    }

    @GetMapping(path = "/regions")
    Iterable<Region> listRegions() {
        return regionRepo.findAll();
    }

    @PostMapping(path = "/region")
    Region addRegion(@RequestBody Region region) {
        securityController.ensureManagerPermission();
        return regionRepo.save(region);
    }

    @PutMapping(path = "/region")
    Region updateRegion(@RequestBody Region region) {
        securityController.ensureManagerPermission();
        Region r = regionRepo.findById(region.getRegionId()).orElseThrow(IswebapiApplication.valueErrorSupplier("No such region"));
        r.setRegionName(region.getRegionName());
        r.setArea(region.getArea());
        r.setPopulation(region.getPopulation());
        r.setUnemployed(region.getUnemployed());
        r.setTotalLabourForce(region.getTotalLabourForce());
        r.setGdp(region.getGdp());
        r.setAvgPropertyPrice(region.getAvgPropertyPrice());
        r.setAvgFamilyIncome(region.getAvgFamilyIncome());
        return regionRepo.save(r);
    }

    @DeleteMapping("/region/{regionId}")
    Region deleteRegion(@PathVariable(name = "regionId") Integer regionId) {
        securityController.ensureManagerPermission();
        Region region =
                regionRepo.findById(regionId)
                        .orElseThrow(IswebapiApplication.valueErrorSupplier("No region with id " + regionId));
        regionRepo.delete(region);
        return region;
    }
}
