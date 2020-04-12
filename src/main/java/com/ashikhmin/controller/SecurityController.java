package com.ashikhmin.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {
    private boolean testingMode = false;

    @GetMapping("/security/ensure_manager_access")
    public void ensureManagerPermission() {
        if (testingMode)
            return;

        List<String> userGroups = ((OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUserInfo()
                .getClaim("groups");
        if (!userGroups.contains("Manager"))
            throw new AccessDeniedException("Only Managers can access!");
    }

    public void setTestingMode() {
        testingMode = true;
    }
}
