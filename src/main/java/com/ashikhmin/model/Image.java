package com.ashikhmin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Image {
    @Id
    @GeneratedValue
    private int imageId;
    private byte[] imageBinary;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setImageBinary(byte[] imageBinary) {
        this.imageBinary = imageBinary;
    }

    public byte[] getImageBinary() {
        return imageBinary;
    }

    public Image(){}

    public Image(byte[] array) {
        imageBinary = array;
    }
}
