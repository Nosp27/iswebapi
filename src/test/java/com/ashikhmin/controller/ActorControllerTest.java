package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.ActorRepo;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.FacilityRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    }

    @WithMockUser
    @Transactional
    @Test
    void likeFacility() throws Exception {
        Actor actor = new Actor(Long.toHexString(System.currentTimeMillis()));
        String mappedActor = mapper.writer().writeValueAsString(actor);

        Facility facility = new Facility();
        facility.setName("Some facility name");
        String mappedFacility = mapper.writer().writeValueAsString(facility);

        MockHttpServletResponse result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        String id = mapper.reader()
                .readTree(result.getContentAsString()).findValue("_id").asText();

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/actor/like/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedFacility)

        )
                .andExpect(MockMvcResultMatchers.status().isOk());

        facility = facilityRepo.findById(Integer.parseInt(id)).get();
        actor = actorRepo.findByUsername("user");
        Assert.assertTrue(facility.getSubscribedActors().contains(actor));
    }

}