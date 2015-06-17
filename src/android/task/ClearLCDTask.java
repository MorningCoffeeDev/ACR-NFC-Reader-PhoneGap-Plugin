package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.ClearCLD;
import com.frankgreen.apdu.command.Display;

/**
 * Created by kevin on 6/2/15.
 */
public class ClearLCDTask extends AsyncTask<ClearLCDParams, Void, Boolean> {
    final private String TAG = "DisplayTask";

    @Override
    protected Boolean doInBackground(ClearLCDParams... clearLCDParamses) {
        ClearLCDParams clearLCDParams = clearLCDParamses[0];
        if (clearLCDParams == null) {
            return false;
        }
        ClearCLD clear = new ClearCLD(clearLCDParams);
        return clear.run();
    }
}
