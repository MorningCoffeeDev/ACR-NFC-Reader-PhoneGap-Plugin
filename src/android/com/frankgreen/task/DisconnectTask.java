package com.frankgreen.task;

import android.os.AsyncTask;
import android.util.Log;
import com.frankgreen.operate.DisconnectReader;
import com.frankgreen.params.BaseParams;
import com.frankgreen.params.DisconnectParams;

/**
 * Created by kevin on 16/3/25.
 */
public class DisconnectTask extends AsyncTask<DisconnectParams, Void, Boolean> {
    @Override
    protected Boolean doInBackground(DisconnectParams... paramses) {
        DisconnectParams params = paramses[0];
        if (params == null) {
            return false;
        }
//        if(!params.getReader().isReady()){
//            params.getReader().raiseNotReady(params.getOnGetResultListener());
//            return false;
//        }
        DisconnectReader disconnectReader = new DisconnectReader(params);
        disconnectReader.run();
        return true;
    }
}
