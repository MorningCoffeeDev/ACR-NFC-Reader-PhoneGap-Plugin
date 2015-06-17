package com.frankgreen.task;

import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.apdu.command.Display;
import com.frankgreen.apdu.command.UpdateBinaryBlock;

/**
 * Created by kevin on 6/2/15.
 */
public class DisplayTask extends AsyncTask<DisplayParams, Void, Boolean> {
    final private String TAG = "DisplayTask";

    @Override
    protected Boolean doInBackground(DisplayParams... displayParamses) {
        DisplayParams displayParams = displayParamses[0];
        if (displayParams == null) {
            return false;
        }
        Display display = new Display(displayParams);
        return display.run();
    }
}
