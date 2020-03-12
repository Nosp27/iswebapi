package com.ashikhmin.model.helpdesk;

import com.ashikhmin.model.Actor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@Entity
public class Issue {
    @Id
    private int id;
    private String topic;
    private Status status;

    @OneToMany(mappedBy = "issue")
    List<Message> messages;

    public static Issue createIssue(){
        return new Issue();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public Status getStatus() {
        return status;
    }

    public void openIssue() {
        if (Status.CLOSED == status)
            status = Status.REOPENED;
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "issues")
    Set<Actor> participants;

    public void closeIssue() {
        status = Status.CLOSED;
    }

    public enum Status {
        OPENED,
        CLOSED,
        REOPENED,
    }
}
