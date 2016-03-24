package com.frankgreen.apdu.command.card;

import com.frankgreen.task.TaskListener;
import com.frankgreen.params.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class StopSession extends CardCommand {

    public StopSession(InitNTAGParams params) {
        super(params);
    }

    public synchronized boolean run(TaskListener listener) {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xC2, (byte) 0x0, (byte) 0x0, (byte) 0x02, (byte) 0x82, (byte) 0x00};
        this.getParams().getReader().clearSessionStartedAt();
        return transmit(sendBuffer);
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
