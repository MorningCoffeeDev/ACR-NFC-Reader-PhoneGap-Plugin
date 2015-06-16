package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/8/15.
 */
public class WriteParams {
    private NFCReader reader;
    private int slotNumber;
    private String data;
    private int block;

    public WriteParams(NFCReader reader, int slotNumber, int block, String data) {
        this.reader = reader;
        this.slotNumber = slotNumber;
        this.block = block;
        this.data = data;
    }
    private OnGetResultListener onGetResultListener;

    public OnGetResultListener getOnGetResultListener() {
        return onGetResultListener;
    }

    public void setOnGetResultListener(OnGetResultListener onGetResultListener) {
        this.onGetResultListener = onGetResultListener;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
