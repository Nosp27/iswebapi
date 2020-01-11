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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class BinaryDataControllerTest {
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    RegionRepo regionRepo;

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
    void testGetImage() throws Exception {
        // create and save sample image
        Image img = new Image(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        img = imageRepo.save(img);
        int imageId = img.getImageId();

        byte[] imageBytesFromApi = mvc.perform(
                MockMvcRequestBuilders
                        .get("/image/" + imageId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG))
                .andReturn().getResponse().getContentAsByteArray();
        Assert.assertArrayEquals(img.getImageBinary(), imageBytesFromApi);
    }

    @Transactional
    @Test
    void testAddImage() throws Exception {
        String imageResource = "static/hse.png";
        String addImageResponse = mvc.perform(
                MockMvcRequestBuilders
                        .post("/image/add/path")
                        .content(imageResource)
                        .contentType("plain/text"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        int imageReturnedId = Integer.parseInt(addImageResponse);
        Assert.assertTrue(imageRepo.findById(imageReturnedId).isPresent());

        Image img = new Image(new byte[]{(byte) 1, (byte) 2, (byte) 3});
        addImageResponse = mvc.perform(
                MockMvcRequestBuilders
                        .post("/image/add/data")
                        .content(img.getImageBinary())
                        .contentType("plain/text"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        imageReturnedId = Integer.parseInt(addImageResponse);
        Assert.assertTrue(imageRepo.findById(imageReturnedId).isPresent());
        Assert.assertArrayEquals(imageRepo.findById(imageReturnedId).get().getImageBinary(), img.getImageBinary());
    }

    @Transactional
    @Test
    void testSetEntityImage() throws Exception {
        // initialize
        Region region = new Region();
        Image img = new Image(new byte[]{(byte) 0, (byte) 1});

        // save to repository
        region = regionRepo.save(region);

        // set image for the region
        String response = mvc.perform(
                MockMvcRequestBuilders
                        .post(String.format(
                                "/image/add/for_entity/%d/%d", EntityEnum.REGION.getIndex(), region.getRegionId()
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Arrays.toString(img.getImageBinary())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertTrue(imageRepo.findById(img.getImageId()).isPresent());

        int returnedId = Integer.parseInt(response);
        Assert.assertTrue(imageRepo.findById(returnedId).isPresent());
        Assert.assertEquals((Integer) returnedId, regionRepo.findById(region.getRegionId()).get().getImageId());
    }

    @Transactional
    @Test
    void testChangeEntityImage() throws Exception {
        // initialize
        Region region = new Region();
        Image img = new Image(new byte[]{(byte) 0, (byte) 1});
        Image imageChanged = new Image(new byte[]{(byte) 2, (byte) 3});

        // save to repository
        img = imageRepo.save(img);
        region.setImageId(img.getImageId());
        region = regionRepo.save(region);

        // change image for the region
        String response = mvc.perform(
                MockMvcRequestBuilders
                        .post(String.format(
                                "/image/add/for_entity/%d/%d", EntityEnum.REGION.getIndex(), region.getRegionId()
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Arrays.toString(imageChanged.getImageBinary())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertFalse(imageRepo.findById(img.getImageId()).isPresent());

        int returnedId = Integer.parseInt(response);
        Assert.assertTrue(imageRepo.findById(returnedId).isPresent());
        Assert.assertEquals((Integer) returnedId, regionRepo.findById(region.getRegionId()).get().getImageId());
    }
}