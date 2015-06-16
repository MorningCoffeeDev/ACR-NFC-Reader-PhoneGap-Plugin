package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;

/**
 * Created by kevin on 5/27/15.
 */
public class UID extends Base {
    public UID(NFCReader nfcReader) {
        super(nfcReader);
    }

    public OnGetResultListener listener;

    public boolean run(int slotNumber) throws ReaderException {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x0, (byte) 0x0, (byte) 0x0};
        byte[] receiveBuffer = new byte[16];

        Reader reader = getNfcReader().getReader();
        int byteCount = reader.transmit(slotNumber, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
        Result result = new Result("UID",byteCount, receiveBuffer);
        if (this.listener != null) {
            this.listener.onResult(result);
        }
        return result.isSuccess();
    }

    public void setOnGetResultListener(OnGetResultListener listener) {
        this.listener = listener;
    }
}
