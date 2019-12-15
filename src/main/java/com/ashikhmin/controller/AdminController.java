package com.ashikhmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping(path="/admin")
    public String requestAdmin() {
        return "/bootstrap/index.html";
    }
}
