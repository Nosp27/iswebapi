package com.ashikhmin.model.helpdesk;

public class MessageDTO {
    private int id;
    private String content;
    private int issueId;
    private int actorId;

    public MessageDTO() {

    }

    public MessageDTO(Message message) {
        id = message.id;
        content = message.content;
        issueId = message.getIssue().getId();
        actorId = message.getActor().getId();
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

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }
}
