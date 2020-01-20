package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.ActorRepo;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.FacilityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class ActorController {
    @Autowired
    ActorRepo actorRepo;

    @Autowired
    FacilityRepo facilityRepo;

    private Actor getActor() {
        String username = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();
        return actorRepo.findByUsername(username);
    }

    @PostMapping("/actor")
    public Actor addActor(@RequestBody Actor actor) {
        return actorRepo.save(actor);
    }

    @GetMapping("/actor/like/{id}")
    public Integer likeFacility(@PathVariable(name = "id") Integer id) {
        if (id == null || !facilityRepo.findById(id).isPresent())
            throw IswebapiApplication.valueError("Incorrect id:" + id);

        Facility facility = facilityRepo.findById(id).get();
        Actor currentActor = getActor();
        currentActor.like(facility);
        facility.getSubscribedActors().add(currentActor);
        return 200;
    }

    @GetMapping("/actor/favorites")
    public Set<Facility> getLikedFacilities() {
        Actor actor = getActor();
        return actor.getFavoriteFacilities();
    }


    @GetMapping("/ping")
    public String ping() {
        return "ping successful";
    }

    @GetMapping("/secure_ping")
    public String securePing() {
        return "Login successful!";
    }

    @GetMapping("/logout")
    public String logout() {
        return "Successful logout";
    }

    @GetMapping("/actor/login_fail")
    public ResponseEntity failLogin() {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/")
    public String home() {
        return "You are at home page";
    }

    @GetMapping("/actor/login_callback")
    public String loginCallback() {
        return "You accessed login callback";
    }
}
