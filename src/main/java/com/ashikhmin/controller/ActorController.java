package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Actor;
import com.ashikhmin.model.ActorRepo;
import com.ashikhmin.model.Facility;
import com.ashikhmin.model.FacilityRepo;
import com.ashikhmin.model.helpdesk.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class ActorController {

    Logger logger = Logger.getLogger(ActorController.class.getName());

    @Autowired
    ActorRepo actorRepo;

    @Autowired
    FacilityRepo facilityRepo;

    @GetMapping("/actor/me")
    public Actor getActor() {
        Object userPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isProduction = userPrincipal instanceof OidcUser;
        String username;
        if (isProduction)
            username = ((OidcUser) userPrincipal).getName();
        else
            username = ((UserDetails) userPrincipal).getUsername();

        Actor newActor;
        if (isProduction)
            newActor = new Actor((OidcUser) userPrincipal);
        else
            newActor = Actor.testAcor(username);

        if (actorRepo.findByUsername(username) == null)
            newActor = actorRepo.save(newActor);
        else
            newActor = editActor(newActor, username);
        return newActor;
    }

    @PostMapping("/actor")
    public Actor addActor(@RequestBody Actor actor) {
        return actorRepo.save(actor);
    }

    @PostMapping("/actor/new_token")
    public String newToken(@RequestBody String token) {
        Actor currentActor = getActor();
        if(token == null)
            throw IswebapiApplication.valueError("Null firebase token supplied!");
        if(token.equals("null")) {
            currentActor.setFirebaseToken(null);
            actorRepo.save(currentActor);
            return "Actor Token reset is successful";
        }
        if(currentActor.getFirebaseToken() != null && token.equals(currentActor.getFirebaseToken()))
            return "Token is fine already";
        String finalMessage = currentActor.getFirebaseToken() == null ? "Successfully assigned a new token to %s" : "Successfully replaced token for %s";
        finalMessage = String.format(finalMessage, currentActor.getEmail());
        currentActor.setFirebaseToken(token);
        actorRepo.save(currentActor);
        logger.log(Level.INFO, finalMessage);
        return finalMessage;
    }

    @PutMapping("/actor/{actorId}")
    public Actor editActor(@RequestBody Actor actor, @PathVariable(name = "actorId") String actorId) {
        Actor old = actorRepo.findByUsername(actorId);
        if (old == null)
            throw IswebapiApplication.valueError("Actor is not present in database");
        old.setEmail(actor.getEmail());
        old.setFamilyName(actor.getFamilyName());
        old.setGivenName(actor.getGivenName());
        old.setImageSrc(actor.getImageSrc());
        old.setPrivilege(actor.getPrivilege());
        return actorRepo.save(old);
    }

    @GetMapping("/actor/like/{id}")
    public Map<String, Boolean> likeFacility(@PathVariable(name = "id") Integer id) {
        if (id == null || !facilityRepo.findById(id).isPresent())
            throw IswebapiApplication.valueError("Incorrect id:" + id);

        Facility facility = facilityRepo.findById(id).get();
        Actor currentActor = getActor();
        Boolean liked = currentActor.like(facility);
        facility.getSubscribedActors().add(currentActor);
        Map<String, Boolean> ret = new HashMap<>();
        ret.put("liked", liked);
        actorRepo.save(currentActor);
        return ret;
    }

    @GetMapping(path = "actor/update_feed")
    Iterable<Facility> getUpdateFeed() {
        Iterable<Facility> actorFavorites = facilityRepo.getAllBySubscribedActorsContaining(getActor());
        List<Integer> actorFavoritesIds = new ArrayList<>();
        actorFavorites.forEach(e -> actorFavoritesIds.add(e.get_id()));
        Iterable<Facility> facilitiesChangelog = facilityRepo.getChangelog(actorFavoritesIds);
        return facilitiesChangelog;
    }

    @GetMapping("/actor/favorites")
    public Set<Facility> getLikedFacilities() {
        Actor actor = getActor();
        return actor.getFavoriteFacilities();
    }

    @GetMapping("/help/issues")
    public Set<Issue> getIssues() {
        Actor actor = getActor();
        return actor.getIssues();
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
