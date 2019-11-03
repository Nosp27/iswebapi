package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Region;
import com.ashikhmin.model.RegionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegionController {
    @Autowired
    RegionRepo regionRepo;

    @GetMapping(path = "/region/{id}")
    Region getRegion(@PathVariable("id") int id) {
        return regionRepo.findById(id).orElseThrow(IswebapiApplication.valueError("No region with id " + id));
    }

    @PostMapping(path = "/region")
    Region addRegion(@RequestBody Region region) {
        return regionRepo.save(region);
    }

    @PutMapping(path = "/region")
    Region updateRegion(@RequestBody Region region) {
        if(regionRepo.existsById(region.getRegionId()))
            return regionRepo.save(region);

        throw IswebapiApplication.valueError("No such region").get();
    }
}
