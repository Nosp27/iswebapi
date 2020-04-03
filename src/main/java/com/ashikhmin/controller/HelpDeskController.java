package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.helpdesk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                actorController.getActor()
        );
        return new MessageDTO(messageRepo.save(newMsg));
    }

    @GetMapping("/help/issue/messages/{id}")
    public List<MessageDTO> getIssueMessages(@PathVariable(name = "id") int id) {
        List<MessageDTO> messageDTOs = new LinkedList<>();
        List<Message> messagesFromDb = issueRepo.findById(id).get().getMessages();
        for (Message m : messagesFromDb)
            messageDTOs.add(new MessageDTO(m));
        return messageDTOs;
    }
}
