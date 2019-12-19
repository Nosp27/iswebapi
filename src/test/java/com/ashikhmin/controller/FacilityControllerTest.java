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
import org.springframework.test.web.servlet.ResultActions;
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

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        //Init MockMvc Object
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private static int counter;

    private Category createTempCategory() {
        return createTempCategory("Test Category " + counter++);
    }

    private Category createTempCategory(String name) {
        Category cat = new Category();
        cat.setCatName(name);
        return categoryRepo.save(cat);
    }

    private Region createTempRegion() {
        return createTempRegion("Test region " + counter++);
    }

    private Region createTempRegion(String name) {
        Region region = new Region();
        region.setRegionName(name);
        return regionRepo.save(region);
    }

    private Facility createTempFacility(
            String name, String description, Integer regionId, Integer[] cats, boolean save) {
        Facility facility = new Facility();
        facility.setName(name);
        facility.setDescription(description);
        if (regionId != null)
            facility.setRegion(regionRepo.findById(regionId)
                    .orElseThrow(IswebapiApplication.valueError("No expected region in database")));
        if (cats != null)
            facility.setCategories(categoryRepo.findAllByCatIdIn(Arrays.asList(cats)));
        facility.setCoordinates(-55.35, -43.66);
        return save ? facilityRepo.save(facility) : facility;
    }

    private FacilityCriterias createFacilityCriterias(List<Integer> regions, List<Integer> categories) {
        FacilityCriterias criterias = new FacilityCriterias();
        criterias.setRegions(regions);
        criterias.setCategories(categories);
        return criterias;
    }

    private ResultActions performRestApiRequest(FacilityCriterias criterias) throws Exception {
        return mvc.perform(
                MockMvcRequestBuilders
                        .post("/facilities")
                        .content(mapper.writeValueAsString(criterias))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAllFacilities() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/facilities")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Transactional
    @Test
    void testCreateFacility() throws Exception {
        long initialCount = facilityRepo.count();
        Facility facility = createTempFacility(
                "Test Facility",
                "Test facility description",
                null,
                null,
                false
        );
        Assert.assertFalse(facilityRepo.findById(facility.get_id()).isPresent());
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/facility")
                        .content(mapper.writeValueAsString(facility))
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(facility.getName())));
        Assert.assertEquals(facilityRepo.count(), initialCount + 1L);
    }

    @Transactional
    @Test
    void testCreateFacilityWithExistingId() throws Exception {
        String nameUnchanged = "Test Facility";
        String nameChanged = "Changed Facility";

        Facility facility = createTempFacility(
                nameUnchanged,
                "Test facility description",
                null,
                null,
                true
        );

        Facility changedFacility = createTempFacility(
                nameChanged,
                "",
                null,
                null,
                false
        );
        Assert.assertTrue(facilityRepo.findById(facility.get_id()).isPresent());
        boolean errorOccurred = false;
        try {
            mvc.perform(
                    MockMvcRequestBuilders
                            .post("/facility")
                            .content(mapper.writeValueAsString(facility))
                            .contentType("application/json"))
                    .andExpect(MockMvcResultMatchers.status().is(Matchers.not(200)))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(facility.getName())));
        } catch (Throwable e) {
            errorOccurred = true;
        }
        Assert.assertEquals(nameUnchanged, facilityRepo.findById(facility.get_id()).get().getName());
        Assert.assertTrue(errorOccurred);
    }

    @Transactional
    @Test
    void testCreateFacilityWithRegionAndCategory() throws Exception {
        // set up temp regions and categories
        final String cat1 = "Research Center";
        final String cat2 = "University";
        final String reg1 = "Mooxosransk";
        final String reg2 = "Ulan-Ude";

        final Integer catId1;
        final Integer catId2;

        final Integer referenceRegionId1;
        final Integer referenceRegionId2;

        Region tempRegion1 = createTempRegion(reg1);
        referenceRegionId1 = tempRegion1.getRegionId();

        Region tempRegion2 = createTempRegion(reg2);
        referenceRegionId2 = tempRegion2.getRegionId();

        Category tempCat1 = createTempCategory(cat1);
        catId1 = tempCat1.getCatId();
        Category tempCat2 = createTempCategory(cat2);
        catId2 = tempCat2.getCatId();
        //////////////////////////////

        Facility testFacility1 = createTempFacility(
                "Test Research center",
                "Test research facility",
                referenceRegionId1,
                new Integer[]{catId1},
                true
        );

        Facility testFacility2 = createTempFacility(
                "Test HSE Facility",
                "Higher school of economics",
                referenceRegionId2,
                new Integer[]{catId2},
                true
        );

        int newFId = testFacility1.get_id();
        int newF2Id = testFacility2.get_id();

        //assert facilities are in database
        Assert.assertTrue(facilityRepo.findById(newFId).isPresent());
        Assert.assertTrue(facilityRepo.findById(newFId).isPresent());

        // select by all regions and all categories
        FacilityCriterias criterias = createFacilityCriterias(
                Arrays.asList(referenceRegionId1, referenceRegionId2),
                Arrays.asList(catId1, catId2)
        );
        String criteriasJson = mapper.writeValueAsString(criterias);
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility1.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility2.getName())));

        // select by all regions and all categories (with 'null' default criteria values)
        criterias = createFacilityCriterias(null, null);
        criteriasJson = mapper.writeValueAsString(criterias);
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility1.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility2.getName())));

        // select by certain region and all categories
        criterias = createFacilityCriterias(
                Arrays.asList(referenceRegionId2),
                Arrays.asList(catId1, catId2)
        );
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(testFacility1.getName()))))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility2.getName())));

        // select by certain region and non present in region category
        criterias = createFacilityCriterias(
                Arrays.asList(referenceRegionId2),
                Arrays.asList(catId1)
        );
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(testFacility1.getName()))))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(testFacility2.getName()))));

        // empty region and not empty categories
        criterias = createFacilityCriterias(
                Arrays.asList(),
                Arrays.asList(catId1)
        );
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility1.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(testFacility2.getName()))));

        // empty categories and not empty regions
        criterias = createFacilityCriterias(
                Arrays.asList(referenceRegionId1),
                Arrays.asList()
        );
        performRestApiRequest(criterias)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(testFacility1.getName())))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.not(Matchers.containsString(testFacility2.getName()))));
    }

    @Test
    @Transactional
    void testUpdateFacility() throws Exception {
        String facilityName = "somename";
        String facilityDesc = "somedesc";
        Region facilityRegion = createTempRegion();
        Integer[] facilityCats = {
                createTempCategory().getCatId(),
                createTempCategory().getCatId()
        };

        String facilityChangedName = "somechangedname";
        String facilityChangedDesc = "somechangeddesc";
        Region facilityChangedRegion = createTempRegion();
        Integer[] facilityChangedCats = {
                createTempCategory().getCatId()
        };

        Facility initialFacility = createTempFacility(
                facilityName, facilityDesc, facilityRegion.getRegionId(), facilityCats, true
        );

        Facility changedFacility = createTempFacility(
                facilityChangedName,
                facilityChangedDesc,
                facilityChangedRegion.getRegionId(),
                facilityChangedCats,
                false
        );
        changedFacility.set_id(initialFacility.get_id());

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/facility")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(changedFacility)))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(facilityChangedName)))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(facilityChangedDesc)));

        Assert.assertEquals(
                (long) facilityChangedRegion.getRegionId(),
                (long) facilityRepo
                        .findById(initialFacility.get_id())
                        .get()
                        .getRegion()
                        .getRegionId());
    }

    @Test
    @Transactional
    void testDeleteFacility() throws Exception {
        String facilityName = "somefacilityname";
        Facility facility = createTempFacility(
                facilityName, "somedesc", null, null, true
        );

        Assert.assertTrue(facilityRepo.existsById(facility.get_id()));
        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/facility/" + facility.get_id())
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(facilityName)));

        Assert.assertFalse(facilityRepo.existsById(facility.get_id()));
    }
}