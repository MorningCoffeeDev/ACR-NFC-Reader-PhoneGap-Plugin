package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.GetVersion;
import com.frankgreen.apdu.command.ReadBinaryBlock;
import com.frankgreen.apdu.command.Reset;
import com.frankgreen.apdu.command.UID;
import com.frankgreen.params.BaseParams;
import com.frankgreen.params.ReadParams;


/**
 * Created by kevin on 6/2/15.
 */
public class ResetTask extends AsyncTask<BaseParams, Void, Boolean> {

    final private String TAG = "ResetTask";

    final private int ULTRALIGHT_MAX_PAGE = 0x0f;

    @Override
    protected Boolean doInBackground(BaseParams... paramses) {
        final BaseParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if (!params.getReader().isReady()) {
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        ReadParams readParams = new ReadParams(0, 0);
        readParams.setOnGetResultListener(params.getOnGetResultListener());
        readParams.setReader(params.getReader());
        final ReadBinaryBlock read = new ReadBinaryBlock(readParams);
        final Reset reset = new Reset(params);
        final UID uid = new UID(params);
        final GetVersion getVersion = new GetVersion(params);
        reset.setSendPlugin(false);
        uid.setSendPlugin(false);
        read.setSendPlugin(false);
        getVersion.setSendPlugin(false);

        final NFCReader reader = params.getReader();
        final TaskListener getVersionListener = new TaskListener() {
            @Override
            public void onSuccess() {
                Result result = Result.buildSuccessInstance("Reset");
                result.setMeta(params.getReader().getChipMeta());
                if (params.getOnGetResultListener() != null) {
                    params.getOnGetResultListener().onResult(result);
                }
            }

            @Override
            public void onFailure() {
            }

            @Override
            public void onException() {

            }
        };

        final TaskListener readListener = new TaskListener() {

            @Override
            public void onSuccess() {
                if (reader.getChipMeta().isMifare_C()) {
                    getVersion.run(getVersionListener);
                } else {
                    Result result = Result.buildSuccessInstance("Reset");
                    result.setMeta(params.getReader().getChipMeta());
                    if (params.getOnGetResultListener() != null) {
                        params.getOnGetResultListener().onResult(result);
                    }
                }
//                getVersion.run();
//                Result result = Result.buildSuccessInstance("Reset");
//                result.setMeta(params.getReader().getChipMeta());
//                if (params.getOnGetResultListener() != null) {
//                    params.getOnGetResultListener().onResult(result);
//                }
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onException() {

            }
        };

        final TaskListener uidListener = new TaskListener() {
            @Override
            public void onSuccess() {
                if (reader.getChipMeta().isMifare()) {
                    read.run(readListener);
                }else{
                    Result result = Result.buildSuccessInstance("Reset");
                    result.setMeta(params.getReader().getChipMeta());
                    if (params.getOnGetResultListener() != null) {
                        params.getOnGetResultListener().onResult(result);
                    }
                }
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onException() {

            }
        };

        final TaskListener resetListener = new TaskListener() {
            @Override
            public void onSuccess() {
                uid.run(uidListener);
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onException() {

            }
        };
        reset.run(resetListener);
        return true;
    }

}


