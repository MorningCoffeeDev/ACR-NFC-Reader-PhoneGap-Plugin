package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.SelectFileParams;
import com.frankgreen.task.WriteParams;

/**
 * Created by kevin on 5/27/15.
 */
public class SelectFile extends Base<SelectFileParams> {
    private static final String TAG = "SelectFile";

    public SelectFile(SelectFileParams params) {
        super(params);
    }


    public boolean run() {
//        00 A4 04 00 02 F0 01
        byte[] header = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x00};
        byte[] receiveBuffer = new byte[300];
        byte[] aid = Util.HexStringToByteArray(this.getParams().getAid());
        header[4] = (byte) (aid.length);

        byte[] sendBuffer = Util.ConcatArrays(header,aid);
        Log.d(TAG, Util.toHexString(sendBuffer));
        Result result = Result.buildSuccessInstance("SelectFile");
        Reader reader = getParams().getReader().getReader();
        try {
            Log.w(TAG, String.valueOf(this.getParams().getSlotNumber()));
            int byteCount = reader.transmit(getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("SelectFile", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("SelectFile", e);
            e.printStackTrace();
        }
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }
}
