package com.ashikhmin.controller;

import com.ashikhmin.firebase.FirebaseComponent;
import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class FacilityController {
    Logger logger = Logger.getLogger(FacilityController.class.getName());

    @Autowired
    SecurityController securityController;

    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    FirebaseComponent firebaseComponent;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private RegionRepo regionRepo;

    @Autowired
    private ActorRepo actorRepo;

    @Autowired
    private ChangelogRepo changelogRepo;

    @GetMapping(path = "/facilities")
    Iterable<Facility> getAllFacilities() {
        return facilityRepo.findAll();
    }

    @PostMapping(path = "/facilities")
    Iterable<Facility> getFacilities(@RequestBody FacilityCriterias criterias) {
        boolean emptyRegions = isEmpty(criterias.getRegions());
        boolean emptyCategories = isEmpty(criterias.getCategories());
        if (emptyCategories && emptyRegions) {
            return facilityRepo.findAll();
        }
        Set<Category> cats = null;
        Set<Region> regions = null;
        if (!emptyCategories) {
            cats = new HashSet<>();
            categoryRepo.findAllById(criterias.getCategories()).forEach(cats::add);
        }
        if (!emptyRegions) {
            regions = new HashSet<>();
            regionRepo.findAllById(criterias.getRegions()).forEach(regions::add);
        }

        if (emptyCategories)
            return facilityRepo.getAllByRegionIn(regionRepo.getAllByRegionIdIn(criterias.getRegions()));
        if (emptyRegions)
            return facilityRepo.getAllByCategoriesIn(cats);

        return facilityRepo.getAllByCategoriesIsInAndRegionIn(cats, regions);
    }

    private <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.isEmpty();
    }

    @PostMapping(path = "/facility")
    Facility addFacility(@RequestBody Facility facility) {
        securityController.ensureManagerPermission();
        if (facility == null || (facility.get_id() == null && facility.getName() == null)) {
            return facilityRepo.save(new Facility());
        }
        if (facilityRepo.existsById(facility.get_id()))
            throw IswebapiApplication.valueErrorSupplier("Id exists. Trying to repeat primary key").get();
        return facilityRepo.save(facility);
    }

    @PutMapping(path = "/facility")
    Facility updateFacility(@RequestBody Facility facility) {
        securityController.ensureManagerPermission();
        Supplier<? extends RuntimeException> exceptionSupplier =
                IswebapiApplication.valueErrorSupplier("No facility with id " + facility.get_id());
        Facility dbFacility = facilityRepo.findById(facility.get_id()).orElseThrow(exceptionSupplier);
        dbFacility.setName(facility.getName());
        dbFacility.setDescription(facility.getDescription());
        dbFacility.setLat(facility.getLat());
        dbFacility.setLng(facility.getLng());
        if (facility.getRegion() != null)
            dbFacility.setRegion(regionRepo.findById(facility.getRegion().getRegionId()).orElseThrow(exceptionSupplier));
        dbFacility.setCategories(facility.getCategories());
        dbFacility.setUtility(facility.getUtility());
        dbFacility.setEmployees(facility.getEmployees());
        dbFacility.setInvestmentSize(facility.getInvestmentSize());
        dbFacility.setProfitability(facility.getProfitability());

        dbFacility = facilityRepo.save(dbFacility);
        changelogRepo.save(new Changelog(dbFacility.get_id(), new Timestamp(System.currentTimeMillis())));
        notifyFacility(dbFacility.get_id());
        return dbFacility;
    }

    @PostMapping(path = "/facility/notify/{id}")
    String notifyFacility(@PathVariable int id) {
        securityController.ensureManagerPermission();
        Facility dbFacility = facilityRepo.findById(id)
                .orElseThrow(IswebapiApplication.valueErrorSupplier("No facility with given id"));
        sendFacilityUpdateNotification(dbFacility);
        return "Notification gone";
    }

    private void sendFacilityUpdateNotification(Facility facility) {
        securityController.ensureManagerPermission();
        Notification notification = Notification.builder()
                .setTitle(facility.getName() + "!")
                .setBody("Go check!")
                .build();

        Set<String> tokens = new HashSet<>();
        for (Actor actor : actorRepo.findByFavoriteFacilitiesContaining(facility))
            if (actor.getFirebaseToken() != null)
                tokens.add(actor.getFirebaseToken());
        try {
            firebaseComponent.sendNotificationToTokens(notification, tokens);
        } catch (FirebaseMessagingException e) {
            logger.log(Level.WARNING, "Error while sending firebase message", e);
        }
    }

    @DeleteMapping(path = "/facility/{facilityId}")
    Facility deleteFacility(@PathVariable int facilityId) {
        securityController.ensureManagerPermission();
        Facility facility =
                facilityRepo.findById(facilityId)
                        .orElseThrow(IswebapiApplication.valueErrorSupplier("No facility with id " + facilityId));

        Iterable<Changelog> linkedChangelog = changelogRepo.findAllByFacilityId(facility.get_id());
        if(linkedChangelog != null)
            changelogRepo.deleteAll(linkedChangelog);
        facility.getSubscribedActors().clear();
        facilityRepo.delete(facility);
        return facility;
    }
}
