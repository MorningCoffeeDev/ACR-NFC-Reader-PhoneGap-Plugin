package com.frankgreen.task;

import android.os.AsyncTask;
import com.frankgreen.apdu.command.UID;

/**
 * Created by kevin on 6/2/15.
 */
public class UIDTask extends AsyncTask<UIDParams, Void, Boolean> {

    final private String TAG = "UIDTask";


    @Override
    protected Boolean doInBackground(UIDParams... uidParamses) {
        UIDParams uidParams = uidParamses[0];
        if (uidParams == null) {
            return false;
        }
        UID uid = new UID(uidParams);
        return uid.run();

    }

}


