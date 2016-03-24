package com.frankgreen.apdu.command;

import android.util.Log;

import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.reader.OnDataListener;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.SelectFileParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;

/**
 * Created by kevin on 5/27/15.
 */
public class SelectFile extends Base<SelectFileParams> implements OnDataListener {
    private static final String TAG = "SelectFile";

    public SelectFile(SelectFileParams params) {
        super(params);
    }


    public String toDataString(Result result) {
        return Util.dataToString(result.getData());
    }

    public boolean run(TaskListener listener) {
//        00 A4 04 00 02 F0 01
        byte[] header = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x00};
        byte[] aid = Util.HexStringToByteArray(this.getParams().getAid());
        header[4] = (byte) (aid.length);

        byte[] sendBuffer = Util.ConcatArrays(header, aid);
        Log.d(TAG, Util.toHexString(sendBuffer));
        ACRReader acrReader = getParams().getReader().getReader();
        acrReader.transmit(0, sendBuffer, this);
        return true;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = new Result(TAG, len, bytes);
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
            result.setProcessor(this);
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }
}
