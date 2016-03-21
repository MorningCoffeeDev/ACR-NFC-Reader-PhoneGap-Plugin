package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.SelectFileParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;

/**
 * Created by kevin on 5/27/15.
 */
public class SelectFile extends Base<SelectFileParams> {
    private static final String TAG = "SelectFile";

    public SelectFile(SelectFileParams params) {
        super(params);
    }


    public String toDataString(Result result) {
        return Util.dataToString(result.getData());
    }

    public boolean run(TaskListener listener) {
//        00 A4 04 00 02 F0 01
        byte[] header = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x00};
        byte[] receiveBuffer = new byte[300];
        byte[] aid = Util.HexStringToByteArray(this.getParams().getAid());
        header[4] = (byte) (aid.length);

        byte[] sendBuffer = Util.ConcatArrays(header, aid);
        Log.d(TAG, Util.toHexString(sendBuffer));
        Result result = Result.buildSuccessInstance("SelectFile");
        ACRReader acrReader = getParams().getReader().getReader();
        try {
            int byteCount = acrReader.transmit(getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("SelectFile", byteCount, receiveBuffer);
        } catch (ACRReaderException e) {
            result = new Result("SelectFile", e);
            e.printStackTrace();
        }
        if (this.getParams().getOnGetResultListener() != null) {
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }
}
