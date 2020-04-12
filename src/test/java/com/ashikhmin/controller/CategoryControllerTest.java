package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Category;
import com.ashikhmin.model.CategoryRepo;
import com.ashikhmin.model.FacilityRepo;
import com.ashikhmin.model.RegionRepo;
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

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IswebapiApplication.class)
@AutoConfigureMockMvc
class CategoryControllerTest {
    @Autowired
    SecurityController securityController;

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
        securityController.setTestingMode();
    }

    Category createCategory(String catName, boolean save) {
        Category tempCategory = new Category();
        tempCategory.setCatName(catName);
        return save ? categoryRepo.save(tempCategory) : tempCategory;
    }

    @Test
    @Transactional
    void testAddCategory() throws Exception {
        String categoryName = "somcatname";
        Category testCategory = createCategory(categoryName, false);
        long databaseSize = categoryRepo.count();
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/category")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(testCategory)))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(categoryName)));

        Assert.assertEquals(databaseSize + 1L, categoryRepo.count());
    }

    @Test
    @Transactional
    void testGetCategory() throws Exception {
        String categoryName = "somecat";
        Category tempCategory = createCategory(categoryName, true);
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/category/" + tempCategory.getCatId())
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(categoryName)));

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/categories/")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(categoryName)));
    }

    @Test
    @Transactional
    void testUpdateCategory() throws Exception {
        String categoryName = "somecat";
        String changedName = "somenewname";
        Category tempCategory = createCategory(categoryName, true);
        Category changedCategory = createCategory(changedName, false);
        changedCategory.setCatId(tempCategory.getCatId());

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/category")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(changedCategory)))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(changedName)));
    }

    @Test
    @Transactional
    void testDeleteCategory() throws Exception {
        Category tempCategory = createCategory("somenonexistentcategory", true);
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/category/" + tempCategory.getCatId())
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(tempCategory.getCatName())));

        Assert.assertFalse(categoryRepo.existsById(tempCategory.getCatId()));
    }
}