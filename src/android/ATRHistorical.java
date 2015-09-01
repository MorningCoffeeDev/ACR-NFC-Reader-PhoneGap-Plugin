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
    private final byte[] JAVA_CARD_BYTES = new byte[]{(byte) 0x3B, (byte) 0x80, (byte) 0x80, (byte) 0x01, (byte) 0x01};
    private final byte[] JAVA_SECURE_ELEMENT_CARD_BYTES = new byte[]{(byte) 0x3B, (byte) 0x8A, (byte) 0x80, (byte) 0x01,
            (byte) 0x00, (byte) 0x31, (byte) 0xC1, (byte) 0x73,
            (byte) 0xC8, (byte) 0x40, (byte) 0x00, (byte) 0x00,
            (byte) 0x90, (byte) 0x00, (byte) 0x90};

    public static final String MIFARE_1K = "Mifare 1K";
    public static final String MIFARE_4K = "Mifare 4K";
    public static final String MIFARE_ULTRALIGHT = "Mifare Ultralight";
    public static final String MIFARE_MINI = "Mifare Mini";
    public static final String TOPAZ_AND_JEWEL = "Topaz and Jewel";
    public static final String FELICA_212K = "FeliCa 212K";
    public static final String FELICA_424K = "FeliCa 424K";
    public static final String JCOP_30 = "JCOP 30";
    public static final String MIFARE_ULTRALIGHT_C = "Mifare Ultralight C";
    public static final String MIFARE_PLUS_SL1_2K = "Mifare Plus SL1 2K";
    public static final String MIFARE_PLUS_SL1_4K = "Mifare Plus SL1 4K";
    public static final String MIFARE_PLUS_SL2_2K = "Mifare Plus SL2 2K";
    public static final String MIFARE_PLUS_SL2_4K = "Mifare Plus SL2 4K";
    public static final String FELICA = "FeliCa";
    public static final String JAVA_CARD = "JavaCard";
    public static final String UNKNOWN = "Unknown";

    private static final Map<String, byte[]> mapping;

    static {
        mapping = new HashMap<String, byte[]>();
        mapping.put(MIFARE_1K, new byte[]{(byte) 0x00, (byte) 0x01});
        mapping.put(MIFARE_4K, new byte[]{(byte) 0x00, (byte) 0x02});
        mapping.put(MIFARE_ULTRALIGHT, new byte[]{(byte) 0x00, (byte) 0x03});
        mapping.put(MIFARE_MINI, new byte[]{(byte) 0x00, (byte) 0x26});
        mapping.put(TOPAZ_AND_JEWEL, new byte[]{(byte) 0x00, (byte) 0x30});
        mapping.put(FELICA_212K, new byte[]{(byte) 0xF0, (byte) 0x11});
        mapping.put(FELICA_424K, new byte[]{(byte) 0xF0, (byte) 0x12});
        mapping.put(JCOP_30, new byte[]{(byte) 0xFF, (byte) 0x28});
        mapping.put(MIFARE_ULTRALIGHT_C, new byte[]{(byte) 0x00, (byte) 0x3A});
        mapping.put(MIFARE_PLUS_SL1_2K, new byte[]{(byte) 0x00, (byte) 0x36});
        mapping.put(MIFARE_PLUS_SL1_4K, new byte[]{(byte) 0x00, (byte) 0x37});
        mapping.put(MIFARE_PLUS_SL2_2K, new byte[]{(byte) 0x00, (byte) 0x38});
        mapping.put(MIFARE_PLUS_SL2_4K, new byte[]{(byte) 0x00, (byte) 0x39});
        mapping.put(FELICA, new byte[]{(byte) 0x00, (byte) 0x3B});
    }

    ATRHistorical(byte[] bytes) {
        this.bytes = bytes;
        this.atr = new ATR(bytes);
    }

    public String getType() {
        if (Arrays.equals(this.bytes, JAVA_CARD_BYTES)) {
            return JAVA_CARD;
        }
        if (Arrays.equals(this.bytes, JAVA_SECURE_ELEMENT_CARD_BYTES)) {
            return JAVA_CARD;
        }
        byte[] historicalBytes = atr.getHistoricalBytes();
        Log.d("ATRHistorical", Util.toHexString(historicalBytes));
        if (historicalBytes != null && historicalBytes.length > 11) {
            byte[] type = Arrays.copyOfRange(historicalBytes, 9, 11);
            Log.d("ATRHistorical", Util.toHexString(type));
            for (Map.Entry<String, byte[]> entry : mapping.entrySet()) {
                String name = entry.getKey();
                byte[] value = entry.getValue();
                if (Arrays.equals(type, value)) {
                    return name;
                }
            }
        }
        return UNKNOWN;
    }


}
