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
public class UpdateBinaryBlock extends Base {
    private static final String TAG = "UpdateBinaryBlock";
    private int block = 4;
    private byte[] data;

    public UpdateBinaryBlock(NFCReader nfcReader, int block, String data) {
        super(nfcReader);
        this.block = block;
        this.data = Util.toNFCByte(data, 16);
    }

    public UpdateBinaryBlock(NFCReader nfcReader, int block, byte[] data) {
        super(nfcReader);
        this.block = block;
        this.data = data;
    }

    public OnGetResultListener listener;

    public boolean run(int slotNumber) {
//        FF D6 00 04 10 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xD6, (byte) 0x0, (byte) 0x04, (byte) 0x10,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.block;

//        byte[] d = Util.toNFCByte(this.data,16);
        System.arraycopy(data, 0, sendBuffer, 5, 16);
        Log.d(TAG, Util.toHexString(sendBuffer));
        Reader reader = getNfcReader().getReader();
        Result result;
        try {
            int byteCount = reader.transmit(slotNumber, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("UpdateBinaryBlock", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("UpdateBinaryBlock", e);
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
