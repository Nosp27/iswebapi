package com.ashikhmin.model.helpdesk;

import com.ashikhmin.model.Actor;

import javax.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue
    int id;

    String content;

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

    @ManyToOne
    @JoinColumn(name = "actor_id")
    Actor actor;

    @ManyToOne
    @JoinColumn(name = "issue_id", insertable = false, updatable = false)
    Issue issue;
}
