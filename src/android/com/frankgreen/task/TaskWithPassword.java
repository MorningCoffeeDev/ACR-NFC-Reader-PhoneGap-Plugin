package com.frankgreen.task;

import android.util.Log;
import com.acs.smartcard.ReaderException;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.Beep;
import com.frankgreen.apdu.command.card.NTagAuth;
import com.frankgreen.apdu.command.card.StartSession;
import com.frankgreen.apdu.command.card.StopSession;
import com.frankgreen.params.BaseParams;
import com.frankgreen.params.InitNTAGParams;

/**
 * Created by kevin on 8/12/15.
 */
public class TaskWithPassword {
    private String name;
    private String password;
    private int slotNumber = 0;
    private NFCReader reader;
    private OnGetResultListener getResultListener;
    private TaskCallback callback;

    public TaskWithPassword(String name, NFCReader reader, int slotNumber, String password) {
        this.name = name;
        this.reader = reader;
        this.slotNumber = slotNumber;
        this.password = password;
    }

    interface TaskCallback {
        boolean run(TaskListener taskListener, StopSession stopSession);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public NFCReader getReader() {
        return reader;
    }

    public void setReader(NFCReader reader) {
        this.reader = reader;
    }

    public OnGetResultListener getGetResultListener() {
        return getResultListener;
    }

    public void setGetResultListener(OnGetResultListener getResultListener) {
        this.getResultListener = getResultListener;
    }

    private Result result = null;

    private boolean callBackSuccess;

    public TaskCallback getCallback() {
        return callback;
    }

    public void setCallback(TaskCallback callback) {
        this.callback = callback;
    }

    public boolean run() {

        if (this.callback != null && reader.getChipMeta().needAuthentication()) {
            InitNTAGParams initNTAGParams = new InitNTAGParams(slotNumber);
            initNTAGParams.setReader(reader);
            initNTAGParams.setPassword(password);
            initNTAGParams.setOnGetResultListener(getResultListener);
            final StartSession startSession = new StartSession(initNTAGParams);
            final NTagAuth nTagAuth = new NTagAuth(initNTAGParams);
            final StopSession stopSession = new StopSession(initNTAGParams);
            BaseParams params = new BaseParams(slotNumber);
            params.setReader(reader);
            final Beep beep = new Beep(params);

            final TaskListener beepListener = new TaskListener() {
                @Override
                public void onSuccess() {
                    beep.run();
                }

                @Override
                public void onFailure() {
                    beep.run();
                }

                @Override
                public void onException() {
                }
            };

            final TaskListener callbackListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    stopSession.run(beepListener);
                }
            };

            final TaskListener nTagAuthListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    Log.d("ACR", "nTagAuthListener success");
                    callBackSuccess = callback.run(callbackListener, getStopSession());
                }

                @Override
                public void onFailure() {
                    result = new Result(getName(), new ReaderException("PWD_WRONG"));
                    stopSession.setSendResult(result);
                    stopSession.run();
                }
            };

            final TaskListener startSessionListener = new AbstractTaskListener(stopSession) {
                @Override
                public void onSuccess() {
                    nTagAuth.initPassword();
                    nTagAuth.run(nTagAuthListener);
                }
            };
            startSession.run(startSessionListener);
//            try {
//                startSession.run();
//                nTagAuth.initPassword();
//                if (nTagAuth.run()) {
//                    return callback.run();
//                } else {
//                    result = new Result(getName(), new ReaderException("PWD_WRONG"));
//                }
//            } catch (NumberFormatException e){
//                e.printStackTrace();
//                result = new Result(getName(), new ReaderException("PWD_WRONG"));
//            } finally {
//                stopSession.run();
//            }
            return callBackSuccess;
        } else {
            result = new Result(getName(), new ReaderException("PWD_WRONG"));
        }
        if (result != null && getResultListener != null) {
            getResultListener.onResult(result);
        }
        return false;
    }
}
