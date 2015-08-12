package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.command.UpdateBinaryBlock;

/**
 * Created by kevin on 6/2/15.
 */
public class WriteTask extends AsyncTask<WriteParams, Void, Boolean> {
    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(WriteParams... writeParamses) {
        WriteParams writeParams = writeParamses[0];
        if (writeParams == null) {
            return false;
        }
        final UpdateBinaryBlock update = new UpdateBinaryBlock(writeParams);

        if (writeParams.getReader().getChipMeta().needAuthentication()) {
            TaskWithPassword task = new TaskWithPassword("UpdateBinaryBlock",
                    writeParams.getReader(),
                    writeParams.getSlotNumber(),
                    writeParams.getPassword()
            );
            task.setGetResultListener(writeParams.getOnGetResultListener());
            task.setCallback(new TaskWithPassword.TaskCallback() {
                @Override
                public boolean run() {
                    return update.run();
                }
            });
            task.run();
        } else {
            return update.run();
        }
        return false;
    }
}
