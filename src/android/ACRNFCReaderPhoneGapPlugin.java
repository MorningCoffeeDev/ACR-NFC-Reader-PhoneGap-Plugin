package com.frankgreen;

/**
 * Created by kevin on 5/20/15.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
//import com.frankgreen.apdu.OnGetResultListener;
//import com.frankgreen.apdu.Result;
//import com.frankgreen.apdu.command.UID;
//import com.frankgreen.task.ReadTask;
//import com.frankgreen.task.UIDTask;
//import com.frankgreen.task.WriteTask;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.task.AuthParams;
import com.frankgreen.task.BaseParams;
import com.frankgreen.task.ClearLCDParams;
import com.frankgreen.task.DisplayParams;
import com.frankgreen.task.InitNTAGParams;
import com.frankgreen.task.ReadParams;
import com.frankgreen.task.SelectFileParams;
import com.frankgreen.task.WriteParams;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACRNFCReaderPhoneGapPlugin extends CordovaPlugin {

    private static final String TAG = "ACR";
    private static final String LISTEN = "listen";
    private static final String READ_UID = "readUID";
    private static final String READ_DATA = "readData";
    private static final String WRITE_DATA = "writeData";
    private static final String GET_VERSION = "getVersion";
    private static final String AUTHENTICATE_WITH_KEY_A = "authenticateWithKeyA";
    private static final String AUTHENTICATE_WITH_KEY_B = "authenticateWithKeyB";
    private static final String WRITE_AUTHENTICATE = "writeAuthenticate";
    private static final String SELECT_FILE = "selectFile";
    private static final String DISPLAY = "display";
    private static final String CLEAR_LCD = "clearLCD";
    private static final String INIT_NTAG213 = "initNTAG213";


    private CordovaWebView webView;

    private NFCReader nfcReader;


    private UsbManager usbManager;
//    private UsbDevice usbDevice;


    //    private Reader reader;
    PendingIntent mPermissionIntent;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    nfcReader.attach(intent);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                synchronized (this) {
                    nfcReader.detach(intent);
                }
            }
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        this.webView = webView;
        super.initialize(cordova, webView);

        Log.d(TAG, "initializing...");
        usbManager = (UsbManager) cordova.getActivity().getSystemService(Context.USB_SERVICE);
        nfcReader = new NFCReader(usbManager);
        nfcReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNumber, int previousState, int currentState) {
//                if (!nfcReader.isProcessing()) {
                Log.d(TAG, "slotNumber " + slotNumber);
                Log.d(TAG, "previousState " + previousState);
                Log.d(TAG, "currentState " + currentState);
                Log.d(TAG, "");
                if (slotNumber == 0 && currentState == Reader.CARD_PRESENT) {
                    Log.d(TAG, "Ready to read!!!!");
                    nfcReader.reset(slotNumber);
                } else {// if (currentState == Reader.CARD_ABSENT && previousState == Reader.CARD_PRESENT) {
                    Log.d(TAG, "Card Lost");
                    webView.sendJavascript("ACR.runCardAbsent();");
                }
//                }
            }
        });

        nfcReader.setOnStatusChangeListener(new NFCReader.StatusChangeListener() {

                                                @Override
                                                public void onReady(Reader reader) {
                                                    Log.d(TAG, "onReady");
                                                    webView.sendJavascript("ACR.onReady('" + reader.getReaderName() + "');");
                                                }

                                                @Override
                                                public void onAttach(UsbDevice device) {
                                                    Log.d(TAG, "onAttach");
                                                    webView.sendJavascript("ACR.onAttach('" + device.getDeviceName() + "');");
                                                }

                                                @Override
                                                public void onDetach(UsbDevice device) {
                                                    Log.d(TAG, "onDetach");
                                                    webView.sendJavascript("ACR.onDetach('" + device.getDeviceName() + "');");
                                                }
                                            }
        );
        // Register receiver for USB permission

        mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        nfcReader.setPermissionIntent(mPermissionIntent);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        Log.d(TAG, "execute " + action);

        // TODO call error callback if there is no reader
        if (action.equalsIgnoreCase(LISTEN)) {
            listen(callbackContext);
        } else if (action.equalsIgnoreCase(READ_UID)) {
            readUID(callbackContext);
        } else if (action.equalsIgnoreCase(READ_DATA)) {
            readData(callbackContext, data);
        } else if (action.equalsIgnoreCase(WRITE_DATA)) {
            writeData(callbackContext, data);
        } else if (action.equalsIgnoreCase(AUTHENTICATE_WITH_KEY_A)) {
            authenticateWithKeyA(callbackContext, data);
        } else if (action.equalsIgnoreCase(AUTHENTICATE_WITH_KEY_B)) {
            authenticateWithKeyB(callbackContext, data);
        } else if (action.equalsIgnoreCase(WRITE_AUTHENTICATE)) {
            writeAuthenticate(callbackContext, data);
        } else if (action.equalsIgnoreCase(SELECT_FILE)) {
            selectFile(callbackContext, data);
        } else if (action.equalsIgnoreCase(DISPLAY)) {
            display(callbackContext, data);
        } else if (action.equalsIgnoreCase(CLEAR_LCD)) {
            clearLCD(callbackContext, data);
        } else if (action.equalsIgnoreCase(GET_VERSION)) {
            getVersion(callbackContext, data);
        } else if (action.equalsIgnoreCase(INIT_NTAG213)) {
            initNTAG213(callbackContext, data);
        } else {
            return false;
        }
        return true;
    }

    private void initNTAG213(CallbackContext callbackContext, JSONArray data) {
        InitNTAGParams initNTAGParams = new InitNTAGParams(0);
        try {
            initNTAGParams.setPassword(data.getString(0));
            initNTAGParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.initNTAGTask(initNTAGParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getVersion(final CallbackContext callbackContext, JSONArray data) {
        BaseParams baseParams = new BaseParams(0);
        baseParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.getVersion(baseParams);
    }

    private void selectFile(final CallbackContext callbackContext, JSONArray data) {
        try {
            SelectFileParams selectFileParams = new SelectFileParams(0, data.getString(0));
            selectFileParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.selectFile(selectFileParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readUID(final CallbackContext callbackContext) {
        BaseParams uidParams = new BaseParams(0);
        uidParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.getUID(uidParams);
    }

    private void writeAuthenticate(final CallbackContext callbackContext, JSONArray data) {
        try {
            AuthParams authParams = new AuthParams(0, data.getInt(0));
            authParams.setKeyA(data.getString(1));
            authParams.setKeyB(data.getString(2));
            authParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.writeAuthenticate(authParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authenticateWithKeyB(final CallbackContext callbackContext, JSONArray data) {
        try {
            AuthParams authParams = new AuthParams(0, data.getInt(0));
            authParams.setKeyB(data.getString(1));
            authParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.authenticateWithKeyB(authParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authenticateWithKeyA(final CallbackContext callbackContext, JSONArray data) {
        try {
            AuthParams authParams = new AuthParams(0, data.getInt(0));
            authParams.setKeyA(data.getString(1));
            authParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.authenticateWithKeyA(authParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readData(final CallbackContext callbackContext, JSONArray data) {

        try {
            ReadParams readParams = new ReadParams(0, data.getInt(0));
            readParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.readData(readParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeData(final CallbackContext callbackContext, JSONArray data) {
        try {
            WriteParams writeParams = new WriteParams(0, data.getInt(0), data.getString(1));
            writeParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.writeData(writeParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearLCD(final CallbackContext callbackContext, JSONArray data) {
        ClearLCDParams clearLCDParams = new ClearLCDParams();
        clearLCDParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.clearLCD(clearLCDParams);
    }

    private void display(final CallbackContext callbackContext, JSONArray data) {
        try {
//            [msg, options.x, options.y, options.bold, options.font]
            DisplayParams displayParams = new DisplayParams(data.getString(0));
            displayParams.setX(data.getInt(1));
            displayParams.setY(data.getInt(2));
            displayParams.setBold(data.getBoolean(3));
            displayParams.setFont(data.getInt(4));
            displayParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.display(displayParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listen(final CallbackContext callbackContext) {
        Log.w(TAG, "ACR listen");
        nfcReader.listen(generateResultListener(callbackContext));
    }

    private OnGetResultListener generateResultListener(final CallbackContext callbackContext) {
        return new OnGetResultListener() {
            @Override
            public void onResult(Result result) {
                Log.w(TAG, "==========" + result.getCommand() + "==========");
                Log.w(TAG, result.getCodeString());
                if (result.getData() != null) {
                    for (byte b : result.getData()) {
                        Log.w(TAG, "byte " + b);
                    }
                }
                Log.w(TAG, "====================");
                if(result.isSendPlugin()) {
                    PluginResult pluginResult = new PluginResult(
                            result.isSuccess() ? PluginResult.Status.OK : PluginResult.Status.ERROR,
                            Util.resultToJSON(result));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            }
        };
    }

    private Activity getActivity() {
        return this.cordova.getActivity();
    }

    private Intent getIntent() {
        return getActivity().getIntent();
    }
}