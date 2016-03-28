package com.frankgreen.task;

import android.os.AsyncTask;
import com.frankgreen.params.BaseParams;

/**
 * Created by kevin on 16/3/25.
 */
public class DisconnectTask extends AsyncTask<BaseParams, Void, Boolean> {
    @Override
    protected Boolean doInBackground(BaseParams... paramses) {
        BaseParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        params.getReader().getReader().disconnect();
        return true;
    }
}
