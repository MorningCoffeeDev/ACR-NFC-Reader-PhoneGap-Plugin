package com.frankgreen.task;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.AsyncTask;
import android.util.Log;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.command.Authentication;
import com.frankgreen.apdu.command.LoadAuthentication;
import com.frankgreen.apdu.command.ReadBinaryBlock;
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
        int slotNumber = writeParams.getSlotNumber();
        final NFCReader reader = writeParams.getReader();
        UpdateBinaryBlock update = new UpdateBinaryBlock(writeParams);
        return update.run();
    }
}
