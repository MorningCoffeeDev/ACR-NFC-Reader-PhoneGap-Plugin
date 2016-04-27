package com.frankgreen.params;

import org.apache.cordova.CallbackContext;

/**
 * Created by cain on 16/4/26.
 */
public class DisconnectParams extends Params{
    private CallbackContext callbackContext;

    public DisconnectParams(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }
}
