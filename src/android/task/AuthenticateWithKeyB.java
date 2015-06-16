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
public class AuthenticateWithKeyB extends AsyncTask<AuthParams, Void, Boolean> {
    private static final String TAG = "AuthenticateWithKeyB";

    @Override
    protected Boolean doInBackground(AuthParams... authParamses) {
        AuthParams authParams = authParamses[0];
        if (authParams == null) {
            return false;
        }
        int slotNumber = authParams.getSlotNumber();
        final NFCReader reader = authParams.getReader();
        LoadAuthentication load = new LoadAuthentication(reader);
        if (authParams.getKeyB() != null && !"".equals(authParams.getKeyB())) {
            Log.d(TAG, authParams.getKeyB().getClass().getName());
            Log.d(TAG, authParams.getKeyB());
            Log.d(TAG, Util.toHexString(Util.toNFCByte(authParams.getKeyB(), 6)));
            load.setPassword(authParams.getKeyB());
            load.setOnGetResultListener(authParams.getOnGetResultListener());
            if (!load.run(slotNumber)) {
                return false;
            }
            Authentication auth = new Authentication(reader, authParams.getBlock(), Authentication.KEY_B);
            auth.setOnGetResultListener(authParams.getOnGetResultListener());
            if (!auth.run(slotNumber)) {
                return false;
            }
        }
        return true;
    
    }
}
