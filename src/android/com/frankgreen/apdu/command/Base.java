package com.frankgreen.apdu.command;

import android.util.Log;
import com.frankgreen.Util;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.card.StopSession;
import com.frankgreen.task.TaskListener;
import com.frankgreen.params.Params;

/**
 * Created by kevin on 5/27/15.
 */
public abstract class Base<T extends Params> implements ToDataString{
    private T params;

    private TaskListener taskListener;
    private StopSession stopSession;

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
    public boolean run(TaskListener listener){
        this.taskListener = listener;
        return true;
    }

    public void runTaskListener(boolean isSuccess){
        if (taskListener != null){
                Log.d("ACR", "isSuccess in runTaskListener :" + String.valueOf(isSuccess));
             if(isSuccess) {
                 taskListener.onSuccess();
             } else {
                 taskListener.onFailure();
             }
        }
    }
    public String toDataString(Result result){
        return Util.toHexString(result.getData());
    }

    public StopSession getStopSession() {
        return stopSession;
    }

    public void setStopSession(StopSession stopSession) {
        this.stopSession = stopSession;
    }
}
