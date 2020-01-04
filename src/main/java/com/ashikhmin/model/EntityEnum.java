package com.ashikhmin.model;

import com.ashikhmin.iswebapi.IswebapiApplication;

public enum EntityEnum {
    REGION(0),
    CATEGORY(1),
    FACILITY(2);

    private int index;

    EntityEnum(int x) {
        index = x;
    }

    public int getIndex() {
        return index;
    }

    public static EntityEnum getByIndex(int i) {
        switch (i) {
            case 0: return REGION;
            case 1: return CATEGORY;
            case 2: return FACILITY;
            default:
                throw IswebapiApplication.valueError("No entity in enum with index " + i);
        }
    }
}
