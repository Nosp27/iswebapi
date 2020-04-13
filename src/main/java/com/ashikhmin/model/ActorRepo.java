package com.ashikhmin.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ActorRepo extends CrudRepository<Actor, Integer> {
    Actor findByUsername(String username);
    Set<Actor> findByFavoriteFacilitiesContaining(Facility favorite);
    Set<Actor> findAllByType(String type);
}
