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
public class StartSession extends CardCommand {
    public StartSession(InitNTAGParams params) {
        super(params);
    }

    public boolean run() {
        byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0xC2, (byte) 0x0, (byte) 0x0, (byte) 0x02, (byte) 0x81,(byte) 0x00};
        this.getParams().getReader().setSessionStartedAt(System.currentTimeMillis());
        return  transmit(sendBuffer);
    }

    @Override
    protected String getTag() {
        return "StartSession";
    }

    @Override
    protected String getCommandName() {
        return "StartSession";
    }

}
