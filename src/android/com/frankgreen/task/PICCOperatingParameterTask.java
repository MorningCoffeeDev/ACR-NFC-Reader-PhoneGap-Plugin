package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.apdu.command.AutoStartPolling;
import com.frankgreen.apdu.command.Beep;
import com.frankgreen.apdu.command.BuzzerOff;
import com.frankgreen.apdu.command.PICCOperatingParameter;
import com.frankgreen.params.BaseParams;
import com.frankgreen.params.PICCOperatingParameterParams;

/**
 * Created by kevin on 8/24/15.
 */
public class PICCOperatingParameterTask extends AsyncTask<PICCOperatingParameterParams, Void, Boolean> {

    final private String TAG = "UIDTask";

    @Override
    protected Boolean doInBackground(PICCOperatingParameterParams... paramses) {
        PICCOperatingParameterParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        final PICCOperatingParameter piccOperatingParameter = new PICCOperatingParameter(params);
        final AutoStartPolling autoStartPolling = new AutoStartPolling(params);
        final BuzzerOff buzzerOff = new BuzzerOff(params);
        BaseParams baseParams = new BaseParams(0);
        baseParams.setReader(params.getReader());
        final Beep beep = new Beep(baseParams);
        final TaskListener autoStartPollingListener = new TaskListener() {
            @Override
            public void onSuccess() {
                beep.run();
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onException() {

            }
        };

        final TaskListener buzzerOffListener = new TaskListener() {
            @Override
            public void onSuccess() {
                autoStartPolling.run(autoStartPollingListener);
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onException() {

            }
        };

        final TaskListener piccOperatingParameterListener = new TaskListener() {
            @Override
            public void onSuccess() {
                buzzerOff.run(buzzerOffListener);
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onException() {

            }
        };
        return piccOperatingParameter.run(piccOperatingParameterListener);
    }
}
