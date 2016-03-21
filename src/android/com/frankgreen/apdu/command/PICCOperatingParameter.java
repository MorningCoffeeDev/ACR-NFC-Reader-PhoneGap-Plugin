package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
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
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x51, this.getParams().getByteValue(), (byte) 0x0};
        byte[] receiveBuffer = new byte[16];
//        Result result = Result.buildSuccessInstance("PICCOperatingParameter");

        ACRReader acrReader = this.getParams().getReader().getReader();
        acrReader.control(0, sendBuffer, this);
//        int byteCount = 0;
//        try {
//             byteCount = acrReader.control(0, Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
//            result = new Result("PICCOperatingParameter", byteCount, receiveBuffer);
//        } catch (ACRReaderException e) {
//            result = new Result("PICCOperatingParameter", e);
//        }
//        result.setSendPlugin(false);
//        if (this.getParams().getOnGetResultListener() != null) {
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
