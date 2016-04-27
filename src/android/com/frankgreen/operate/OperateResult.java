package com.frankgreen.operate;

/**
 * Created by cain on 16/4/26.
 */
public class OperateResult {

    private String ResultMessage;

    private CustomDevice customDevice;

    public OperateResult(CustomDevice customDevice) {
        this.customDevice = customDevice;
    }

    public OperateResult(String resultMessage) {
        ResultMessage = resultMessage;
    }

    public String getResultMessage() {
        return ResultMessage;
    }

    public CustomDevice getCustomDevice() {
        return customDevice;
    }
}
