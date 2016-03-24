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
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 5/27/15.
 */
public abstract class CardCommand extends Base<InitNTAGParams> implements OnDataListener{

    public CardCommand(InitNTAGParams params) {
        super(params);
    }

    protected boolean transmit(byte[] sendBuffer, OnDataListener listener){
        Log.d(getTag(), Util.toHexString(sendBuffer));
        ACRReader acrReader = this.getParams().getReader().getReader();
        acrReader.transmit(0, sendBuffer, listener);
        return true;
    }

    protected boolean transmit(byte[] sendBuffer){
        return transmit(sendBuffer,this);
    }

    protected abstract String getTag();

    protected abstract String getCommandName();

    public Result.Checker getChecker(){
        return null;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = new Result(getCommandName(), len, bytes);
        result.setSendPlugin(false);
        result.setChecker(getChecker());
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        runTaskListener(result.isSuccess());
        return result.isSuccess();
    }

    @Override
    public boolean onError(ACRReaderException e) {
        Result result = new Result(getCommandName(), e);
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }
}
