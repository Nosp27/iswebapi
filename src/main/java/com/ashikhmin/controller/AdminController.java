package com.ashikhmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@Controller
public class AdminController {
    @GetMapping(path="/admin")
    public String requestAdmin() {
        return "/bootstrap/index.html";
    }
}
