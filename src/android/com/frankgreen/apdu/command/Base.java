package com.frankgreen.apdu.command;

import com.acs.smartcard.ReaderException;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.TaskListener;
import com.frankgreen.params.Params;

/**
 * Created by kevin on 5/27/15.
 */
public abstract class Base<T extends Params> implements ToDataString{
    private T params;

    private TaskListener taskListener;

    public Base(T params) {
        this.params = params;
    }
    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }
    public boolean run(){
        return run(null);
    }
    public  boolean run(TaskListener listener){
        this.taskListener = listener;
        return true;
    }

    public void runTaksListener(){
        if (taskListener != null){
            taskListener.onSuccess();
        }
    }
    public String toDataString(Result result){
        return Util.toHexString(result.getData());
    }
}
