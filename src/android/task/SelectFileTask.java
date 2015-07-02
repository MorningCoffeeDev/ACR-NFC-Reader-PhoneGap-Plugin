package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.SelectFile;
import com.frankgreen.apdu.command.UID;

/**
 * Created by kevin on 6/2/15.
 */
public class SelectFileTask extends AsyncTask<SelectFileParams, Void, Boolean> {

    final private String TAG = "SelectFileTask";


    @Override
    protected Boolean doInBackground(SelectFileParams... selectFileParamses) {
        SelectFileParams selectFileParams = selectFileParamses[0];
        if (selectFileParams == null) {
            return false;
        }
        SelectFile selectFile = new SelectFile(selectFileParams);
        return selectFile.run();

    }

}


