package com.frankgreen.apdu.command.card;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.Base;
import com.frankgreen.params.InitNTAGParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;

/**
 * Created by kevin on 5/27/15.
 */
public abstract class CardCommand extends Base<InitNTAGParams> {

    public CardCommand(InitNTAGParams params) {
        super(params);
    }

    protected boolean transmit(byte[] sendBuffer){
        byte[] receiveBuffer = new byte[64];
        Result result = Result.buildSuccessInstance(getCommandName());
        Log.d(getTag(), Util.toHexString(sendBuffer));
        ACRReader acrReader = this.getParams().getReader().getReader();
        int byteCount = 0;
        try {
            byteCount = acrReader.transmit(this.getParams().getSlotNumber(), sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            result = new Result(getCommandName(), byteCount, receiveBuffer);
        } catch (ACRReaderException e) {
            result = new Result(getCommandName(), e);
        }
        result.setSendPlugin(false);
        result.setChecker(getChecker());
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

    protected abstract String getTag();

    protected abstract String getCommandName();

    public Result.Checker getChecker(){
        return null;
    }

}
