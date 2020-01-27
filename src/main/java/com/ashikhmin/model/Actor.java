package com.ashikhmin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Actor {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String username;

    public Actor(String username) {
        this.username = username;
        favoriteFacilities = new HashSet<>();
    }

    @ManyToMany(mappedBy = "actors")
    private Set<Authority> authorities;

    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_favorite_facility",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facility> favoriteFacilities;

    public void like(Facility facility) {
        favoriteFacilities.add(facility);
    }

    public Set<Facility> getFavoriteFacilities() {
        return favoriteFacilities;
    }
}
