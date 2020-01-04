package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.Region;
import com.ashikhmin.model.RegionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class RegionController {
    @Autowired
    RegionRepo regionRepo;

    @GetMapping(path = "/region/{id}")
    Region getRegion(@PathVariable("id") int id) {
        return regionRepo.findById(id).orElseThrow(IswebapiApplication.valueError("No region with id " + id));
    }

    @GetMapping(path = "/regions")
    Iterable<Region> listRegions() {
        return regionRepo.findAll();
    }

    @PostMapping(path = "/region")
    Region addRegion(@RequestBody Region region) {
        return regionRepo.save(region);
    }

    @PutMapping(path = "/region")
    Region updateRegion(@RequestBody Region region) {
        Region r = regionRepo.findById(region.getRegionId()).orElseThrow(IswebapiApplication.valueError("No such region"));
        r.setRegionName(region.getRegionName());
        return regionRepo.save(r);
    }

    @DeleteMapping("/region/{regionId}")
    Region deleteRegion(@PathVariable(name = "regionId") Integer regionId) {
        Region region =
                regionRepo.findById(regionId)
                        .orElseThrow(IswebapiApplication.valueError("No region with id " + regionId));
        regionRepo.delete(region);
        return region;
    }
}
