package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;
import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.*;
import org.apache.cordova.PluginResult;

/**
 * Created by kevin on 6/2/15.
 */
public class ReadTask extends AsyncTask<ReadParams, Void, Boolean> {
    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(ReadParams... readParamses) {
        ReadParams readParams = readParamses[0];
        if (readParams == null) {
            return false;
        }
        final ReadBinaryBlock read = new ReadBinaryBlock(readParams);

        if (readParams.getReader().getChipMeta().needAuthentication()) {
            TaskWithPassword task = new TaskWithPassword("ReadBinaryBlock",
                    readParams.getReader(),
                    readParams.getSlotNumber(),
                    readParams.getPassword()
            );
            task.setGetResultListener(readParams.getOnGetResultListener());
            task.setCallback(new TaskWithPassword.TaskCallback() {
                @Override
                public boolean run() {
                    return read.run();
                }
            });

            task.run();
        } else {
            return read.run();
        }
        return false;
    }
}
