package com.frankgreen.task;

import android.os.AsyncTask;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;


/**
 * Created by kevin on 6/2/15.
 */
public class ResetTask extends AsyncTask<ResetParams, Void, Boolean> {

    final private String TAG = "ResetTask";


    @Override
    protected Boolean doInBackground(ResetParams... resetParamses) {
        ResetParams resetParams = resetParamses[0];
        int slotNumber = resetParams.getSlotNumber();
        NFCReader reader = resetParams.getReader();
        Result result = Result.buildSuccessInstance("Reset");

        try {
            byte[] atr = reader.getReader().power(slotNumber, Reader.CARD_WARM_RESET);
            if (atr != null) {
                result.setData(atr);
                reader.getReader().setProtocol(slotNumber, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
            }
        } catch (ReaderException e) {
            result = new Result("Reset",e);
        }
        if (resetParams.getOnGetResultListener() != null) {
            resetParams.getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}


