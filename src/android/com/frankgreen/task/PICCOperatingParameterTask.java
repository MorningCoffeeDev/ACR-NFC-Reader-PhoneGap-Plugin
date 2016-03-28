package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.PICCOperatingParameter;
import com.frankgreen.params.PICCOperatingParameterParams;

/**
 * Created by kevin on 8/24/15.
 */
public class PICCOperatingParameterTask extends AsyncTask<PICCOperatingParameterParams, Void, Boolean> {

    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(PICCOperatingParameterParams... paramses) {
        PICCOperatingParameterParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        PICCOperatingParameter piccOperatingParameter = new PICCOperatingParameter(params);
//        params.getReader().getReader().getBatteryLevel();
        return piccOperatingParameter.run();
    }
}
