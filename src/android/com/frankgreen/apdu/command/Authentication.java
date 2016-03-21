package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.AuthParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;

/**
 * Created by kevin on 5/27/15.
 */
public class Authentication extends Base<AuthParams> {
    private static final String TAG = "Authentication";
    public static final byte KEY_A = (byte) 0x60;
    public static final byte KEY_B = (byte) 0x61;

    public Authentication(AuthParams params) {
        super(params);
    }

    public boolean run(TaskListener listener) {
//        FF 86 00 00 05 01 00 04 60 00h
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x86, (byte) 0x0, (byte) 0x0, (byte) 0x05,
                (byte) 0x01, (byte) 0x0, (byte) 0x04, (byte) 0x60, (byte) 0x00
        };
        if (this.getParams().isA()){
            sendBuffer[8] = KEY_A;
        }else{
            sendBuffer[8] = KEY_B;
        }
        sendBuffer[7] = (byte) this.getParams().getBlock();
        byte[] receiveBuffer = new byte[16];
        Log.d(TAG, Util.toHexString(sendBuffer));
        ACRReader acrReader = this.getParams().getReader().getReader();
        Result result;
        try {
            int byteCount = acrReader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("Authentication", byteCount, receiveBuffer);
        } catch (ACRReaderException e) {
            result = new Result("Authentication", e);
            e.printStackTrace();
        }
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
