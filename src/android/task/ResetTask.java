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
    protected Boolean doInBackground(BaseParams... baseParamses) {
        BaseParams baseParams = baseParamses[0];
        if (baseParams == null) {
            return false;
        }
        Result result = Result.buildSuccessInstance("Reset");
        ReadParams readParams = new ReadParams(0,0);
        readParams.setOnGetResultListener(baseParams.getOnGetResultListener());
        readParams.setReader(baseParams.getReader());
        ReadBinaryBlock read = new ReadBinaryBlock(readParams);
        Reset reset = new Reset(baseParams);
        UID uid = new UID(baseParams);
        GetVersion getVersion = new GetVersion(baseParams);
        reset.setSendPlugin(false);
        uid.setSendPlugin(false);
        read.setSendPlugin(false);
        getVersion.setSendPlugin(false);

        reset.run();
        uid.run();
        if(baseParams.getReader().getChipMeta().isMifare()) {
            read.run();
            getVersion.run();
        }
        result.setMeta(baseParams.getReader().getChipMeta());
        if (baseParams.getOnGetResultListener() != null) {
            baseParams.getOnGetResultListener().onResult(result);
        }
        return true;
    }

}


