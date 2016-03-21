package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.ReaderException;
import com.frankgreen.ATRHistorical;
import com.frankgreen.Chip;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.BaseParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 5/27/15.
 */
public class GetVersion extends Base<BaseParams> implements OnDataListener {
    private static final String TAG = "GetVersion";

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    private NFCReader nfcReader;

    public GetVersion(BaseParams params) {
        super(params);
    }

    public String toDataString(Result result) {
        Chip chip = Chip.find(result.getData());
        if (chip != null) {
            return chip.getName();
        } else {
            return "UNKNOWN";
        }
    }

    public boolean run(TaskListener listener) {
        super.run(listener);
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x01, (byte) 0x60};
        byte[] receiveBuffer = new byte[16];
//        Result result = Result.buildSuccessInstance(TAG);
        Log.d(TAG, Util.toHexString(sendBuffer));
        nfcReader = this.getParams().getReader();
        nfcReader.getReader().transmit(0, sendBuffer, this);

//        if (nfcReader.getChipMeta().canGetVersion()) {
//            int byteCount = 0;
//            try {
//                byteCount = nfcReader.getReader().transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
//                result = new Result(TAG, byteCount, receiveBuffer);
//                if (result.isSuccess()) {
//                    Chip chip = Chip.find(result.getData());
//                    if (chip != null) {
//                        nfcReader.getChipMeta().setName(chip.getName());
//                        nfcReader.getChipMeta().setType(chip.getType());
//                    } else {
//                        nfcReader.getChipMeta().setName(ATRHistorical.UNKNOWN);
//                        nfcReader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT_C);
//                    }
//                } else {
//                    nfcReader.getChipMeta().setName(ATRHistorical.UNKNOWN);
//                    nfcReader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT);
//                    Util.sleep(800);
//                }
//            } catch (ACRReaderException e) {
//                result = new Result("GetVersion", e);
//            }
//        } else {
//            result = new Result("GetVersion", new ReaderException("the chip does not support"));
//        }
//        result.setSendPlugin(this.isSendPlugin());
//        if (this.getParams().getOnGetResultListener() != null) {
//            result.setProcessor(this);
//            this.getParams().getOnGetResultListener().onResult(result);
//        }
//        return result.isSuccess();
        return true;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = Result.buildSuccessInstance(TAG);
        if (nfcReader.getChipMeta().canGetVersion()) {
            if (result.isSuccess()) {
                Chip chip = Chip.find(result.getData());
                if (chip != null) {
                    nfcReader.getChipMeta().setName(chip.getName());
                    nfcReader.getChipMeta().setType(chip.getType());
                } else {
                    nfcReader.getChipMeta().setName(ATRHistorical.UNKNOWN);
                    nfcReader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT_C);
                }
            } else {
                nfcReader.getChipMeta().setName(ATRHistorical.UNKNOWN);
                nfcReader.getChipMeta().setType(ATRHistorical.MIFARE_ULTRALIGHT);
                Util.sleep(800);
            }
        } else {
            result = new Result("GetVersion", new ReaderException("the chip does not support"));
        }
        result.setSendPlugin(this.isSendPlugin());
        if (this.getParams().getOnGetResultListener() != null) {
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
        runTaksListener();
        return result.isSuccess();
    }

    @Override
    public boolean onError(ACRReaderException e) {
        e.printStackTrace();
        Result result = new Result(TAG, e);
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return false;
    }
}
