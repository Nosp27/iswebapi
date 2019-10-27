package com.ashikhmin.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface FacilityRepo extends CrudRepository<Facility, Integer> {
    Iterable<Facility> getAllByCategoriesIsInAndRegionIn(Set<Category> category, Set<Region> region);
}
