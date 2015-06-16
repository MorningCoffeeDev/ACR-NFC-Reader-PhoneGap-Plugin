package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/10/15.
 */
public class AuthParams {
    private NFCReader reader;
    private int slotNumber;
    private String keyA;
    private String keyB;
    private int block;
    private OnGetResultListener onGetResultListener;

    public OnGetResultListener getOnGetResultListener() {
        return onGetResultListener;
    }

    public void setOnGetResultListener(OnGetResultListener onGetResultListener) {
        this.onGetResultListener = onGetResultListener;
    }

    public AuthParams(NFCReader reader, int slotNumber, int block) {
        this.reader = reader;
        this.slotNumber = slotNumber;
        this.block = block;
    }

    public NFCReader getReader() {
        return reader;
    }

    public void setReader(NFCReader reader) {
        this.reader = reader;
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
