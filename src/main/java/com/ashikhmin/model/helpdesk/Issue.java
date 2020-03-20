package com.ashikhmin.model.helpdesk;

import com.ashikhmin.model.Actor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Issue {
    @Id
    @GeneratedValue
    private int id;
    private String topic;
    private Status status;

    @JsonIgnore
    @OneToMany(mappedBy = "issue")
    private List<Message> messages;

    public static Issue createIssue(){
        return new Issue();
    }

    private Issue() {
        messages = new ArrayList<>();
        participants = new HashSet<>();
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

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void openIssue() {
        if (Status.CLOSED == status)
            status = Status.REOPENED;
    }

    public Set<Actor> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Actor> participants) {
        this.participants = participants;
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
