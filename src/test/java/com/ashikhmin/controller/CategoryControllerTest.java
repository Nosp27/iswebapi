package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.CategoryRepo;
import com.ashikhmin.model.FacilityRepo;
import com.ashikhmin.model.RegionRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class CategoryControllerTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    RegionRepo regionRepo;

    @Autowired
    CategoryRepo categoryRepo;

    MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        //Init MockMvc Object
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    void addCategory() {
        
    }

}