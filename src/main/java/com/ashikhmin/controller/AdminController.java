package com.ashikhmin.controller;

import com.google.rpc.BadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.security.sasl.AuthenticationException;
import java.security.Principal;
import java.util.List;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@Controller
public class AdminController {
    @Autowired
    SecurityController securityController;

    @GetMapping(path = "/admin")
    public String requestAdmin() {
        securityController.ensureManagerPermission();
        return "/bootstrap/index.html";
    }
}
