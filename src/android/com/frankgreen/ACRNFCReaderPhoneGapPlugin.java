package com.frankgreen;

/**
 * Created by kevin on 5/20/15.
 */

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.acs.bluetooth.*;
import com.acs.smartcard.Reader;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;
import com.frankgreen.params.AuthParams;
import com.frankgreen.params.BaseParams;
import com.frankgreen.params.ClearLCDParams;
import com.frankgreen.params.DisplayParams;
import com.frankgreen.params.InitNTAGParams;
import com.frankgreen.params.ReadParams;
import com.frankgreen.params.SelectFileParams;
import com.frankgreen.reader.ACRReader;
import com.frankgreen.reader.BTReader;
import com.frankgreen.reader.USBReader;
import com.frankgreen.task.StopSessionTimerTask;
import com.frankgreen.params.WriteParams;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;

/**
 * This class echoes a string called from JavaScript.
 */
public class ACRNFCReaderPhoneGapPlugin extends CordovaPlugin {

    private Activity pluginActivity;

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
    private static final String IS_READY = "isReady";
    private static final String DISPLAY = "display";
    private static final String CLEAR_LCD = "clearLCD";
    private static final String INIT_NTAG213 = "initNTAG213";
    private static final String INIT_READER = "initReader";
    private static final String GET_FIRMWARE_VERSION = "getFirmwareVersion";
    private static final String GET_RECEIVED_DATA = "getReceivedData";
    private static final String GET_LED_STATUS = "getLedStatus";
    private static final int REQUEST_ENABLE_BT = 1;

    private static boolean isSupportedBlueTooth = true;

    private CordovaWebView webView;

    private NFCReader nfcReader;


    private UsbManager usbManager;

    private BluetoothReaderManager bluetoothReaderManager;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothReader mbluetoothReader;
    private BluetoothReaderGattCallback mGattCallback;

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

//    private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "-----------Attach listen----------------");
//            String action = intent.getAction();
//            Log.d(TAG, "bluetoothBroadcastReceiver:" + action);
//            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                nfcReader.attach(intent);
//            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                nfcReader.detach(intent);
//            }
//        }
//    };


    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        this.webView = webView;
        super.initialize(cordova, webView);
        this.pluginActivity = cordova.getActivity();

        Log.d(TAG, "initializing...");
        if (pluginActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            mBluetoothManager = (BluetoothManager) pluginActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                cordova.getActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            useBluetoothReader(cordova, webView);

        } else {
            isSupportedBlueTooth = false;
            useUsbReader(cordova, webView);
        }

    }

    private Timer timer;

    private void setupTimer() {
        timer = new Timer();
        StopSessionTimerTask task = new StopSessionTimerTask(nfcReader);
        timer.schedule(task, 10000, 5000);
    }


    private void useBluetoothReader(CordovaInterface cordova, final CordovaWebView webView) {
        ACRReader reader = new BTReader(mBluetoothManager, getActivity());

        nfcReader = new NFCReader(reader, webView);

        nfcReader.setOnStatusChangeListener(new ACRReader.StatusChangeListener() {

                                                @Override
                                                public void onReady(ACRReader reader) {
                                                    Log.d(TAG, "onReady");
                                                    initReader(null, null);
                                                    webView.sendJavascript("ACR.onReady('" + reader.getReaderName() + "');");
                                                }

                                                @Override
                                                public void onAttach(ACRDevice device) {
                                                    Log.d(TAG, "onAttach");
                                                    webView.sendJavascript("ACR.onAttach('" + ((BluetoothDevice) device.getDevice()).getName() + "');");
                                                }

                                                @Override
                                                public void onDetach(ACRDevice device) {
                                                    Log.d(TAG, "onDetach");
                                                    webView.sendJavascript("ACR.onDetach('" + ((BluetoothDevice) device.getDevice()).getName() + "');");
                                                }
                                            }
        );
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        getActivity().registerReceiver(bluetoothBroadcastReceiver, filter);
    }


