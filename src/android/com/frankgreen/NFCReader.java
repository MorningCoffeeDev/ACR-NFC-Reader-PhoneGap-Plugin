package com.frankgreen;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.acs.smartcard.*;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.task.*;

import com.frankgreen.params.*;
import org.apache.cordova.CordovaWebView;

/**
 * Created by kevin on 5/27/15.
 */
public class NFCReader {

    private ACRReader acrReader;
    private ChipMeta chipMeta;
    private CordovaWebView cordovaWebView;
    private static final String TAG = "NFCReader";

    public CordovaWebView getCordovaWebView() {
        return cordovaWebView;
    }

    public NFCReader(ACRReader acrReader) {
        this.acrReader = acrReader;
        this.acrReader.setNfcReader(this);
    }

    public NFCReader(ACRReader acrReader, CordovaWebView cordovaWebView) {
        this.acrReader = acrReader;
        this.acrReader.setNfcReader(this);
        this.cordovaWebView = cordovaWebView;
    }

    public ChipMeta getChipMeta() {
        return chipMeta;
    }

    public void readData(ReadParams readParams) {
        readParams.setReader(this);
        new ReadTask().execute(readParams);
    }

    public void writeData(WriteParams writeParams) {
        writeParams.setReader(this);
        new WriteTask().execute(writeParams);
    }

    public void writeAuthenticate(AuthParams authParams) {
        authParams.setReader(this);
        new WriteAuthenticateTask().execute(authParams);
    }


    public void authenticateWithKeyB(AuthParams authParams) {
        authParams.setReader(this);
        new AuthenticateWithKeyB().execute(authParams);
    }


    public void authenticateWithKeyA(AuthParams authParams) {
        authParams.setReader(this);
        new AuthenticateWithKeyA().execute(authParams);
    }

    public void display(DisplayParams displayParams) {
        displayParams.setReader(this);
        new DisplayTask().execute(displayParams);
    }

    public void clearLCD(ClearLCDParams clearLCDParams) {
        clearLCDParams.setReader(this);
        new ClearLCDTask().execute(clearLCDParams);
    }

    public void getUID(BaseParams uidParams) {
        uidParams.setReader(this);
        new UIDTask().execute(uidParams);
    }

    public void getFirmwareVersion(BaseParams firmwareVersionParams) {
        firmwareVersionParams.setReader(this);
        new GetFirmwareVersionTask().execute(firmwareVersionParams);
    }

    public void getReceivedData(BaseParams receivedDataParams) {
        receivedDataParams.setReader(this);
        new GetReceivedDataTask().execute(receivedDataParams);
    }

    public void getLedStatus(BaseParams ledStatusParams) {
        ledStatusParams.setReader(this);
        new GetLedStatusTask().execute(ledStatusParams);
    }

    public void selectFile(SelectFileParams selectFileParams) {
        selectFileParams.setReader(this);
        new SelectFileTask().execute(selectFileParams);
    }

    public void initNTAGTask(InitNTAGParams initNTAGParams) {
        initNTAGParams.setReader(this);
        new InitNTAGTask().execute(initNTAGParams);
    }

    public void getVersion(BaseParams baseParams) {
        baseParams.setReader(this);
        new GetVersionTask().execute(baseParams);
    }

    public void getBatteryLevel(BaseParams baseParams) {
        baseParams.setReader(this);
        new GetBatteryLevelTask().execute(baseParams);
    }

    public void connect() {
        this.acrReader.connect();
    }

    public void disconnect(BaseParams baseParams) {
        baseParams.setReader(this);
        new DisconnectTask().execute(baseParams);
    }

    public void updatePICCOperatingParameter(OnGetResultListener onGetResultListener) {
        PICCOperatingParameterParams params = new PICCOperatingParameterParams();
        params.setReader(this);
        params.setOnGetResultListener(onGetResultListener);
        new PICCOperatingParameterTask().execute(params);
    }

    public void raiseNotReady(OnGetResultListener onGetResultListener) {
        if (onGetResultListener != null) {
            onGetResultListener.onResult(new Result("Reader", new ReaderException("Reader is not ready.")));
        }
    }

    private long sessionStartedAt = 0;

    public long getSessionStartedAt() {
        return sessionStartedAt;
    }

    public void setSessionStartedAt(long l) {
        sessionStartedAt = l;
    }

    public void clearSessionStartedAt() {
        sessionStartedAt = 0;
    }

    public void setOnStatusChangeListener(ACRReader.StatusChangeListener onStatusChangeListener) {
        this.acrReader.setOnStatusChangeListener(onStatusChangeListener);
    }

    public void setOnStateChangeListener(Reader.OnStateChangeListener onStateChangeListener) {
        this.acrReader.setOnStateChangeListener(onStateChangeListener);
    }

    public void setPermissionIntent(PendingIntent mPermissionIntent) {
        this.acrReader.setPermissionIntent(mPermissionIntent);
    }

    public PendingIntent getPermissionIntent() {
        return this.acrReader.getmPermissionIntent();
    }

    public ACRReader getReader() {
        return this.acrReader;
    }

    public void attach(Intent intent) {
        this.acrReader.attach(intent);
    }

    public void detach(Intent intent) {
        this.acrReader.detach(intent);
    }

//    public void close() {
//        new CloseTask().execute();
//    }

//    public void open(UsbDevice device) {
//        acrReader.open(device);
//    }

    private boolean processing = false;

    public synchronized void reset(int slotNumber) {
        this.chipMeta = new ChipMeta();
        BaseParams baseParams = new BaseParams(slotNumber);
        baseParams.setReader(this);
        baseParams.setOnGetResultListener(acrReader.getOnTouchListener());
        new ResetTask().execute(baseParams);
    }

    public boolean isProcessing() {
        return processing;
    }

    public boolean isReady() {
        Log.d(TAG, "Ready:" + String.valueOf(this.acrReader.isReady()));
        return acrReader.isReady();
    }


    public void listen(OnGetResultListener listener) {
        acrReader.listen(listener);
    }

    private void logBuffer(byte[] atr, int length) {
        Log.d(TAG, Util.toHexString(atr));
    }

    public void start() {
    acrReader.start();
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            acrReader.close();
            return null;
        }
    }

}
