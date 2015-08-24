package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.BaseParams;
import com.frankgreen.task.PICCOperatingParameterParams;

/**
 * Created by kevin on 8/24/15.
 */
public class PICCOperatingParameter extends Base<PICCOperatingParameterParams> {


    public PICCOperatingParameter(PICCOperatingParameterParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x51, this.getParams().getByteValue(), (byte) 0x0};
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("PICCOperatingParameter");

        NFCReader reader = this.getParams().getReader();
        int byteCount = 0;
        try {
             byteCount = reader.getReader().control(0, Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result("PICCOperatingParameter", byteCount, receiveBuffer);
        } catch (ReaderException e) {
            result = new Result("PICCOperatingParameter", e);
        }
        result.setSendPlugin(false);
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
