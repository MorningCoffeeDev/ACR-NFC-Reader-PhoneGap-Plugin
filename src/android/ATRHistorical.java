package com.frankgreen;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 8/11/15.
 */
public class ATRHistorical {
    private byte[] bytes;
    private ATR atr;
    private final byte[] JAVA_CARD = new byte[]{(byte) 0x3B, (byte) 0x80, (byte) 0x80, (byte) 0x01, (byte) 0x01};

    private static final Map<String, byte[]> mapping;

    static {
        mapping = new HashMap<String, byte[]>();
        mapping.put("Mifare 1K", new byte[]{(byte) 0x00, (byte) 0x01});
        mapping.put("Mifare 4K", new byte[]{(byte) 0x00, (byte) 0x02});
        mapping.put("Mifare Ultralight", new byte[]{(byte) 0x00, (byte) 0x03});
        mapping.put("Mifare Mini", new byte[]{(byte) 0x00, (byte) 0x26});
        mapping.put("Topaz and Jewel", new byte[]{(byte) 0xF0, (byte) 0x04});
        mapping.put("FeliCa 212K", new byte[]{(byte) 0xF0, (byte) 0x11});
        mapping.put("FeliCa 424K", new byte[]{(byte) 0xF0, (byte) 0x12});
        mapping.put("JCOP 30", new byte[]{(byte) 0xF0, (byte) 0x28});
        mapping.put("Mifare Ultralight C", new byte[]{(byte) 0x00, (byte) 0x3A});
        mapping.put("Mifare Plus SL1 2K", new byte[]{(byte) 0x00, (byte) 0x36});
        mapping.put("Mifare Plus SL1 4K", new byte[]{(byte) 0x00, (byte) 0x37});
        mapping.put("Mifare Plus SL2 2K", new byte[]{(byte) 0x00, (byte) 0x38});
        mapping.put("Mifare Plus SL2 4K", new byte[]{(byte) 0x00, (byte) 0x39});
        mapping.put("FeliCa", new byte[]{(byte) 0x00, (byte) 0x3B});
    }

    ATRHistorical(byte[] bytes) {
        this.bytes = bytes;
        this.atr = new ATR(bytes);
    }

    public String getType() {
        if (Arrays.equals(this.bytes, JAVA_CARD)) {
            return "JavaCard";
        }
        byte[] historicalBytes = atr.getHistoricalBytes();
        Log.d("ATRHistorical", Util.toHexString(historicalBytes));
        if (historicalBytes != null && historicalBytes.length > 7) {
            byte[] type = Arrays.copyOfRange(historicalBytes, 5, 7);
            Log.d("ATRHistorical", Util.toHexString(type));
            for (Map.Entry<String, byte[]> entry : mapping.entrySet()) {
                String name = entry.getKey();
                byte[] value = entry.getValue();
                if (Arrays.equals(type, value)) {
                    return name;
                }
            }
        }
        return "Unknown";
    }


}
