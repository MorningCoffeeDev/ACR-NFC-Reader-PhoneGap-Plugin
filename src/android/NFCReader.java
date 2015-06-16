package com.frankgreen;

import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.acs.smartcard.*;
import com.frankgreen.apdu.OnGetResultListener;
//import com.frankgreen.apdu.OnListenListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.apdu.command.*;
import com.frankgreen.task.*;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 5/27/15.
 */
public class NFCReader {
    private static String TAG = "NFCReader";

    private PendingIntent mPermissionIntent;
    private UsbManager mManager;
    private Reader mReader;

    private List<String> mReaderList;
    private List<String> mSlotList;
    private boolean ready = false;

    public NFCReader(UsbManager mManager) {
        this.mManager = mManager;
        this.mReader = new Reader(mManager);
    }

    public void readData(Integer slot, int block, OnGetResultListener listener) {
        ReadParams readParams = new ReadParams(this, slot, block);
        readParams.setOnGetResultListener(listener);
        new ReadTask().execute(readParams);
    }

    public void writeData(Integer slot, int block, String data, OnGetResultListener listener) {
        WriteParams writeParams = new WriteParams(this, slot, block, data);
        writeParams.setOnGetResultListener(listener);
        new WriteTask().execute(writeParams);
    }


    private OnGetResultListener onGetUIDResultlistener;


    public void writeAuthenticate(int slot, int block, String keyA, String keyB, OnGetResultListener listener) {
        AuthParams authParams = new AuthParams(this, slot, block);
        authParams.setKeyA(keyA);
        authParams.setKeyB(keyB);
        authParams.setOnGetResultListener(listener);
        new WriteAuthenticate().execute(authParams);
    }


    public void authenticateWithKeyB(int slot, int block, String keyB, OnGetResultListener listener) {
        AuthParams authParams = new AuthParams(this, slot, block);
        authParams.setKeyB(keyB);
        authParams.setOnGetResultListener(listener);
        new AuthenticateWithKeyB().execute(authParams);
    }


    public void authenticateWithKeyA(int slot, int block, String keyA,OnGetResultListener listener) {
        AuthParams authParams = new AuthParams(this, slot, block);
        authParams.setKeyA(keyA);
        authParams.setOnGetResultListener(listener);
        new AuthenticateWithKeyA().execute(authParams);
    }

    public interface StatusChangeListener {
        void onReady(Reader reader);

        void onAttach(UsbDevice device);

        void onDetach(UsbDevice device);
    }

    private StatusChangeListener onStatusChangeListener;

    public void setOnStatusChangeListener(StatusChangeListener onStatusChangeListener) {
        this.onStatusChangeListener = onStatusChangeListener;
    }

    public void setOnStateChangeListener(Reader.OnStateChangeListener onStateChangeListener) {
        mReader.setOnStateChangeListener(onStateChangeListener);
    }

    public void setPermissionIntent(PendingIntent mPermissionIntent) {
        this.mPermissionIntent = mPermissionIntent;
    }

    public PendingIntent getPermissionIntent() {
        return mPermissionIntent;
    }

    public UsbManager getUsbManager() {
        return mManager;
    }

    public Reader getReader() {
        return mReader;
    }

    public void attach(Intent intent) {

        UsbDevice device = (UsbDevice) intent
                .getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (intent.getBooleanExtra(
                UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

            if (device != null) {
                Log.d(TAG, "Opening reader: " + device.getDeviceName()
                        + "...");
                if (onStatusChangeListener != null) {
                    onStatusChangeListener.onAttach(device);
                }
                open(device);
            }

        } else {
            Log.w(TAG, "Permission denied for device "
                    + device.getDeviceName());

        }
    }

    public boolean isSupported(UsbDevice device) {
        return mReader.isSupported(device);
    }

    public void detach(Intent intent) {
        if (mReaderList == null) {
            mReaderList = new ArrayList<String>();
        }
        mReaderList.clear();
        for (UsbDevice device : mManager.getDeviceList().values()) {
            if (mReader.isSupported(device)) {
                mReaderList.add(device.getDeviceName());
            }
        }

        UsbDevice device = (UsbDevice) intent
                .getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (device != null && device.equals(mReader.getDevice())) {
            if (mSlotList != null) {
                mSlotList.clear();
            }
        }
        Log.d(TAG, "Closing reader...");
        if (onStatusChangeListener != null) {
            onStatusChangeListener.onDetach(device);
        }
        ready = false;
        close();

    }

    public void close() {
        new CloseTask().execute();
    }

    public void open(UsbDevice device) {
        new OpenTask().execute(device);
    }

    private boolean processing = false;

    public synchronized void reset(int slotNumber) {
//        processing = true;
        UIDParams uidParams = new UIDParams(this, slotNumber);
        uidParams.setOnGetResultListener(this.onGetUIDResultlistener);
        new UIDTask().execute(uidParams);
//        ResetParams pp = new ResetParams();
//        pp.slotNum = slotNumber;
//        pp.powerAction = Reader.CARD_WARM_RESET;
//        pp.protocols = Reader.PROTOCOL_T0 | Reader.PROTOCOL_T1;
//        new ResetTask().execute(new ResetParams());
    }

    public boolean isProcessing() {
        return processing;
    }

    public boolean isReady() {
        return ready;
    }


    public void listen(OnGetResultListener listener) {
        onGetUIDResultlistener = listener;
        if (mReaderList == null) {
            mReaderList = new ArrayList<String>();
        }
        mReaderList.clear();
        for (UsbDevice device : mManager.getDeviceList().values()) {
            if (mReader.isSupported(device)) {
                mReaderList.add(device.getDeviceName());
                mManager.requestPermission(device, mPermissionIntent);
            }
        }
    }

    private void logBuffer(byte[] atr, int length) {
        Log.d(TAG, Util.toHexString(atr));
    }


    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {
            Exception result = null;
            try {
                mReader.open(params[0]);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {

            if (result != null) {
                Log.d(TAG, result.toString());
            } else {
                Log.d(TAG, "Reader name: " + mReader.getReaderName());

                int numSlots = mReader.getNumSlots();
                Log.d(TAG, "Number of slots: " + numSlots);
                ready = true;
                if (onStatusChangeListener != null) {
                    onStatusChangeListener.onReady(mReader);
                }
                // Add slot items
                if (mSlotList == null) {
                    mSlotList = new ArrayList<String>();
                }
                mSlotList.clear();
                for (int i = 0; i < numSlots; i++) {
                    mSlotList.add(Integer.toString(i));
                }
            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mReader.close();
            return null;
        }
    }
}
