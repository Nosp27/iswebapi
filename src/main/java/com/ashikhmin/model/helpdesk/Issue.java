package com.ashikhmin.model.helpdesk;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Issue {
    @Id
    int id;
    String topic;

    @OneToMany(mappedBy = "issue")
    List<Message> messages;
}
