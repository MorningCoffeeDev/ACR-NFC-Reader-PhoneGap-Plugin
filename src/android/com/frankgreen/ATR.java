package com.frankgreen;

import java.util.Arrays;

/**
 * Created by kevin on 7/2/15.
 */
public class ATR {
    private byte[] atr;

    private transient int startHistorical, nHistorical;
    public ATR(byte[] atr) {
        this.atr = atr.clone();
        parse();
    }

    private void parse() {
        if (atr.length < 2) {
            return;
        }
        if ((atr[0] != 0x3b) && (atr[0] != 0x3f)) {
            return;
        }
        int t0 = (atr[1] & 0xf0) >> 4;
        int n = atr[1] & 0xf;
        int i = 2;
        while ((t0 != 0) && (i < atr.length)) {
            if ((t0 & 1) != 0) {
                i++;
            }
            if ((t0 & 2) != 0) {
                i++;
            }
            if ((t0 & 4) != 0) {
                i++;
            }
            if ((t0 & 8) != 0) {
                if (i >= atr.length) {
                    return;
                }
                t0 = (atr[i++] & 0xf0) >> 4;
            } else {
                t0 = 0;
            }
        }
        int k = i + n;
        if ((k == atr.length) || (k == atr.length - 1)) {
            startHistorical = i;
            nHistorical = n;
        }
    }

    /**
     * Returns a copy of the bytes in this ATR.
     *
     * @return a copy of the bytes in this ATR.
     */
    public byte[] getBytes() {
        return atr.clone();
    }

    /**
     * Returns a copy of the historical bytes in this ATR.
     * If this ATR does not contain historical bytes, an array of length
     * zero is returned.
     *
     * @return a copy of the historical bytes in this ATR.
     */
    public byte[] getHistoricalBytes() {
        byte[] b = new byte[nHistorical];
        System.arraycopy(atr, startHistorical, b, 0, nHistorical);
        return b;
    }

    /**
     * Returns a string representation of this ATR.
     *
     * @return a String representation of this ATR.
     */
    public String toString() {
        return "ATR: " + atr.length + " bytes";
    }

    /**
     * Compares the specified object with this ATR for equality.
     * Returns true if the given object is also an ATR and its bytes are
     * identical to the bytes in this ATR.
     *
     * @param obj the object to be compared for equality with this ATR
     * @return true if the specified object is equal to this ATR
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ATR == false) {
            return false;
        }
        ATR other = (ATR)obj;
        return Arrays.equals(this.atr, other.atr);
    }

    /**
     * Returns the hash code value for this ATR.
     *
     * @return the hash code value for this ATR.
     */
    public int hashCode() {
        return Arrays.hashCode(atr);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        atr = (byte[])in.readUnshared();
        parse();
    }
}
