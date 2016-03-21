package com.frankgreen.apdu.command;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.DisplayParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 5/27/15.
 */
public class Display extends Base<DisplayParams> implements OnDataListener{

    private static final String TAG = "Display" ;

    public Display(DisplayParams params) {
        super(params);
    }

    public boolean run(TaskListener listener) {
//       FF 00 68 00 02 31 32
        byte[] sendBuffer = new byte[]{(byte) 0xFF, this.getParams().getOption(), (byte) 0x68, this.getParams().getXY(),
                (byte) 0x0F,//length
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0, (byte) 0x00, (byte) 0x00
        };
        byte[] receiveBuffer = new byte[16];
//        Result result = Result.buildSuccessInstance("Display");
//        if (this.getParams().getMessage() != null) {
            byte []  msg = Util.toNFCByte(this.getParams().getMessage(),16);

            System.arraycopy(msg, 0, sendBuffer, 5, 16);
//            Log.d(TAG, Util.toHexString(sendBuffer));
            ACRReader acrReader = this.getParams().getReader().getReader();
            acrReader.control(0, sendBuffer, this);
//            try {
//                int byteCount = acrReader.control(0,Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
//                result = new Result("Display", byteCount, receiveBuffer);
//            } catch (ACRReaderException e) {
//                result = new Result("Display", e);
//                e.printStackTrace();
//            }
//        }
//
//        if (this.getParams().getOnGetResultListener() != null) {
//            this.getParams().getOnGetResultListener().onResult(result);
//        }
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
