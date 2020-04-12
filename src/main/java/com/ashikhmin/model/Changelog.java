package com.ashikhmin.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Changelog {
    @Id
    @GeneratedValue
    int _id;

    String type;
    int facilityId;
    Timestamp updatedAt;

    public Changelog() {
    }

    public Changelog(int facilityId, Timestamp timestamp) {
        this.facilityId = facilityId;
        updatedAt = timestamp;
        type = "Facility";
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
