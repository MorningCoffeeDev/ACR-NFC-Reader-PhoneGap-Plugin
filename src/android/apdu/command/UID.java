package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.BaseParams;

/**
 * Created by kevin on 5/27/15.
 */
public class UID extends Base<BaseParams> {

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public UID(BaseParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x0, (byte) 0x0, (byte) 0x0};
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("UID");

        NFCReader reader = this.getParams().getReader();
        int byteCount = 0;
        try {
            byteCount = reader.getReader().transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("UID", byteCount, receiveBuffer);
            reader.getChipMeta().setUID(result.getData());
        } catch (ReaderException e) {
            result = new Result("UID", e);
        }
        result.setSendPlugin(this.isSendPlugin());
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
