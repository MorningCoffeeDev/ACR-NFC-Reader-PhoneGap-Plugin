package com.frankgreen.task;

import android.os.AsyncTask;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.GetVersion;
import com.frankgreen.apdu.command.ReadBinaryBlock;
import com.frankgreen.apdu.command.Reset;
import com.frankgreen.apdu.command.UID;


/**
 * Created by kevin on 6/2/15.
 */
public class ResetTask extends AsyncTask<BaseParams, Void, Boolean> {

    final private String TAG = "ResetTask";


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
        Result result = Result.buildSuccessInstance("Reset");
        ReadParams readParams = new ReadParams(0,0);
        readParams.setOnGetResultListener(params.getOnGetResultListener());
        readParams.setReader(params.getReader());
        ReadBinaryBlock read = new ReadBinaryBlock(readParams);
        Reset reset = new Reset(params);
        UID uid = new UID(params);
        GetVersion getVersion = new GetVersion(params);
        reset.setSendPlugin(false);
        uid.setSendPlugin(false);
        read.setSendPlugin(false);
        getVersion.setSendPlugin(false);

        reset.run();
        uid.run();
        if(params.getReader().getChipMeta().isMifare()) {
            read.run();
            getVersion.run();
        }
        result.setMeta(params.getReader().getChipMeta());
        if (params.getOnGetResultListener() != null) {
            params.getOnGetResultListener().onResult(result);
        }
        return true;
    }

}


