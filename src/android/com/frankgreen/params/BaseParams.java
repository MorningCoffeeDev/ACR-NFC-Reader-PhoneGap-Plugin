package com.frankgreen.params;

/**
 * Created by kevin on 6/5/15.
 */
public class BaseParams extends Params {
    private int slotNumber = 0;

    public BaseParams(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }
}
