package com.frankgreen.apdu;

import com.frankgreen.ChipMeta;
import com.frankgreen.Util;
import com.frankgreen.apdu.command.Base;
import com.frankgreen.apdu.command.ToDataString;

import java.util.Arrays;

/**
 * Created by kevin on 5/27/15.
 */
public class Result {
    //    private String command;
    private int size = 0;
    private String command;
    private byte[] data;
    private byte[] code;
    private ChipMeta meta;
    private Exception exception;
    private Checker checker;
    private String resultMessage;
    private boolean sendPlugin = true;
    private ToDataString processor;
    public interface Checker{
        boolean check(Result result);
    }
    public String getCommand() {
        return command;
    }

    public void setProcessor(ToDataString processor) {
        this.processor = processor;
    }

    public String getDataString(){
        if (this.processor != null) {
            return this.processor.toDataString(this);
        } else {
            return Util.toHexString(this.getData());
        }
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static Result buildSuccessInstance(String command) {
        return new Result(command, 2, new byte[]{(byte) 0x90, (byte) 0x00});
    }

    public Result(String command, int byteCount, byte[] receiveBuffer) {
        setReceiveBuffer(byteCount,receiveBuffer);
        this.command = command;
    }

    public Result(String command, String message) {
        this.resultMessage = message;
        this.command = command;
        this.size = 0;
        this.code = new byte[]{(byte) 0x90, (byte) 0x00};
    }

    public Result(String command, Exception exception) {
        this.exception = exception;
        this.command = command;
        this.size = 0;
        this.code = new byte[]{(byte)0,(byte)0};
    }

    public Result() {
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        setData(data,data.length);
    }

    public void setData(byte[] data, int length) {
        this.data = data;
        this.size = length;
    }

    public byte[] getCode() {
        return code;
    }

    public String getCodeString() {
        return Util.toHexString(this.code);
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public boolean isSuccess() {
        boolean flag = this.code[0] == (byte) 0x90 && this.code[1] == (byte) 0x00;
        if(flag && this.checker != null){
            return this.checker.check(this);
        }
        return flag;
    }

    public ChipMeta getMeta() {
        return meta;
    }

    public void setMeta(ChipMeta meta) {
        this.meta = meta;
    }

    public boolean isSendPlugin() {
        return sendPlugin;
    }

    public void setSendPlugin(boolean sendPlugin) {
        this.sendPlugin = sendPlugin;
    }

    public Checker getChecker() {
        return checker;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public void setReceiveBuffer(int byteCount,byte[] receiveBuffer) {
        if (byteCount >= 2) {
            this.size = byteCount - 2;
            this.code = new byte[2];
            this.code[0] = receiveBuffer[size];
            this.code[1] = receiveBuffer[size + 1];
            this.data = Arrays.copyOf(receiveBuffer, size);
        } else {
            this.size = 0;
            this.code = new byte[]{(byte)0,(byte)0};
            if (receiveBuffer != null && receiveBuffer.length > 2) {
                this.code[0] = receiveBuffer[size];
                this.code[1] = receiveBuffer[size + 1];
            }
            this.data = null;
        }
    }
}
