package com.frankgreen.apdu.command;

import android.util.Log;

import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.ReadParams;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 5/27/15.
 */
public class ReadBinaryBlock extends Base<ReadParams> implements OnDataListener {
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

    public String toDataString(Result result) {
        return Util.dataToString(result.getData());
    }

    private NFCReader nfcReader;

    public boolean run(TaskListener listener) {
        super.run(listener);
//         FFB0000410
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xB0, (byte) 0x0, (byte) 0x04, (byte) 0x10};
        byte[] receiveBuffer = new byte[300];
        sendBuffer[3] = (byte) this.getParams().getBlock();
        Log.d(TAG, Util.toHexString(sendBuffer));
        nfcReader = this.getParams().getReader();
        nfcReader.getReader().transmit(0, sendBuffer, this);
        return true;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = new Result(TAG, len, bytes);
        if(result.isSuccess() && getParams().getBlock() == 0){
            nfcReader.getChipMeta().parseBlock0(result.getData());
        }
        result.setSendPlugin(this.isSendPlugin());
        result.setProcessor(this);
        if(getStopSession() == null){
            if (this.getParams().getOnGetResultListener() != null) {
                this.getParams().getOnGetResultListener().onResult(result);
            }
        }else{
            getStopSession().setSendResult(result);
        }
        runTaskListener(result.isSuccess());
        return result.isSuccess();
    }

    @Override
    public boolean onError(ACRReaderException e) {
        e.printStackTrace();
        Result result = new Result(TAG, e);
        result.setSendPlugin(this.isSendPlugin());
        if (this.getParams().getOnGetResultListener() != null) {
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return false;
    }
}
