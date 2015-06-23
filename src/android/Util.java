package com.frankgreen;

import android.util.Log;

import com.frankgreen.apdu.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

    static final String TAG = "CoffeeNFC";

    public static byte[] toByteArray(String hexString) {

        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[len] = (byte) (value << 4);

                } else {

                    byteArray[len] |= value;
                    len++;
                }

                first = !first;
            }
        }

        return byteArray;
    }


    public static String toHexString(int i) {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }


    public static String toHexString(byte[] buffer) {
        if (buffer == null) {
            return "";
        }
        return toHexString(buffer, buffer.length);
    }

    public final static int BYTE_FILL_NONE = -1;

    public static byte[] toNFCByte(String s, int length) {
        return toNFCByte(s, length, 0);
    }

    public static byte[] toNFCByte(String s, int length, int fill) {
        if (s == null) {
            s = "";
        }
        if (fill == BYTE_FILL_NONE) {
            int l = s.length();
            if (length > l) {
                length = l;
            }
        }
        byte[] bytes = new byte[length];
        byte[] ss = s.getBytes();
        for (int i = 0; i < length; i++) {
            if (i < ss.length) {
                bytes[i] = ss[i];
            } else {
                bytes[i] = (byte) fill;
            }
        }
        return bytes;
    }

    public static String toHexString(byte[] buffer, int size) {

        if (buffer == null) {
            return "";
        }
        String bufferString = "";
        for (int i = 0; i < size; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase();
        }

        return bufferString;
    }

    //    public static JSONObject messageToJSON(Message message) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("command", message.getCommand());
//            json.put("data", message.getData());
//            json.put("code", Util.toHexString(message.getCode()));
//            if(message.getException() != null) {
//                json.put("exception", message.getException().getMessage());
//            }
//        } catch (JSONException e) {
//            try {
//                json.put("exception", e.getMessage());
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//        }
//        return json;
//    }
    public static JSONObject resultToJSON(Result result) {
        JSONObject json = new JSONObject();
        try {
//            json.put("data", Util.toHexString(result.getData()));
//            json.put("code", Util.toHexString(result.getCode()));
//            json.put("command", result.getCommand());
            if(result.isSuccess()) {
                if (result.getCommand() != null && result.getCommand() == "ReadBinaryBlock") {
                    int i = 0;
                    for (; i < result.getSize(); i++) {
                        if (result.getData()[i] == (byte) 0x0) {
                            break;
                        }
                    }
                    byte[] data = new byte[i];
                    Log.d(TAG, "I:" + String.valueOf(i));
                    System.arraycopy(result.getData(), 0, data, 0, i);
                    json.put("data", new String(data));
                } else {
                    json.put("data", Util.toHexString(result.getData()));

                }
            }
            json.put("success", result.isSuccess());
//            json.put("size", result.getSize());
            if (result.getException() != null) {
                json.put("exception", result.getException().getMessage());
            }
        } catch (JSONException e) {
            try {
                json.put("exception", e.getMessage());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return json;
    }

}
