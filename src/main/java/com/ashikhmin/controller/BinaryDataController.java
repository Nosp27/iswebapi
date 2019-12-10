package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.Image;
import com.ashikhmin.model.ImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class BinaryDataController {
    @Autowired
    ImageRepo imageRepo;

    @GetMapping(path = "/image/{img}")
    byte[] getImage(@PathVariable("img") String img) {
        return imageRepo.findById(img)
                .orElseThrow(IswebapiApplication.valueError("No image at path " + img))
                .getImageBinary();
    }

    @PostMapping(path = "/image/add/{id}")
    @Nullable
    Image addImage(@PathVariable("id") String id, @RequestBody String path) {
        Resource image = new ClassPathResource(path);
        if(!image.exists())
            throw IswebapiApplication.valueError("Image " + path + " does not exist").get();

        if(!imageRepo.existsById(id)){
            try (InputStream is = image.getInputStream()){
                byte[] imageBytes = new byte[is.available()];
                int bytesRead = is.read(imageBytes);
                if(bytesRead != imageBytes.length)
                    throw IswebapiApplication.valueError("Byte lengths don't match").get();
                return imageRepo.save(new Image(id, imageBytes));
            } catch (IOException e) {
                RuntimeException exc = IswebapiApplication.valueError("Error loading image").get();
                exc.initCause(e);
                throw exc;
            }
        }
        return null;
    }
}
