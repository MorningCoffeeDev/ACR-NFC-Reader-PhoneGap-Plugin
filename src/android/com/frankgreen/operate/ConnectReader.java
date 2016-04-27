package com.frankgreen.operate;

import com.acs.smartcard.ReaderException;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.Base;
import com.frankgreen.params.ConnectParams;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cain on 16/4/26.
 */
public class ConnectReader extends Base<ConnectParams> implements OperateDataListener {
    public ConnectReader(ConnectParams params) {
        super(params);
    }

    @Override
    public boolean run() {
        String mDeviceAddress = this.getParams().getmDeviceAddress();
        return this.getParams().getReader().getReader().connect(mDeviceAddress, this);
    }

    @Override
    public boolean onData(OperateResult operateResult) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", operateResult.getCustomDevice().getName());
            json.put("address", operateResult.getCustomDevice().getAddress());
            this.getParams().getCallbackContext().success(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onError(OperateResult operateResult) {
        this.getParams().getOnGetResultListener().onResult(new Result("Connect", new ReaderException(operateResult.getResultMessage())));
        return false;
    }
}
