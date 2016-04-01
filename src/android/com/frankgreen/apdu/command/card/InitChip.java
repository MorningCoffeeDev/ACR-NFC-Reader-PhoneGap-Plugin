package com.frankgreen.apdu.command.card;

import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.reader.ACRReaderException;
import com.frankgreen.reader.OnDataListener;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.InitNTAGParams;

/**
 * Created by kevin on 5/27/15.
 */
public class InitChip extends CardCommand {
    public InitChip(InitNTAGParams params) {
        super(params);
    }

    abstract class AbstractOnDataListener implements OnDataListener {
        @Override
        public boolean onError(ACRReaderException e) {
            return false;
        }
    }

//    final byte[] type = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
//            (byte) 0x2A, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
//    final byte[] password = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
//            (byte) 0x2B, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
//final byte[] pack = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
//        (byte) 0x2C, (byte) 0x33, (byte) 0x33, (byte) 0x00, (byte) 0x00};
//
//    final byte[] range = new byte[]{(byte) 0xFF, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x06, (byte) 0xA2,
//            (byte) 0x29, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x04};

    public boolean run(TaskListener listener) {
        super.run(listener);
        final byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xD6, (byte) 0x0, (byte) 0x29, (byte) 0x10,
                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x04, // 29 range
                (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, // 2a type
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // 2b password
                (byte) 0x33, (byte) 0x33, (byte) 0x00, (byte) 0x00}; // 2c pack

        if (this.getParams().getPassword() != null && !"".equals(this.getParams().getPassword())) {
            byte[] pwd = Util.convertHexAsciiToByteArray(this.getParams().getPassword(), 4);
            System.arraycopy(pwd, 0, bytes, 13, 4);
        }
        return transmit(bytes);
    }

//    @Override
//    public Result.Checker getChecker() {
//        return new Result.Checker() {
//            @Override
//            public boolean check(Result result) {
//                byte[] data = result.getData();
//                if (data != null && data.length > 0 && data[0] == (byte) 0x0a) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//    }

    @Override
    protected String getTag() {
        return "InitChip";
    }

    @Override
    protected String getCommandName() {
        return "InitChip";
    }
}
