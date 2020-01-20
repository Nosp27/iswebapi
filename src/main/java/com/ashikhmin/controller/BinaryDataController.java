package com.ashikhmin.controller;

import com.ashikhmin.iswebapi.IswebapiApplication;
import com.ashikhmin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin(
        origins = "http://localhost:4613",
        allowCredentials = "true",
        allowedHeaders = "*",
        maxAge = 3600
)
@RestController
public class BinaryDataController {
    @Autowired
    ImageRepo imageRepo;

    @Autowired
    RegionRepo regionRepo;

    @Autowired
    FacilityRepo facilityRepo;

    @Autowired
    CategoryRepo categoryRepo;

    static byte[] byteArrayListToPrimitive(ArrayList<Byte> byteList) {
        byte[] ret = new byte[byteList.size()];
        for(int i = 0; i < ret.length; i++)
            ret[i] = byteList.get(i);
        return ret;
    }

    @GetMapping(path = "/image/{img}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    byte[] getImage(@PathVariable("img") int img) {
        return imageRepo.findById(img)
                .orElseThrow(IswebapiApplication.valueErrorSupplier("No image with id " + img))
                .getImageBinary();
    }

    @PostMapping(path = "/image/add/path")
    int addImage(@RequestBody String path) {
        Resource image = new ClassPathResource(path);
        if (!image.exists())
            throw IswebapiApplication.valueError("Image " + path + " does not exist");

        try (InputStream is = image.getInputStream()) {
            byte[] imageBytes = new byte[is.available()];
            int bytesRead = is.read(imageBytes);
            if (bytesRead != imageBytes.length)
                throw IswebapiApplication.valueError("Byte lengths don't match");
            return addImage(imageBytes);
        } catch (IOException e) {
            RuntimeException exc = IswebapiApplication.valueError("Error loading image");
            exc.initCause(e);
            throw exc;
        }
    }

    @PostMapping(path = "/image/add/data")
    int addImage(@RequestBody byte[] imageBytes) {
        return imageRepo.save(new Image(imageBytes)).getImageId();
    }

    @PostMapping(path = "/image/add/for_entity/{entity_index}/{id}")
    String addImageForEntity(
            @PathVariable("entity_index") int entityIndex,
            @PathVariable("id") int id,
            @RequestBody ArrayList<Byte> imageReceivedData) {
        Integer imageId;
        CrudRepository repo;
        EntityEnum entityEnum = EntityEnum.getByIndex(entityIndex);
        byte[] imageData = byteArrayListToPrimitive(imageReceivedData);
        switch (entityEnum) {
            case REGION:
                repo = regionRepo;
                break;
            case CATEGORY:
                repo = categoryRepo;
                break;
            case FACILITY:
                repo = facilityRepo;
                break;
            default:
                repo = null;
        }
        Optional<HasImage> optionalEntity = repo.findById(id);
        if (!optionalEntity.isPresent())
            throw IswebapiApplication.valueError(entityEnum.name() + "with id " + id + " not found");
        HasImage entity = optionalEntity.get();
        imageId = entity.getImageId();
        if (imageId != null && imageRepo.findById(imageId).isPresent())
            imageRepo.deleteById(imageId);
        imageId = imageRepo.save(new Image(imageData)).getImageId();
        entity.setImageId(imageId);
        repo.save(entity);
        return imageId.toString();
    }
}
