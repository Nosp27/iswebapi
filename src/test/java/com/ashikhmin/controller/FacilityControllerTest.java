package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.CategoryRepo;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.FacilityRepo;
import com.ashikhmin.model.RegionRepo;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

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

    @Autowired
    MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Transactional
    @Test
    void testCreateFacility() throws Exception {
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
        Assert.assertTrue(facilityRepo.findById(f.get_id()).isPresent());
    }

    @Transactional
    @Test
    void testCreateFacilityWithRegionAndCategory() throws Exception {
        Facility f = new Facility();
        f.setName("Test HSE Facility");
        f.setDescription("Higher school of economics");
        f.setRegion(regionRepo.findById(0).orElseThrow(IswebapiApplication.valueError("No region with id 0")));
        f.setCategories(categoryRepo.findAllByCatNameIn(Arrays.asList("University", "Research center")));
        f.setCoordinates(new double[]{-55.35, -43.66});

        Facility f2 = new Facility();
        f2.setName("Test Research center");
        f2.setDescription("Test research facility");
        f2.setRegion(regionRepo.findById(1).orElseThrow(IswebapiApplication.valueError("No region with id 1")));
        f2.setCategories(categoryRepo.findAllByCatNameIn(Arrays.asList("Research center")));
        f2.setCoordinates(new double[] {0.0, -0.3});

        Assert.assertFalse(facilityRepo.findById(f.get_id()).isPresent());
        Assert.assertFalse(facilityRepo.findById(f2.get_id()).isPresent());
        facilityRepo.save(f);
        facilityRepo.save(f2);

        Assert.assertTrue(facilityRepo.findById(f.get_id()).isPresent());

        // select by all regions and all categories
        FacilityCriterias criterias = new FacilityCriterias();
        criterias.setRegions(Arrays.asList(0, 1));
        criterias.setCategories(Arrays.asList("University", "Research center"));
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f2.getName())));

        // select by all regions and all categories (with null defaults)
        criterias = new FacilityCriterias();
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(f2.getName())));

        // select by certain region and all categories
        criterias = new FacilityCriterias();
        criterias.setRegions(Arrays.asList(1));
        criterias.setCategories(Arrays.asList("Research center", "University"));
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
        criterias.setRegions(Arrays.asList(1));
        criterias.setCategories(Arrays.asList("University"));
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.equalTo("[]")));
    }
}