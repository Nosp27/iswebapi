package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.ActorRepo;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.FacilityRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
public class ActorControllerTest {
    @Autowired
    SecurityController securityController;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ActorRepo actorRepo;

    @Autowired
    FacilityRepo facilityRepo;

    MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        //Init MockMvc Object
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        securityController.setTestingMode();
    }

    MockHttpServletResponse postNewFacility() throws Exception {
        Facility facility = new Facility();
        facility.setName("Some facility name");
        String mappedFacility = mapper.writer().writeValueAsString(facility);

        return mvc.perform(
                MockMvcRequestBuilders
                        .post("/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedFacility))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }

    @WithMockUser
    @Transactional
    @Test
    void getActor() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.get("/actor/me"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("user")));
    }

    @WithMockUser
    @Transactional
    @Test
    void likeFacility() throws Exception {
        MockHttpServletResponse newFacility = postNewFacility();

        // Like new facility
        String id = mapper.reader()
                .readTree(newFacility.getContentAsString()).findValue("_id").asText();
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/actor/like/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(Matchers.containsString("\"liked\":true")));

        // Unlike new facility (like again)
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/actor/like/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(Matchers.containsString("\"liked\":false")));
    }

    @WithMockUser
    @Transactional
    @Test
    void favoriteFacilities() throws Exception {
        MockHttpServletResponse newFacility = postNewFacility();

        String _id = mapper.reader()
                .readTree(newFacility.getContentAsString()).findValue("_id").asText();
        String name = mapper.reader()
                .readTree(newFacility.getContentAsString()).findValue("name").asText();

        // Test no favorites
        mvc.perform(
                MockMvcRequestBuilders.get("/actor/favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(Matchers.not(Matchers.containsString("\"_id\":" + _id))))
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(Matchers.not(Matchers.containsString(name))));

        // Like new facility
        String id = mapper.reader()
                .readTree(newFacility.getContentAsString()).findValue("_id").asText();
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/actor/like/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(Matchers.containsString("\"liked\":true")));

        // Test there are favorites
        mvc.perform(
                MockMvcRequestBuilders.get("/actor/favorites")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.containsString("\"_id\":" + _id)))
                .andExpect(MockMvcResultMatchers.content()
                        .string(Matchers.containsString(name)));
    }
}