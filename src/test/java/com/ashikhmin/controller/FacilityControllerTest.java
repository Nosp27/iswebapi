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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class FacilityControllerTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    RegionRepo regionRepo;

    @Autowired
    CategoryRepo categoryRepo;

    MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        //Init MockMvc Object
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    @Test
    void testCreateFacility() throws Exception {
        long initialCount = facilityRepo.count();
        Facility f = new Facility();
        f.setDescription("Test facility description");
        f.setName("Test Facility");
        Assert.assertFalse(facilityRepo.findById(f.get_id()).isPresent());
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facility")
                        .content(mapper.writeValueAsString(f))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f.getName())));
        Assert.assertEquals(facilityRepo.count(), initialCount + 1L);
    }

    @Transactional
    @Test
    void testCreateFacilityWithRegionAndCategory() throws Exception {
        // set regions and categories
        final String cat1 = "Research Center";
        final String cat2 = "University";
        final String reg1 = "Mooxosransk";
        final String reg2 = "Ulan-Ude";

        Region temp = new Region();
        temp.setRegionName(reg1);
        temp.setRegionId(228);
        int referenceRegionId1 = regionRepo.save(temp).getRegionId();
        temp.setRegionName(reg2);
        temp.setRegionId(2292);
        int referenceRegionId2 = regionRepo.save(temp).getRegionId();

        Category cat = new Category();
        cat.setCatName(cat1);
        categoryRepo.save(cat);
        cat.setCatName(cat2);
        categoryRepo.save(cat);
        //////////////////////////////

        Facility f = new Facility();
        f.setName("Test HSE Facility");
        f.setDescription("Higher school of economics");
        f.setRegion(regionRepo.findById(referenceRegionId1)
                .orElseThrow(IswebapiApplication.valueError("No expected region in database")));
        f.setCategories(categoryRepo.findAllByCatNameIn(Arrays.asList(cat1, cat2)));
        f.setCoordinates(new double[]{-55.35, -43.66});

        Facility f2 = new Facility();
        f2.setName("Test Research center");
        f2.setDescription("Test research facility");
        f2.setRegion(regionRepo.findById(referenceRegionId2)
                .orElseThrow(IswebapiApplication.valueError("No expected region in database")));
        f2.setCategories(categoryRepo.findAllByCatNameIn(Arrays.asList(cat1)));
        f2.setCoordinates(new double[]{0.0, -0.3});

        // facilities are not in database
        Assert.assertFalse(facilityRepo.findById(f.get_id()).isPresent());
        Assert.assertFalse(facilityRepo.findById(f2.get_id()).isPresent());

        facilityRepo.save(f);
        facilityRepo.save(f2);

        //facilities are in database
        Assert.assertTrue(facilityRepo.findById(f.get_id()).isPresent());
        Assert.assertTrue(facilityRepo.findById(f2.get_id()).isPresent());

        // select by all regions and all categories
        FacilityCriterias criterias = new FacilityCriterias();
        criterias.setRegions(Arrays.asList(referenceRegionId1, referenceRegionId2));
        criterias.setCategories(Arrays.asList(cat1, cat2));
        String criteriasJson = mapper.writeValueAsString(criterias);
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .secure(false)
                        .content(criteriasJson)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f2.getName())));

        // select by all regions and all categories (with 'null' default criteria values)
        criterias = new FacilityCriterias();
        criteriasJson = mapper.writeValueAsString(criterias);
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(criteriasJson)
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f2.getName())));

        // select by certain region and all categories
        criterias = new FacilityCriterias();
        criterias.setRegions(Arrays.asList(referenceRegionId2));
        criterias.setCategories(Arrays.asList(cat1, cat2));
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(f.getName()))))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f2.getName())));

        // select by certain region and non present in region category
        criterias = new FacilityCriterias();
        criterias.setRegions(Arrays.asList(referenceRegionId2));
        criterias.setCategories(Arrays.asList(cat2));
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(f.getName()))))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(f2.getName()))));
    }
}