package com.frankgreen.reader;

/**
 * Created by kevin on 16/3/18.
 */
public interface OnDataListener {
    boolean onData(byte[] bytes, int len);
    boolean onError(ACRReaderException e);
}
