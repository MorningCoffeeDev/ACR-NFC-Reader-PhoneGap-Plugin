package com.frankgreen.apdu.command;

import com.frankgreen.NFCReader;

/**
 * Created by kevin on 5/27/15.
 */
public class Base {
    private NFCReader nfcReader;

    public Base(NFCReader nfcReader) {
        this.nfcReader = nfcReader;
    }

    public NFCReader getNfcReader() {
        return nfcReader;
    }

    public void setNfcReader(NFCReader nfcReader) {
        this.nfcReader = nfcReader;
    }
}
