package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.ClearLCDParams;
import com.frankgreen.task.DisplayParams;

/**
 * Created by kevin on 5/27/15.
 */
public class ClearCLD extends Base {

    private static final String TAG = "ClearCLD";
    private ClearLCDParams params;

    public ClearCLD(NFCReader nfcReader) {
        super(nfcReader);
    }

    public ClearLCDParams getParams() {
        return params;
    }

    public void setParams(ClearLCDParams params) {
        this.params = params;
    }

    public ClearCLD(ClearLCDParams params) {
        super(params.getReader());
        this.params = params;
    }

    public boolean run() {
//       FF 00 60 00 00
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x60, (byte) 0x00, (byte) 0x00};
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("ClearCLD");
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = getNfcReader().getReader();
        try {
            int byteCount = reader.control(0, Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("ClearCLD", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("ClearCLD", e);
            e.printStackTrace();
        }

        if (this.params.getOnGetResultListener() != null) {
            this.params.getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
