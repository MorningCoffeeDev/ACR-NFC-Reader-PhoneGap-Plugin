package com.frankgreen.params;

import org.apache.cordova.CallbackContext;

/**
 * Created by cain on 16/4/26.
 */
public class ConnectParams extends Params {
    private String mDeviceAddress;

    private CallbackContext callbackContext;

    public ConnectParams(String mDeviceAddress, CallbackContext callbackContext) {
        this.mDeviceAddress = mDeviceAddress;
        this.callbackContext = callbackContext;
    }

    public String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }
}
