package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.ActorRepo;
import com.ashikhmin.model.helpdesk.Issue;
import com.ashikhmin.model.helpdesk.IssueRepo;
import com.ashikhmin.model.helpdesk.Message;
import com.ashikhmin.model.helpdesk.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class HelpDeskController {
    @Autowired
    IssueRepo issueRepo;

    @Autowired
    MessageRepo messageRepo;

    @PostMapping("/help/issue")
    public Issue openIssue(@RequestBody Issue issue) {
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
            Issue newIssue = openIssue(Issue.createIssue());
            message.setIssue(newIssue);
        }
        return messageRepo.save(message);
    }
}
