package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.BaseParams;
import com.frankgreen.task.UIDParams;

/**
 * Created by kevin on 5/27/15.
 */
public class GetVersion extends Base<BaseParams> {
    private static final String TAG = "GetVersion";


    public GetVersion(BaseParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x01, (byte) 0x60};
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("GetVersion");
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = this.getParams().getReader().getReader();
        int byteCount = 0;
        try {
            byteCount = reader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("GetVersion", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("GetVersion", e);
        }
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
