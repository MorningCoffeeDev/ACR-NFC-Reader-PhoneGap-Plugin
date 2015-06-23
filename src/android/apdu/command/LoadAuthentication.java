package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.AuthParams;

/**
 * Created by kevin on 5/27/15.
 */
public class LoadAuthentication extends Base<AuthParams> {
    private static final String TAG = "LoadAuthentication";

    public LoadAuthentication(AuthParams params) {
        super(params);
    }


    public boolean run() {
//        FF 82 00 00 06 FF FF FF FF FF FF
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x82, (byte) 0x0, (byte) 0x0, (byte) 0x06,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0
        };

        byte[] pwd = Util.toNFCByte(this.getParams().isA()? this.getParams().getKeyA() : this.getParams().getKeyB(), 6);
        System.arraycopy(pwd, 0, sendBuffer, 5, 6);
        byte[] receiveBuffer = new byte[16];
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = this.getParams().getReader().getReader();
        Result result;
        try {
            int byteCount = reader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("LoadAuthentication", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("LoadAuthentication", e);
            e.printStackTrace();
        }
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
