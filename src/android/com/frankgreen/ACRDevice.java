package com.frankgreen;

import android.hardware.usb.UsbDevice;

public class ACRDevice<T extends Object> {
    private T device;

    public ACRDevice(T device) {
        this.device = device;
    }

    public T getDevice() {
        return device;
    }
}
