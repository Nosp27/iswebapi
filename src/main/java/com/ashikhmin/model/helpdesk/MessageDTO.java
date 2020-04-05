package com.ashikhmin.model.helpdesk;

import com.ashikhmin.model.Actor;

public class MessageDTO {
    private int id;
    private String content;
    private int issueId;
    private boolean mine;
    private long sendTime;

    public MessageDTO() {

    }

    public MessageDTO(Message message, Actor requester) {
        id = message.id;
        content = message.content;
        issueId = message.getIssue().getId();
        mine = message.getActor().getId() == requester.getId();
        sendTime = message.getSendTime().getTime();
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

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}
