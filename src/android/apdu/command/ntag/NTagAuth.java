package com.frankgreen.apdu.command.ntag;

import com.frankgreen.Util;
import com.frankgreen.task.BaseParams;
import com.frankgreen.task.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class NTagAuth extends CardCommand {
    public NTagAuth(InitNTAGParams params) {
        super(params);
    }

    private byte[] sendBuffer = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x05,
            (byte) 0x1B, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};


    public boolean tryPassword() {
        if (transmit(sendBuffer)) { // use default password
            return true;
        }
        if (this.getParams().getPassword() != null && !"".equals(this.getParams().getPassword())) // use custom password
        {
           Util.sleep(500);
            byte[] pwd = Util.toNFCByte(this.getParams().getPassword(), 4);
            System.arraycopy(pwd, 0, sendBuffer, 6, 4);
            return transmit(sendBuffer);
        }
        return false;
    }

    public void initPassword() {
        if (this.getParams().getPassword() != null && !"".equals(this.getParams().getPassword())) // use custom password
        {

            byte[] pwd = Util.toNFCByte(this.getParams().getPassword(), 4);
            System.arraycopy(pwd, 0, sendBuffer, 6, 4);
        }
    }

    public boolean run() {
        return transmit(sendBuffer);
    }

    @Override
    protected String getTag() {
        return "NTagAuth";
    }

    @Override
    protected String getCommandName() {
        return "NTagAuth";
    }

}
