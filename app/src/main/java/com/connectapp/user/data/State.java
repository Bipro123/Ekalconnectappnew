package com.connectapp.user.data;

import java.io.Serializable;
import java.util.ArrayList;

public class State implements Serializable {
    private String name;
    private String code;
    private ArrayList<District> districts;

    public State(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public ArrayList<District> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<District> districts) {
        this.districts = districts;
    }
}
