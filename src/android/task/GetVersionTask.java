package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.GetVersion;
import com.frankgreen.apdu.command.UID;

/**
 * Created by kevin on 6/2/15.
 */
public class GetVersionTask extends AsyncTask<BaseParams, Void, Boolean> {

    final private String TAG = "GetVersionTask";


    @Override
    protected Boolean doInBackground(BaseParams... baseParamses) {
        BaseParams baseParams = baseParamses[0];
        if (baseParams == null) {
            return false;
        }
        GetVersion getVersion = new GetVersion(baseParams);
        return getVersion.run();

    }

}


