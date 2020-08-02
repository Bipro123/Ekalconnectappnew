package com.connectapp.user.data;

import java.io.Serializable;

public class District implements Serializable {

    private String districtID;

    private String districtName;

    public String getDistrictID() {
        return districtID;
    }

    public void setDistrictID(String districtID) {
        this.districtID = districtID;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
}
