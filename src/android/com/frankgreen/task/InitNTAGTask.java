package com.frankgreen.task;

import android.os.AsyncTask;

import android.util.Log;
import com.acs.smartcard.ReaderException;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.card.InitChip;
import com.frankgreen.apdu.command.card.NTagAuth;
import com.frankgreen.apdu.command.card.StartSession;
import com.frankgreen.apdu.command.card.StopSession;
import com.frankgreen.params.InitNTAGParams;

/**
 * Created by kevin on 6/2/15.
 */
public class InitNTAGTask extends AsyncTask<InitNTAGParams, Void, Boolean> {

    final private String TAG = "InitNTAGTask";

    private Result result;

    @Override
    protected Boolean doInBackground(InitNTAGParams... paramses) {
        final InitNTAGParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if (!params.getReader().isReady()) {
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        result = Result.buildSuccessInstance("InitNTAGTask");
        final StartSession startSession = new StartSession(params);
        final NTagAuth nTagAuth = new NTagAuth(params);
        final InitChip initChip = new InitChip(params);
        final StopSession stopSession = new StopSession(params);
        if (!params.getReader().getChipMeta().needAuthentication()) {
            result = new Result("InitNTAGTask", new ReaderException("Invalid Chip"));
        } else {
//            try {
            nTagAuth.initOldPassword();
//                startSession.run();
//                if (nTagAuth.run()) {
//                    initChip.run();
//                }else{
//                    result = new Result("InitNTAGTask", new ReaderException("PWD_WRONG"));
//                }

            final TaskListener initChipListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    stopSession.run();
                }
            };

            final TaskListener nTagAuthListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    initChip.run(initChipListener);
                }

                @Override
                public void onFailure() {
                    result = new Result("InitNTAGTask", new ReaderException("PWD_WRONG"));
                    stopSession.run();
                    if (params.getOnGetResultListener() != null) {
                        params.getOnGetResultListener().onResult(result);
                    }
                }
            };

            final TaskListener startSessionListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    nTagAuth.run(nTagAuthListener);
                }
            };
            startSession.run(startSessionListener);
//
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                result = new Result("InitNTAGTask", new ReaderException("PWD_WRONG"));
//            } finally {
//                stopSession.run();
//            }
        }
        if (params.getOnGetResultListener() != null) {
            params.getOnGetResultListener().onResult(result);
        }
        return true;
    }

}


