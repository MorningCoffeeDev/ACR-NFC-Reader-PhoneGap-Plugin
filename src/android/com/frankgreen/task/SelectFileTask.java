package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.SelectFile;
import com.frankgreen.params.SelectFileParams;

/**
 * Created by kevin on 6/2/15.
 */
public class SelectFileTask extends AsyncTask<SelectFileParams, Void, Boolean> {

    final private String TAG = "SelectFileTask";


    @Override
    protected Boolean doInBackground(SelectFileParams... paramses) {
        SelectFileParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        SelectFile selectFile = new SelectFile(params);
        return selectFile.run();

    }

}


