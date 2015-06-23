package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/8/15.
 */
public class ReadParams extends Params {
    private int slotNumber = 0;
    private int block = 4;

    public ReadParams(int slotNumber,int block) {
        this.slotNumber = slotNumber;
        this.block = block;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

}
