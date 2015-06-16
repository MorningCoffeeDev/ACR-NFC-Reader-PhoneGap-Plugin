package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/5/15.
 */
public class UIDParams {
    private NFCReader reader;
    private int slotNumber;

    public UIDParams(NFCReader reader, int slotNumber) {
        this.reader = reader;
        this.slotNumber = slotNumber;
    }
    private OnGetResultListener onGetResultListener;

    public OnGetResultListener getOnGetResultListener() {
        return onGetResultListener;
    }

    public void setOnGetResultListener(OnGetResultListener onGetResultListener) {
        this.onGetResultListener = onGetResultListener;
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
}
