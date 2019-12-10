package com.ashikhmin.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Image {
    @Id
    private String imageId;
    private byte[] imageBinary;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageBinary(byte[] imageBinary) {
        this.imageBinary = imageBinary;
    }

    public byte[] getImageBinary() {
        return imageBinary;
    }

    public Image(){}

    public Image(String id, byte[] array) {
        imageId = id;
        imageBinary = array;
    }
}
