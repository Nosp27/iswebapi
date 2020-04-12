package com.ashikhmin.model;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ChangelogRepo extends CrudRepository<Changelog, Integer> {
    Iterable<Changelog> findAllByUpdatedAtAfter(Timestamp timestamp);
}
