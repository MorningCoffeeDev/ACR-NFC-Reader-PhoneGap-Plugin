package com.frankgreen.operate;

/**
 * Created by cain on 16/4/26.
 */
public class CustomDevice {
    private String name;
    private String address;

    public CustomDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}

