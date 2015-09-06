package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.ATRHistorical;
import com.frankgreen.Chip;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.BaseParams;

/**
 * Created by kevin on 5/27/15.
 */
public class GetVersion extends Base<BaseParams> {
    private static final String TAG = "GetVersion";

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public GetVersion(BaseParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x01, (byte) 0x60};
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("GetVersion");
        Log.d(TAG, Util.toHexString(sendBuffer));
        NFCReader reader = this.getParams().getReader();
        if (reader.getChipMeta().canGetVersion()) {
            int byteCount = 0;
            try {
                byteCount = reader.getReader().transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
                result = new Result("GetVersion", byteCount, receiveBuffer);
                if (result.isSuccess()) {
                    Chip chip = Chip.find(result.getData());
                    if (chip != null) {
                        reader.getChipMeta().setName(chip.getName());
                        reader.getChipMeta().setType(chip.getType());
                    } else {
                        reader.getChipMeta().setName(ATRHistorical.UNKNOWN);
                        reader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT);
                    }
                } else {
                    reader.getChipMeta().setName(ATRHistorical.UNKNOWN);
                    reader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT);
                    Util.sleep(800);
                }
            } catch (ReaderException e) {
                result = new Result("GetVersion", e);
            }
        } else {
            result = new Result("GetVersion", new ReaderException("the chip does not support"));
        }
        result.setSendPlugin(this.isSendPlugin());
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
