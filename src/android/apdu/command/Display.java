package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.DisplayParams;
import com.frankgreen.task.Params;

/**
 * Created by kevin on 5/27/15.
 */
public class Display extends Base<DisplayParams> {

    private static final String TAG = "Display" ;

    public Display(DisplayParams params) {
        super(params);
    }

    public boolean run() {
//       FF 00 68 00 02 31 32
        byte[] sendBuffer = new byte[]{(byte) 0xFF, this.getParams().getOption(), (byte) 0x68, this.getParams().getXY(),
                (byte) 0x0F,//length
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00
        };
        byte[] receiveBuffer = new byte[16];
        Result result = Result.buildSuccessInstance("Display");
        if (this.getParams().getMessage() != null) {
            byte []  msg = Util.toNFCByte(this.getParams().getMessage(),16);

            System.arraycopy(msg, 0, sendBuffer, 5, 16);
            Log.d(TAG, Util.toHexString(sendBuffer));
            Reader reader = this.getParams().getReader().getReader();
            try {
                int byteCount = reader.control(0,Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
                result = new Result("Display", byteCount, receiveBuffer);
            } catch (ReaderException e) {
                result = new Result("Display", e);
                e.printStackTrace();
            }
        }

        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
