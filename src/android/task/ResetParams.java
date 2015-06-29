package com.frankgreen.task;

/**
 * Created by kevin on 6/5/15.
 */
public class ResetParams extends Params {
    private int slotNumber;

    public ResetParams(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }
}
