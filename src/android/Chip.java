package com.frankgreen;

import java.util.Arrays;

/**
 * Created by kevin on 8/3/15.
 */
public class Chip {
    private byte[] version;
    private String name;

    public Chip(String name, byte[] version) {
        this.name = name;
        this.version = version;
    }

    public Chip(String name, int[] version) {
        byte[] v = new byte[version.length];
        for (int i = 0; i < version.length; i++) {
            v[i] = (byte) (version[i] & 0xff);
        }
        this.name = name;
        this.version = v;
    }

    static final Chip NTAG213 = new Chip("NTAG213", new int[]{
            0, 4, 4, 2, 1, 0, 0x0f, 3
    });
    static final Chip NTAG215 = new Chip("NTAG215", new int[]{
            0, 4, 4, 2, 1, 0, 0x11, 3
    });
    static final Chip NTAG216 = new Chip("NTAG216", new int[]{
            0, 4, 4, 1, 1, 0, 0x13, 3
    });

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static Chip[] chips = new Chip[]{NTAG213, NTAG215, NTAG216};

    public static Chip find(byte[] version) {
        for (Chip chip : chips) {
            if (chip.equals(version)) {
                return chip;
            }
        }
        return null;
    }

    public boolean equals(byte[] version) {
        return Arrays.equals(this.version, version);
    }

    public boolean equals(Chip chip) {
        return Arrays.equals(this.version, chip.version);
    }


    public String getType() {
        return ATRHistorical.MIFARE_ULTRALIGHT_C; // all chip are ULC
    }
}
