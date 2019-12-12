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
class BinaryDataControllerTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    ImageRepo imageRepo;

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
    void testAddImage() throws Exception {
        String imageId = "test_image";
        String imageResource = "static/hse.png";
        long initialCount = imageRepo.count();
        Image img = new Image(imageId, new byte[]{(byte) 1, (byte) 2, (byte) 3});
        Assert.assertFalse(imageRepo.findById(img.getImageId()).isPresent());
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/image/add/" + imageId)
                        .content(imageResource)
                        .contentType("plain/text"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(img.getImageId())));
        Assert.assertTrue(imageRepo.findById(img.getImageId()).isPresent());
        Assert.assertEquals(imageRepo.count(), initialCount + 1L);
    }
}