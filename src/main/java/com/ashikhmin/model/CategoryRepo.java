package com.ashikhmin.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CategoryRepo extends CrudRepository<Category, Integer> {
    Set<Category> findAllByCatIdIn(List<Integer> catIds);
}
