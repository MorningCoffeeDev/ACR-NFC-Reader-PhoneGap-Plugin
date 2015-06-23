package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/10/15.
 */
public class AuthParams extends Params {
    private int slotNumber;
    private String keyA;
    private String keyB;
    private int block;
    private boolean A = true;

    public AuthParams(int slotNumber, int block) {
        this.slotNumber = slotNumber;
        this.block = block;
    }

    public boolean isA() {
        return A;
    }

    public void setA(boolean a) {
        A = a;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getKeyA() {
        return keyA;
    }

    public void setKeyA(String keyA) {
        this.keyA = keyA;
    }

    public String getKeyB() {
        return keyB;
    }

    public void setKeyB(String keyB) {
        this.keyB = keyB;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }
}
