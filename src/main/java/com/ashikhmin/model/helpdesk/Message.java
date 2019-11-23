package com.ashikhmin.model.helpdesk;

import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue
    int id;

    String content;

    @ManyToOne
    @JoinColumn(name = "id")
    Issue issue;
}
