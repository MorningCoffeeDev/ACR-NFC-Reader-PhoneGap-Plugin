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

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public boolean run() {
//         FFB0000410
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xB0, (byte) 0x0, (byte) 0x04, (byte) 0x10};
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.getParams().getBlock();
        Log.d(TAG, Util.toHexString(sendBuffer));
        NFCReader reader = this.getParams().getReader();
        Result result;
        try {
            int byteCount = reader.getReader().transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("ReadBinaryBlock", byteCount, receiveBuffer);
            if(result.isSuccess() && getParams().getBlock() == 0){
                reader.getChipMeta().parseBlock0(result.getData());
            }
        } catch (ReaderException e) {
            result = new Result("ReadBinaryBlock", e);

            e.printStackTrace();
        }
        result.setSendPlugin(this.isSendPlugin());
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
