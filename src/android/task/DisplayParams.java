package com.frankgreen.task;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

/**
 * Created by kevin on 6/10/15.
 */
public class DisplayParams  extends Params{
    private int x = 0;
    private int y = 0;
    private boolean bold = false;
    private int font = 1;
    private String message;


    public byte getOption() {
        byte o = 0;
        if (this.isBold()) {
            o |= (byte) 0x01;
        }
        if (this.getFont() == 2) {
            o |= (byte) 0x10;
        }
        if (this.getFont() == 3) {
            o |= (byte) 0x20;
        }
        return o;
    }

    public byte getXY(){
        byte xy = 0;
        if(this.getFont() == 1 || this.getFont() == 2){
            xy |= (byte)((this.getX() % 2) << 6);
        }else{
            xy |= (byte)((this.getX() % 4) << 5);
        }
        xy |= (byte)(this.getY() & 0x0F);
        return xy;
    }

    public DisplayParams(String message) {
        this.message = message;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public int getFont() {
        if (font < 1) return 1;
        if (font > 3) return 1;
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
