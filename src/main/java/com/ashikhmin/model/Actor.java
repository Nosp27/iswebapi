package com.ashikhmin.model;

import com.ashikhmin.model.helpdesk.Issue;
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

    public Actor() {
        
    }

    public Actor(String username) {
        this.username = username;
        favoriteFacilities = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_favorite_facility",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facility> favoriteFacilities;

    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_issue",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id"))
    private Set<Issue> issues;

    public Boolean like(Facility facility) {
        if (favoriteFacilities.contains(facility)) {
            favoriteFacilities.remove(facility);
            return false;
        } else {
            favoriteFacilities.add(facility);
            return true;
        }
    }

    public Set<Facility> getFavoriteFacilities() {
        return favoriteFacilities;
    }
}
