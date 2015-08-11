package com.frankgreen.apdu.command;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.BaseParams;

/**
 * Created by kevin on 5/27/15.
 */
public class Reset extends Base<BaseParams> {


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
    public boolean run() {
        BaseParams BaseParams = this.getParams();
        int slotNumber = BaseParams.getSlotNumber();
        NFCReader reader = BaseParams.getReader();
        Result result = Result.buildSuccessInstance("Reset");
        try {
            byte[] atr = reader.getReader().power(slotNumber, Reader.CARD_WARM_RESET);
            if (atr != null) {
                result.setData(atr);
                reader.getChipMeta().parseATR(atr);
                reader.getReader().setProtocol(slotNumber, Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1);
            }
        } catch (ReaderException e) {
            result = new Result("Reset", e);
        }
        result.setSendPlugin(isSendPlugin());
        if (BaseParams.getOnGetResultListener() != null) {
            BaseParams.getOnGetResultListener().onResult(result);
        }
        return result.isSuccess();
    }

}
