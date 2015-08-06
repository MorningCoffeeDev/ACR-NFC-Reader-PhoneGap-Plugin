package com.frankgreen.apdu.command.ntag;

import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.Base;
import com.frankgreen.task.BaseParams;
import com.frankgreen.task.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class StopSession extends CardCommand {

    public StopSession(InitNTAGParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xC2, (byte) 0x0, (byte) 0x0, (byte) 0x02, (byte) 0x82, (byte) 0x00};
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
