package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.helpdesk.Issue;
import com.ashikhmin.model.helpdesk.IssueRepo;
import com.ashikhmin.model.helpdesk.Message;
import com.ashikhmin.model.helpdesk.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Message sendMessage(@RequestBody Message message) {
        if (message.getIssue() == null) {
            message.setIssue(issueRepo.findById(message.getIssueId())
                    .orElseThrow(IswebapiApplication.valueErrorSupplier("Message issue not found!")));
        }
        message.setActor(actorController.getActor());
        return messageRepo.save(message);
    }

    @GetMapping("/help/issue/messages/{id}")
    public List<Message> getIssueMessages(@PathVariable(name = "id") int id) {
        return issueRepo.findById(id).get().getMessages();
    }
}