//        @Override
//        public void onPause(boolean multitasking) {
//            super.onPause(multitasking);
//            getActivity().unregisterReceiver(bluetoothBroadcastReceiver);
//        }

    private void useUsbReader(CordovaInterface cordova, final CordovaWebView webView) {
        usbManager = (UsbManager) cordova.getActivity().getSystemService(Context.USB_SERVICE);
        ACRReader reader = new USBReader(usbManager);
        nfcReader = new NFCReader(reader);
        nfcReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNumber, int previousState, int currentState) {
//                if (!nfcReader.isProcessing()) {
                Log.d(TAG, "slotNumber " + slotNumber);
                Log.d(TAG, "previousState " + previousState);
                Log.d(TAG, "currentState " + currentState);


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

        nfcReader.setOnStatusChangeListener(new ACRReader.StatusChangeListener() {

                                                @Override
                                                public void onReady(ACRReader reader) {
                                                    Log.d(TAG, "onReady");
                                                    initReader(null, null);
                                                    webView.sendJavascript("ACR.onReady('" + reader.getReaderName() + "');");
                                                }

                                                @Override
                                                public void onAttach(ACRDevice device) {
                                                    Log.d(TAG, "onAttach");
                                                    webView.sendJavascript("ACR.onAttach('" + ((UsbDevice) device.getDevice()).getDeviceName() + "');");
                                                }

                                                @Override
                                                public void onDetach(ACRDevice device) {
                                                    Log.d(TAG, "onDetach");
                                                    webView.sendJavascript("ACR.onDetach('" + ((UsbDevice) device.getDevice()).getDeviceName() + "');");
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
        setupTimer();
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
        } else if (action.equalsIgnoreCase(INIT_READER)) {
            initReader(callbackContext, data);
        } else if (action.equalsIgnoreCase(GET_FIRMWARE_VERSION)) {
            getFirmwareVersion(callbackContext);
        } else if (action.equalsIgnoreCase(GET_LED_STATUS)) {
            getLedStatus(callbackContext);
        } else if (action.equalsIgnoreCase(GET_RECEIVED_DATA)) {
            getReceivedData(callbackContext);
        } else if (action.equalsIgnoreCase(IS_READY)) {
            if (nfcReader != null && nfcReader.isReady()) {
                callbackContext.success();
            } else {
                callbackContext.error("Reader is not ready.");
            }
        } else {
            return false;
        }
        return true;
    }

    private void initReader(CallbackContext callbackContext, JSONArray data) {
        nfcReader.updatePICCOperatingParameter(generateResultListener(null));
    }


    private void initNTAG213(CallbackContext callbackContext, JSONArray data) {
        InitNTAGParams initNTAGParams = new InitNTAGParams(0);
        try {
            initNTAGParams.setOldPassword(data.getString(0));
            initNTAGParams.setPassword(data.getString(1));
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

    private void getFirmwareVersion(final CallbackContext callbackContext) {
        BaseParams firmwareVersionParams = new BaseParams(0);
        firmwareVersionParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.getFirmwareVersion(firmwareVersionParams);
    }

    private void getReceivedData(final CallbackContext callbackContext) {
        BaseParams receivedDataParams = new BaseParams(0);
        receivedDataParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.getReceivedData(receivedDataParams);
    }

    private void getLedStatus(final CallbackContext callbackContext) {
        BaseParams ledStatusParams = new BaseParams(0);
        ledStatusParams.setOnGetResultListener(generateResultListener(callbackContext));
        nfcReader.getLedStatus(ledStatusParams);
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
            readParams.setPassword(data.getString(1));
            readParams.setOnGetResultListener(generateResultListener(callbackContext));
            nfcReader.readData(readParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeData(final CallbackContext callbackContext, JSONArray data) {
        try {
            WriteParams writeParams = new WriteParams(0, data.getInt(0), data.getString(1));
            writeParams.setPassword(data.getString(2));
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
                Log.d(TAG, "==========" + result.getCommand() + "==========");
                Log.d(TAG, result.isSendPlugin() ? "Send to Plugin" : "Does not Send to Plugin");
                Log.d(TAG, "Code: " + result.getCodeString());
                if (result.getData() != null) {
                    Log.d(TAG, "Data: " + Util.ByteArrayToHexString(result.getData()));
                }
                Log.d(TAG, "====================");
                if (callbackContext != null && result.isSendPlugin()) {
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

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }
}