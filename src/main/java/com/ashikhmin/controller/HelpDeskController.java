package com.ashikhmin.controller;

import com.ashikhmin.model.helpdesk.Issue;
import com.ashikhmin.model.helpdesk.IssueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelpDeskController {
    @Autowired
    IssueRepo issueRepo;

    @GetMapping("")
    public void home(){}
}
