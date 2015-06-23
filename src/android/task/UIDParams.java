package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/5/15.
 */
public class UIDParams  extends Params {
    private NFCReader reader;
    private int slotNumber;

    public UIDParams( int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }
}
