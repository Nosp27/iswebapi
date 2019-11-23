package com.ashikhmin.model;

import com.ashikhmin.model.helpdesk.Message;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Actor {
    @Id
    @GeneratedValue
    int id;

    long authToken;
    String username;

    @OneToMany(mappedBy = "actor")
    Collection<Message> messages;
}
