package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;

/**
 * Created by kevin on 5/27/15.
 */
public class ReadBinaryBlock extends Base {
    private static final String TAG = "ReadBinaryBlock";
    private int block = 4;

    public ReadBinaryBlock(NFCReader nfcReader) {
        super(nfcReader);
    }

    public ReadBinaryBlock(NFCReader nfcReader, int block) {
        super(nfcReader);
        this.block = block;
    }

    public OnGetResultListener listener;

    public boolean run(int slotNumber) {
//         FF B0 00 04 10h
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xB0, (byte) 0x0, (byte) 0x04, (byte) 0x10};
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.block;
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = getNfcReader().getReader();
        Result result;
        try {
            int byteCount = reader.transmit(slotNumber, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            int i = 2;
            for(;i<byteCount;i++){
                if(receiveBuffer[i+1] == (byte) 0x0){
                    break;
                }
            }
            result = new Result("ReadBinaryBlock", i, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("ReadBinaryBlock", e);
            e.printStackTrace();
        }
        if (this.listener != null) {
            this.listener.onResult(result);
        }
        return result.isSuccess();
    }

    public void setOnGetResultListener(OnGetResultListener listener) {
        this.listener = listener;
    }
}
