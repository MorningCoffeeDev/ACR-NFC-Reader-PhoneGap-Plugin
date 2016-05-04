package com.frankgreen.apdu.command;

import com.frankgreen.apdu.Result;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.PICCOperatingParameterParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 8/24/15.
 */
public class PICCOperatingParameter extends Base<PICCOperatingParameterParams> implements OnDataListener {

    private static final String TAG = "PICCOperatingParameter";
    public PICCOperatingParameter(PICCOperatingParameterParams params) {
        super(params);
    }

    public boolean run(TaskListener listener) {
        super.run(listener);
        byte[] sendBuffer = new byte[]{(byte) 0xE0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x01, this.getParams().getByteValue()};

        ACRReader acrReader = this.getParams().getReader().getReader();
        acrReader.control(0, sendBuffer, this);
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
        runTaskListener(result.isSuccess());
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
