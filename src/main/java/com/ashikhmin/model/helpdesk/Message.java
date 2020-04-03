package com.ashikhmin.model.helpdesk;

import com.ashikhmin.model.Actor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue
    int id;

    String content;

    public Message() {

    }

    public Message(String content, Issue issue, Actor sender) {
        this.content = content;
        this.issue = issue;
        this.actor = sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "actor_id")
    Actor actor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "issue_id")
    Issue issue;
}
