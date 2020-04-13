package com.ashikhmin.model;

import com.ashikhmin.model.helpdesk.Issue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Actor {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String username;

    private String givenName;
    private String familyName;
    private String email;
    private String privilege;
    private String imageSrc;
    private String firebaseToken;

    @Column(columnDefinition = "text not null default='Investor'")
    private String type;

    public Actor() {

    }

    public Actor(OidcUser user) {
        OidcUserInfo userInfo = user.getUserInfo();
        username = userInfo.getSubject();
        givenName = userInfo.getGivenName();
        familyName = userInfo.getFamilyName();
        email = userInfo.getEmail();
        privilege = userInfo.getClaimAsString("privilege");
    }

    public static Actor testAcor(String testUsername) {
        Actor ret = new Actor();
        ret.username = testUsername;
        ret.givenName = "Test";
        ret.familyName = "User";
        ret.email = "somefakeuseruser@somemail.some";
        ret.favoriteFacilities = new HashSet<>();
        ret.issues = new HashSet<>();
        return ret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_favorite_facility",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private Set<Facility> favoriteFacilities;

    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(
            name = "actor_issue",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id"))
    private Set<Issue> issues;

    public Boolean like(Facility facility) {
        if (favoriteFacilities.contains(facility)) {
            favoriteFacilities.remove(facility);
            return false;
        } else {
            favoriteFacilities.add(facility);
            return true;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Facility> getFavoriteFacilities() {
        return favoriteFacilities;
    }
}
