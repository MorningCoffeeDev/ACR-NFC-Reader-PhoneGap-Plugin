package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.BaseParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;

/**
 * Created by kevin on 5/27/15.
 */
public class Reset extends Base<BaseParams> implements OnDataListener {

    private NFCReader reader;

    public Reset(BaseParams params) {
        super(params);
    }

    private boolean sendPlugin = true;

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public String toDataString(Result result) {
        return result.getMeta().getUid();
    }

    public boolean run(TaskListener listener) {
        super.run(listener);
//        BaseParams BaseParams = this.getParams();
//        int slotNumber = BaseParams.getSlotNumber();
        reader = getParams().getReader();
//        try {
        byte[] atr = reader.getReader().power(getParams().getSlotNumber(), Reader.CARD_WARM_RESET, this);
//            if (atr != null) {
//                result.setData(atr);
//                reader.getChipMeta().parseATR(atr);
//                reader.getReader().setProtocol(slotNumber, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
//            }
//        } catch (ACRReaderException e) {

//        }
//        result.setSendPlugin(isSendPlugin());
//        if (BaseParams.getOnGetResultListener() != null) {
//            BaseParams.getOnGetResultListener().onResult(result);
//        }
        return true;
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = Result.buildSuccessInstance("Reset");
        result.setData(bytes);
        reader.getChipMeta().parseATR(bytes);
        reader.getReader().setProtocol(this.getParams().getSlotNumber(), Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
        result.setSendPlugin(isSendPlugin());
        if (getParams().getOnGetResultListener() != null) {
            getParams().getOnGetResultListener().onResult(result);
        }
        runTaksListener();
        return false;
    }

    @Override
    public boolean onError(ACRReaderException e) {
        Result result = new Result("Reset", e);
        result.setSendPlugin(isSendPlugin());
        if (getParams().getOnGetResultListener() != null) {
            getParams().getOnGetResultListener().onResult(result);
        }
        return false;
    }
}
