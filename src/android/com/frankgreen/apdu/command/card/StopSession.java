package com.frankgreen.apdu.command.card;

import com.frankgreen.apdu.Result;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class StopSession extends CardCommand {

    Result sendResult = null;
    public StopSession(InitNTAGParams params) {
        super(params);
    }

    public void setSendResult(Result sendResult) {
        this.sendResult = sendResult;
    }

    public synchronized boolean run(TaskListener listener) {
        super.run(listener);
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xC2, (byte) 0x0, (byte) 0x0, (byte) 0x02, (byte) 0x82, (byte) 0x00};
        this.getParams().getReader().clearSessionStartedAt();
        return transmit(sendBuffer);
    }

    @Override
    public boolean onData(byte[] bytes, int len) {
        Result result = new Result(getCommandName(), len, bytes);
        result.setChecker(getChecker());
        result.setSendPlugin(false);
        if (this.getParams().getOnGetResultListener() != null) {
            this.getParams().getOnGetResultListener().onResult(result);
        }
        if(this.sendResult != null) {
            if (this.getParams().getOnGetResultListener() != null) {
                this.getParams().getOnGetResultListener().onResult(sendResult);
            }
        }
        runTaskListener(result.isSuccess());
        return result.isSuccess();
    }

    @Override
    protected String getTag() {
        return "StopSession";
    }

    @Override
    protected String getCommandName() {
        return "StopSession";
    }

}
