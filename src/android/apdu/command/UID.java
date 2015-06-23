package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.UIDParams;

/**
 * Created by kevin on 5/27/15.
 */
public class UID extends Base<UIDParams> {


    public UID(UIDParams params) {
        super(params);
    }

    public boolean run() throws ReaderException {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x0, (byte) 0x0, (byte) 0x0};
        byte[] receiveBuffer = new byte[16];

        Reader reader = this.getParams().getReader().getReader();
        int byteCount = reader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
        Result result = new Result("UID",byteCount, receiveBuffer);
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
