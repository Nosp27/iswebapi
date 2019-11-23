package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.helpdesk.Issue;
import com.ashikhmin.model.helpdesk.IssueRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("LawOfDemeter")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class HelpDeskControllerTest {
    @Autowired
    IssueRepo issueRepo;

    @Autowired
    MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    void openIssue() throws Exception {
        Issue issue = Issue.createIssue();
        issue.setTopic("Test issue");
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/help/issue")
                        .content(mapper.writeValueAsString(issue))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(issue.getTopic())));
        Assert.assertTrue(issueRepo.findById(issue.getId()).isPresent());
    }

    @Test
    void closeIssue() {
    }

    @Test
    void sendMessage() {
    }
}