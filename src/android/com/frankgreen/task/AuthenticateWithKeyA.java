package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;

import com.frankgreen.Util;
import com.frankgreen.apdu.command.Authentication;
import com.frankgreen.apdu.command.LoadAuthentication;
import com.frankgreen.params.AuthParams;


/**
 * Created by kevin on 6/10/15.
 */
public class AuthenticateWithKeyA extends AsyncTask<AuthParams, Void, Boolean> {
    private static final String TAG = "AuthenticateWithKeyA";

    @Override
    protected Boolean doInBackground(AuthParams... paramses) {
        AuthParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
        }
        LoadAuthentication load = new LoadAuthentication(params);
        if (params.getKeyA() != null && !"".equals(params.getKeyA())) {
            Log.d(TAG, params.getKeyA().getClass().getName());
            Log.d(TAG, params.getKeyA());
            Log.d(TAG, Util.toHexString(Util.convertHexAsciiToByteArray(params.getKeyA(), 6)));
            if (!load.run()) {
                return false;
            }
            Authentication auth = new Authentication(params);
            if (!auth.run()) {
                return false;
            }
        }
        return true;
    }
}
