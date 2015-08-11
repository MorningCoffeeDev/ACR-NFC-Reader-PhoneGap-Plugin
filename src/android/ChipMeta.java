package com.frankgreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 8/3/15.
 */
public class ChipMeta {
    private String uid;
    private String type;
    private String name;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean needAuthentication() {
        return name != null && name == "NTAG213";
    }

    public boolean canGetVersion() {
        return type != null && (type == "Mifare Ultralight" || type == "Mifare Ultralight C");
    }

    public void parseATR(byte[] atr) {
        ATRHistorical atrHistorical = new ATRHistorical(atr);
        this.type = atrHistorical.getType();
    }

    public void setUID(byte[] data) {
        this.uid = Util.toHexString(data);
    }


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("type",this.type);
            json.put("name",this.name);
            json.put("uid",this.uid);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
