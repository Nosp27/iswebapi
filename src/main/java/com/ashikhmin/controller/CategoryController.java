package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Category;
import com.ashikhmin.model.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class CategoryController {
    @Autowired
    CategoryRepo categoryRepo;

    @GetMapping(path = "/category/{catId}")
    Category getCategory(@PathVariable("catId") Integer catId) {
        return categoryRepo.findById(catId).orElseThrow(IswebapiApplication.valueErrorSupplier("No category " + catId));
    }

    @PostMapping(path = "/category")
    Category addCategory(@RequestBody Category category) {
        return categoryRepo.save(category);
    }

    @GetMapping(path = "/categories")
    Iterable<Category> listCategories() {
        return categoryRepo.findAll();
    }

    @PutMapping(path = "/category")
    Category updateCategory(@RequestBody Category category) {
        Integer catId = category.getCatId();
        Category dbCat = categoryRepo.findById(catId)
                .orElseThrow(IswebapiApplication.valueErrorSupplier("No category " + catId));
        dbCat.setCatName(category.getCatName());
        return categoryRepo.save(category);
    }

    @DeleteMapping(path = "/category/{catId}")
    Category deleteCategory(@PathVariable(name = "catId") Integer catId) {
        Category cat = categoryRepo.findById(catId)
                .orElseThrow(IswebapiApplication.valueErrorSupplier("No such category (" + catId + ")"));
        categoryRepo.deleteById(catId);
        return cat;
    }
}
