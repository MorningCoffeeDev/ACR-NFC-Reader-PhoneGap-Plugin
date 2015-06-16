package com.frankgreen.task;

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
//        LoadAuthentication load = new LoadAuthentication(reader);
//        if (readParams.getPassword() != null && !"".equals(readParams.getPassword())) {
//            Log.d(TAG, readParams.getPassword().getClass().getName());
//            Log.d(TAG, readParams.getPassword());
//            Log.d(TAG, Util.toHexString(Util.toNFCPassword(readParams.getPassword())));
//            load.setPassword(readParams.getPassword());
//            load.setOnGetResultListener(reader.getOnReadResultlistener());
//            if (!load.run(slotNumber)) {
//                return false;
//            }
//            Authentication auth = new Authentication(reader, readParams.getBlock());
//            auth.setOnGetResultListener(reader.getOnReadResultlistener());
//            if (!auth.run(slotNumber)) {
//                return false;
//            }
//        }
        UpdateBinaryBlock update = new UpdateBinaryBlock(reader, writeParams.getBlock(), writeParams.getData());
        update.setOnGetResultListener(writeParams.getOnGetResultListener());
        return update.run(slotNumber);
    }
}
