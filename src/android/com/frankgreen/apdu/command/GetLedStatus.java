package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.BaseParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by Kevin on 16/3/1.
 */
public class GetLedStatus extends Base<BaseParams> implements OnDataListener {

    private static final String TAG = "GetLedStatus";

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public GetLedStatus(BaseParams params) {
        super(params);
    }

    public String toDataString(Result result) {
        byte[] data = new byte[1];
        System.arraycopy(result.getData().clone(), 5, data, 0, 1);
        return Util.toHexString(data);
    }

    public boolean run(TaskListener listener) {
        byte[] sendBuffer = new byte[]{(byte) 0xE0, (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x00};
        byte[] receiveBuffer = new byte[30];
        Result result = Result.buildSuccessInstance(TAG);

        ACRReader acrReader = this.getParams().getReader().getReader();
        acrReader.control(0, sendBuffer, this);
//        int byteCount;
//        try {
//            byteCount = acrReader.control(0, Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
//            result.setData(receiveBuffer, byteCount);
//        } catch (ACRReaderException e) {
//            result = new Result(TAG, e);
//            e.printStackTrace();
//        }
//
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
        result.setData(bytes, len);
        if (this.getParams().getOnGetResultListener() != null) {
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
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
