package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Category;
import com.ashikhmin.model.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {
    @Autowired
    CategoryRepo categoryRepo;

    @GetMapping(path = "/category/{catName}")
    Category getRegion(@PathVariable("catName") String catName) {
        return categoryRepo.findById(catName).orElseThrow(IswebapiApplication.valueError("No category " + catName));
    }

    @PostMapping(path = "/category")
    Category addRegion(@RequestBody Category category) {
        return categoryRepo.save(category);
    }

    @PutMapping(path = "/category/{catName}")
    Category updateRegion(@PathVariable String catName, @RequestBody Category category) {
        if(categoryRepo.existsById(catName))
            return categoryRepo.save(category);

        throw IswebapiApplication.valueError("No category " + catName).get();
    }
}
