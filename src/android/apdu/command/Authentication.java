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
public class Authentication extends Base {
    private static final String TAG = "Authentication";
    public static final byte KEY_A = (byte) 0x60;
    public static final byte KEY_B = (byte) 0x61;
    private int block = 4;
    private byte keyType = KEY_A;


    public Authentication(NFCReader nfcReader) {
        super(nfcReader);
    }

    public Authentication(NFCReader nfcReader, int block, byte keyType) {
        super(nfcReader);
        this.block = block;
        this.keyType = keyType;
    }

    public OnGetResultListener listener;

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public boolean run(int slotNumber) {
//        FF 86 00 00 05 01 00 04 60 00h
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x86, (byte) 0x0, (byte) 0x0, (byte) 0x05,
                (byte) 0x01, (byte) 0x0, (byte) 0x04, (byte) 0x60, (byte) 0x00
        };
        if (this.keyType == KEY_B){
            sendBuffer[8] = KEY_B;
        }else{
            sendBuffer[8] = KEY_A;
        }
        sendBuffer[7] = (byte) this.block;
        byte[] receiveBuffer = new byte[16];
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = getNfcReader().getReader();
        Result result;
        try {
            int byteCount = reader.transmit(slotNumber, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("Authentication", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("Authentication", e);
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
