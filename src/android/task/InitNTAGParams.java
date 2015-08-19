package com.frankgreen.task;

/**
 * Created by kevin on 6/5/15.
 */
public class InitNTAGParams extends Params {
    private int slotNumber = 0;
    private String password = "";
    private String oldPassword = "";

    public InitNTAGParams(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
