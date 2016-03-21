package com.frankgreen.apdu.command;

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
 * Created by kevin on 16/2/26.
 */
public class GetReceivedData extends Base<BaseParams> implements OnDataListener {
    private static final String TAG = "GetReceivedData";

    private boolean sendPluguin = true;

    public boolean isSendPluguin() {
        return sendPluguin;
    }

    public void setSendPluguin(boolean sendPluguin) {
        this.sendPluguin = sendPluguin;
    }

    public GetReceivedData(BaseParams params) {
        super(params);
    }

    public String toDataString(Result result){
        byte[] data = new byte[result.getSize()];
        System.arraycopy(result.getData().clone(), 5, data, 0, result.getSize() - 5);
        return Util.dataToString(data);
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        return false;
    }

    @Override
    public boolean onError(ACRReaderException e) {
        return false;
    }

    public boolean run(TaskListener listener) {
        byte[] sendBuffer = new byte[]{(byte) 0xE0, (byte) 0x00, (byte) 0x00, (byte) 0x58, (byte) 0x00};
        byte[] receiveBuffer = new byte[30];
        Result result = Result.buildSuccessInstance(TAG);

        ACRReader acrReader = this.getParams().getReader().getReader();
        int byteCount;
        try {
            byteCount = acrReader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result(TAG, byteCount, receiveBuffer);
        } catch (ACRReaderException e) {
            result = new Result(TAG, e);
            e.printStackTrace();
        }

        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }

        return result.isSuccess();
    }
}
