package com.ashikhmin.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Authority {
    @Id
    @GeneratedValue
    private int id;
    private String authority;

    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_to_authority",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    Set<Actor> actors;
}
