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
        int slotNumber = readParams.getSlotNumber();
        final NFCReader reader = readParams.getReader();

        ReadBinaryBlock read = new ReadBinaryBlock(reader, readParams.getBlock());
        read.setOnGetResultListener(readParams.getOnGetResultListener());
        return read.run(slotNumber);
    }
}
