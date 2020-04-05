package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.helpdesk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class HelpDeskController {
    @Autowired
    ActorController actorController;

    @Autowired
    IssueRepo issueRepo;

    @Autowired
    MessageRepo messageRepo;

    @PostMapping("/help/issue")
    public Issue openIssue(@RequestBody Issue issue) {
        Actor me = actorController.getActor();
        issue.getParticipants().add(me);
        me.getIssues().add(issue);
        issue.openIssue();
        return issueRepo.save(issue);
    }

    @GetMapping("/help/issue/close/{id}")
    public Issue closeIssue(@PathVariable int id) {
        Issue issue = issueRepo.findById(id)
                .orElseThrow(IswebapiApplication.valueErrorSupplier("Non existent issue with id " + id));
        issue.closeIssue();
        return issueRepo.save(issue);
    }

    @PostMapping("/help/message/send")
    public MessageDTO sendMessage(@RequestBody MessageDTO dto) {
        Message newMsg = new Message(
                dto.getContent(),
                issueRepo.findById(dto.getIssueId())
                        .orElseThrow(IswebapiApplication.valueErrorSupplier(
                                "Invalid issue id provided"
                        )),
                actorController.getActor(),
                dto.getSendTime()
        );
        return new MessageDTO(messageRepo.save(newMsg), actorController.getActor());
    }

    @GetMapping("/help/issue/messages/{id}")
    public List<MessageDTO> getIssueMessages(@PathVariable(name = "id") int id) {
        List<MessageDTO> messageDTOs = new LinkedList<>();
        List<Message> messagesFromDb = issueRepo.findById(id).get().getMessages();
        for (Message m : messagesFromDb)
            messageDTOs.add(new MessageDTO(m, actorController.getActor()));
        return messageDTOs;
    }

    @GetMapping("/help/issue/new_messages/{id}/{last_check_timestamp}")
    public List<MessageDTO> getNewIssueMessages(
            @PathVariable(name = "id") int id,
            @PathVariable(name = "last_check_timestamp") long last_check_timestamp) {
        Timestamp last_check_time = new Timestamp(last_check_timestamp);
        List<MessageDTO> messageDTOs = new LinkedList<>();
        List<Message> messagesFromDb = messageRepo.
                findAllByIssue_IdAndSendTimeAfterOrderBySendTimeAsc(
                        id, last_check_time
                );
        for (Message m : messagesFromDb)
            messageDTOs.add(new MessageDTO(m, actorController.getActor()));
        return messageDTOs;
    }
}
