package com.frankgreen.apdu.command.ntag;

import android.util.Log;

import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class InitChip extends CardCommand {
    public InitChip(InitNTAGParams params) {
        super(params);
    }

    public boolean run() {
        byte[] type = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
                (byte) 0x2A, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] password = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
                (byte) 0x2B, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        if(this.getParams().getPassword() != null && !"".equals(this.getParams().getPassword())) {
            byte[] pwd = Util.convertHexAsciiToByteArray(this.getParams().getPassword(), 4);
            System.arraycopy(pwd, 0, password, 7, 4);
        }
        byte[] pack = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
                (byte) 0x2C, (byte) 0x33, (byte) 0x33, (byte) 0x00, (byte) 0x00};

        byte[] range = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
                (byte) 0x29, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x04};

        if (transmit(type) && transmit(password) && transmit(pack) && transmit(range)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Result.Checker getChecker() {
        return new Result.Checker() {
            @Override
            public boolean check(Result result) {
                byte[] data = result.getData();
                Log.d(getTag(), Util.toHexString(data));
                if (data != null && data.length > 0 && data[0] == (byte) 0x0a) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    protected String getTag() {
        return "InitChip";
    }

    @Override
    protected String getCommandName() {
        return "InitChip";
    }

}
