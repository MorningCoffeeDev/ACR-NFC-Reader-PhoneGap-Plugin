package com.frankgreen;

import android.util.Log;

import com.frankgreen.apdu.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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

    public static byte[] convertHexAsciiToByteArray(byte[] bytes) {
        return convertHexAsciiToByteArray(bytes, 0, bytes.length,0);
    }

    public static byte[] convertHexAsciiToByteArray(String s,int size) {
        byte[] bytes = s.getBytes();
        return convertHexAsciiToByteArray(bytes, 0, bytes.length,size);
    }

    public static byte[] convertHexAsciiToByteArray(byte[] bytes,int size) {
        return convertHexAsciiToByteArray(bytes, 0, bytes.length,size);
    }

    public static byte[] convertHexAsciiToByteArray(byte[] bytes, int offset, int length,int size) {
        if (size == 0){size = length /2;}
        byte[] bin = new byte[size];
        for (int x = 0; x < size; x++) {
            if(offset + (x * 2) + 1 <= bytes.length) {
                bin[x] = (byte) Integer.parseInt(new String(bytes, offset + (x * 2), 2), 16);
            }else{
                bin[x] = (byte)0;
            }
        }
        Log.d("Util.convertHexAsciiToByteArray",toHexString(bin));
        return bin;
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

    public static String dataToString(byte[] s){
        int i = 0;
        for (; i < s.length; i++) {
            if (s[i] == (byte) 0x0) {
                break;
            }
        }
        byte[] data = new byte[i];
        Log.d(TAG, "I:" + String.valueOf(i));
        System.arraycopy(s, 0, data, 0, i);
        return new String(data);
    }

    public static void sleep(int n){
        try {
            Log.d("Util.sleep",String.valueOf(n));
            Thread.sleep(n); //do remove this line.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject resultToJSON(Result result) {
        JSONObject json = new JSONObject();
        try {
            if (result.isSuccess()) {
                if (result.getCommand() == "GetVersion") {
                    json.put("response", toHexString(result.getData()));
                }

                if (result.getCommand() == "Reset") {
                    json.put("metadata", result.getMeta().toJSON());
                }
                json.put("data", result.getDataString());
            }
            json.put("success", result.isSuccess());
            if (result.getException() != null) {
                json.put("exception", result.getException().getMessage());
            }
            if (result.getCommand() == "Disconnect") {
                json.put("data", result.getResultMessage());
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

    public static JSONObject customJSON(boolean isSuccess, String resultMessage) {
        JSONObject json = new JSONObject();
        try {
            if (isSuccess) {
                json.put("success", "true");
                json.put("data", resultMessage);
            } else {
                json.put("success", "false");
                json.put("exception", resultMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
