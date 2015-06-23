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
        LoadAuthentication load = new LoadAuthentication(authParams);
        if (authParams.getKeyA() != null && !"".equals(authParams.getKeyA())) {
            Log.d(TAG, authParams.getKeyA().getClass().getName());
            Log.d(TAG, authParams.getKeyA());
            Log.d(TAG, Util.toHexString(Util.toNFCByte(authParams.getKeyA(), 6)));
            if (!load.run()) {
                return false;
            }
            Authentication auth = new Authentication(authParams);
            if (!auth.run()) {
                return false;
            }
        }
        return true;
    }
}
