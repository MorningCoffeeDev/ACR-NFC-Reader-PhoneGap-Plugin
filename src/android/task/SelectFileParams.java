package com.frankgreen.task;

/**
 * Created by kevin on 6/5/15.
 */
public class SelectFileParams extends Params {
    private int slotNumber;
    private String aid;

    public SelectFileParams(int slotNumber, String aid) {
        this.slotNumber = slotNumber;
        this.aid = aid;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }
}
