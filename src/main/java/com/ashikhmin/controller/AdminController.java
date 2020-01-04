package com.ashikhmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin
@Controller
public class AdminController {
    @GetMapping(path="/admin")
    public String requestAdmin() {
        return "/bootstrap/index.html";
    }
}
