package com.ashikhmin.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface FacilityRepo extends CrudRepository<Facility, Integer> {
    Iterable<Facility> getAllByCategoriesIsInAndRegionIn(Set<Category> cats, Set<Region> regions);
    Iterable<Facility> getAllByRegionIn(Set<Region> regions);
    Iterable<Facility> getAllByCategoriesIn(Set<Category> categories);
}
