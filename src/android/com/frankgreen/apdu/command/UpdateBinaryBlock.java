package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.WriteParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

import java.util.Arrays;

/**
 * Created by kevin on 5/27/15.
 */
public class UpdateBinaryBlock extends Base<WriteParams> implements OnDataListener {
    private static final String TAG = "UpdateBinaryBlock";

    private byte[] sendBuffer;

    public UpdateBinaryBlock(WriteParams params) {
        super(params);
    }

//    public UpdateBinaryBlock(NFCReader nfcReader, int block, String data) {
//        super(nfcReader);
//        this.block = block;
//        this.data = Util.toNFCByte(data, 16);
//    }
//
//    public UpdateBinaryBlock(NFCReader nfcReader, int block, byte[] data) {
//        super(nfcReader);
//        this.block = block;
//        this.data = data;
//    }

//    public OnGetResultListener listener;


    public String toDataString(Result result) {
        return Util.dataToString(result.getData());
    }

    public boolean run(TaskListener listener) {
        super.run(listener);
//        FF D6 00 04 10 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
        sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xD6, (byte) 0x0, (byte) 0x04, (byte) 0x10,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.getParams().getBlock();

        byte[] data = null;
        if (this.getParams().getBytes() != null) {
            Log.w(TAG, Util.toHexString(this.getParams().getBytes()));
            data = this.getParams().getBytes();
        } else {
            data = Util.toNFCByte(this.getParams().getData(), 16);
            Log.w(TAG, this.getParams().getData());
        }
        System.arraycopy(data, 0, sendBuffer, 5, 16);
        Log.d(TAG, Util.toHexString(sendBuffer));
        ACRReader acrReader = getParams().getReader().getReader();
        acrReader.transmit(0, sendBuffer, this);
        return true;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = new Result("UpdateBinaryBlock", len, bytes);
        if (result.isSuccess()) {
            result.setData(Arrays.copyOfRange(sendBuffer, 5, sendBuffer.length));
        } else {
            result = new Result("UpdateBinaryBlock", new ReaderException("Write data fail."));
        }
        result.setProcessor(this);
        result.setSendPlugin(true);
        if (getStopSession() == null) {
            if (this.getParams().getOnGetResultListener() != null) {
                this.getParams().getOnGetResultListener().onResult(result);
            }
        } else {
            getStopSession().setSendResult(result);
        }
        runTaskListener(result.isSuccess());
        return result.isSuccess();
    }

    @Override
    public boolean onError(ACRReaderException e) {
        Result result = new Result("UpdateBinaryBlock", e);
        if (this.getParams().getOnGetResultListener() != null) {
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }
}
