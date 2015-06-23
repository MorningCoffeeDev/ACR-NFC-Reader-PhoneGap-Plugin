package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;
import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.UID;
import org.apache.cordova.PluginResult;

/**
 * Created by kevin on 6/2/15.
 */
public class UIDTask extends AsyncTask<UIDParams, Void, Boolean> {

    final private String TAG = "UIDTask";


    @Override
    protected Boolean doInBackground(UIDParams... uidParamses) {
        int slotNumber = uidParamses[0].getSlotNumber();
        NFCReader reader = uidParamses[0].getReader();
        UIDParams uidParams = uidParamses[0];
        try {
            byte[] atr = reader.getReader().power(slotNumber, Reader.CARD_WARM_RESET);
            if (atr != null) {
                reader.getReader().setProtocol(slotNumber, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
                UID uid = new UID(uidParams);
                return uid.run();
            }
        } catch (ReaderException e) {
            if (uidParams.getOnGetResultListener() != null) {
                uidParams.getOnGetResultListener().onResult(new Result("UID",e));
            }
        }
        return false;
    }

}


