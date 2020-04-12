package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class RegionControllerTest {
    @Autowired
    SecurityController securityController;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    RegionRepo regionRepo;

    MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        //Init MockMvc Object
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        securityController.setTestingMode();
    }

    Region createRegion(String regionName, boolean save) {
        Region tempRegion = new Region();
        tempRegion.setRegionName(regionName);
        return save ? regionRepo.save(tempRegion) : tempRegion;
    }

    @Test
    @Transactional
    void testAddRegion() throws Exception {
        String regionName = "somregionname";
        Region testRegion = createRegion(regionName, false);
        long databaseSize = regionRepo.count();
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/region")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(testRegion)))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(regionName)));

        Assert.assertEquals(databaseSize + 1L, regionRepo.count());
    }

    @Test
    @Transactional
    void testGetRegion() throws Exception {
        String regionName = "someregion";
        Region tempRegion = createRegion(regionName, true);
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/region" + "/" + tempRegion.getRegionId())
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(regionName)));

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/regions/")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(regionName)));
    }

    @Test
    @Transactional
    void testUpdateRegion() throws Exception {
        String regionName = "somecat";
        String changedName = "somenewname";
        Region tempRegion = createRegion(regionName, true);
        Region changedRegion = createRegion(changedName, false);
        changedRegion.setRegionId(tempRegion.getRegionId());

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/region")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(changedRegion)))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(changedName)));
    }

    @Test
    @Transactional
    void testDeleteRegion() throws Exception {
        Region tempRegion = createRegion("somenonexistentregion" +
                "", true);
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/region" +
                                "/" + tempRegion.getRegionId())
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(tempRegion.getRegionName())));

        Assert.assertFalse(regionRepo.existsById(tempRegion.getRegionId()));
    }
}