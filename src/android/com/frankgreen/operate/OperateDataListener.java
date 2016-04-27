package com.frankgreen.operate;

/**
 * Created by cain on 16/4/26.
 */
public interface OperateDataListener {
    boolean onData(OperateResult operateResult);
    boolean onError(OperateResult operateResult);
}
