package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;

import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.command.Authentication;
import com.frankgreen.apdu.command.LoadAuthentication;


/**
 * Created by kevin on 6/10/15.
 */
public class AuthenticateWithKeyA extends AsyncTask<AuthParams, Void, Boolean> {
    private static final String TAG = "AuthenticateWithKeyA";

    @Override
    protected Boolean doInBackground(AuthParams... authParamses) {
        AuthParams authParams = authParamses[0];
        if (authParams == null) {
            return false;
        }
        int slotNumber = authParams.getSlotNumber();
        final NFCReader reader = authParams.getReader();
        LoadAuthentication load = new LoadAuthentication(reader);
        if (authParams.getKeyA() != null && !"".equals(authParams.getKeyA())) {
            Log.d(TAG, authParams.getKeyA().getClass().getName());
            Log.d(TAG, authParams.getKeyA());
            Log.d(TAG, Util.toHexString(Util.toNFCByte(authParams.getKeyA(), 6)));
            load.setPassword(authParams.getKeyA());
            load.setOnGetResultListener(authParams.getOnGetResultListener());
            if (!load.run(slotNumber)) {
                return false;
            }
            Authentication auth = new Authentication(reader, authParams.getBlock(),Authentication.KEY_A);
            auth.setOnGetResultListener(authParams.getOnGetResultListener());
            if (!auth.run(slotNumber)) {
                return false;
            }
        }
        return true;
    }
}
