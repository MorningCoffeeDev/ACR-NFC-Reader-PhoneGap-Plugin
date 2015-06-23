package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/23/15.
 */
abstract public class Params {
    private NFCReader reader;
    private OnGetResultListener onGetResultListener;

    public NFCReader getReader() {
        return reader;
    }

    public void setReader(NFCReader reader) {
        this.reader = reader;
    }

    public OnGetResultListener getOnGetResultListener() {
        return onGetResultListener;
    }

    public void setOnGetResultListener(OnGetResultListener onGetResultListener) {
        this.onGetResultListener = onGetResultListener;
    }
}
