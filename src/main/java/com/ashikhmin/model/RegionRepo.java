package com.ashikhmin.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface RegionRepo extends CrudRepository<Region, Integer> {
    Set<Region> getAllByRegionIdIn(List<Integer> ids);
}
