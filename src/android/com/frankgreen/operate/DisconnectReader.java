package com.frankgreen.operate;

import com.acs.smartcard.ReaderException;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.Base;
import com.frankgreen.params.DisconnectParams;

/**
 * Created by cain on 16/4/26.
 */
public class DisconnectReader extends Base<DisconnectParams> implements OperateDataListener {

    public DisconnectReader(DisconnectParams disconnectParams) {
        super(disconnectParams);
    }

    @Override
    public boolean run() {
        this.getParams().getReader().getReader().disconnectReader(this);
        return true;
    }

    @Override
    public boolean onData(OperateResult operateResult) {
        this.getParams().getOnGetResultListener().onResult(new Result("Disconnect", "Disconnect Success!"));
        return true;
    }

    @Override
    public boolean onError(OperateResult operateResult) {
        this.getParams().getOnGetResultListener().onResult(new Result("Disconnect", new ReaderException(operateResult.getResultMessage())));
        return false;
    }
}
