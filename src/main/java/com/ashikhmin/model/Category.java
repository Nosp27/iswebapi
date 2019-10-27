package com.ashikhmin.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Category {
    @Id
    private String catName;

    @ManyToMany(mappedBy = "categories")
    Set<Facility> facilities;
}
