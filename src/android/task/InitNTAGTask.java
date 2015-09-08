package com.frankgreen.task;

import android.os.AsyncTask;

import com.acs.smartcard.ReaderException;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.UID;
import com.frankgreen.apdu.command.ntag.InitChip;
import com.frankgreen.apdu.command.ntag.NTagAuth;
import com.frankgreen.apdu.command.ntag.StartSession;
import com.frankgreen.apdu.command.ntag.StopSession;

/**
 * Created by kevin on 6/2/15.
 */
public class InitNTAGTask extends AsyncTask<InitNTAGParams, Void, Boolean> {

    final private String TAG = "InitNTAGTask";


    @Override
    protected Boolean doInBackground(InitNTAGParams... paramses) {
        InitNTAGParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if (!params.getReader().isReady()) {
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        Result result = Result.buildSuccessInstance("InitNTAGTask");
        StartSession startSession = new StartSession(params);
        NTagAuth nTagAuth = new NTagAuth(params);
        InitChip initChip = new InitChip(params);
        StopSession stopSession = new StopSession(params);
        if (!params.getReader().getChipMeta().needAuthentication()) {
            result = new Result("InitNTAGTask", new ReaderException("Invalid Chip"));
        } else {
            try {
                nTagAuth.initOldPassword();
                startSession.run();
                if (nTagAuth.run()) {
                    initChip.run();
                }else{
                    result = new Result("InitNTAGTask", new ReaderException("Invalid Password"));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                result = new Result("InitNTAGTask", new ReaderException("Invalid Password"));
            } finally {
                stopSession.run();
            }
        }
        if (params.getOnGetResultListener() != null) {
            params.getOnGetResultListener().onResult(result);
        }
        return true;
    }

}


