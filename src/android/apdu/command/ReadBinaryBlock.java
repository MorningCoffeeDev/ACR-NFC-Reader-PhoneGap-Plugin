package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.ReadParams;

/**
 * Created by kevin on 5/27/15.
 */
public class ReadBinaryBlock extends Base<ReadParams> {
    private static final String TAG = "ReadBinaryBlock";

    public ReadBinaryBlock(ReadParams params) {
        super(params);
    }

    public boolean run() {
//         FF B0 00 04 10h
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xB0, (byte) 0x0, (byte) 0x04, (byte) 0x10};
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.getParams().getBlock();
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = this.getParams().getReader().getReader();
        Result result;
        try {
            Log.w(TAG, String.valueOf(this.getParams().getSlotNumber()));
            int byteCount = reader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);

            result = new Result("ReadBinaryBlock", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("ReadBinaryBlock", e);
            e.printStackTrace();
        }
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
