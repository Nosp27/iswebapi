package com.ashikhmin.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface
FacilityRepo extends CrudRepository<Facility, Integer> {

    @Query(value =
            "select * " +
            "from facility f " +
            "join changelog on changelog.facility_id=f._id " +
            "where changelog.updated_at > (now() - interval '1 day')" +
            "and f._id in (?1)",
            nativeQuery = true)
    Iterable<Facility> getChangelog(Iterable<Integer> ids);

    Iterable<Facility> getAllBySubscribedActorsContaining(Actor actor);
    Iterable<Facility> getAllByCategoriesIsInAndRegionIn(Set<Category> cats, Set<Region> regions);
    Iterable<Facility> getAllByRegionIn(Set<Region> regions);
    Iterable<Facility> getAllByCategoriesIn(Set<Category> categories);
}
