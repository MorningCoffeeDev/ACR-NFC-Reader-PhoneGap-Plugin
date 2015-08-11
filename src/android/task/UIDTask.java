package com.frankgreen.task;

import android.os.AsyncTask;
import com.frankgreen.apdu.command.UID;

/**
 * Created by kevin on 6/2/15.
 */
public class UIDTask extends AsyncTask<BaseParams, Void, Boolean> {

    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(BaseParams... baseParamses) {
        BaseParams baseParams = baseParamses[0];
        if (baseParams == null) {
            return false;
        }
        UID uid = new UID(baseParams);
        return uid.run();

    }

}


