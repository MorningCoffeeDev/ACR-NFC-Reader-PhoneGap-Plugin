package com.frankgreen.reader;


import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.*;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Intent;
import android.os.Handler;

import android.util.Log;
import com.acs.bluetooth.*;
import com.acs.smartcard.Reader;
import com.frankgreen.ACRDevice;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.Utils;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.operate.CustomDevice;
import com.frankgreen.operate.OperateDataListener;
import com.frankgreen.operate.OperateResult;
import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;


public class BTReader implements ACRReader {
    public static StatusChangeListener onStatusChangeListener;
    private BluetoothManager bluetoothManager;
    private Acr1255uj1Reader reader;
    private BluetoothReaderGattCallback gattCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothReaderManager bluetoothReaderManager;
    private Activity activity;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    private CallbackContext startScanCallbackContext;

    private NFCReader nfcReader;
    private final String TAG = "BTReader";
    private int mConnectState = BluetoothReader.STATE_DISCONNECTED;
    private BluetoothDevice device;
    private boolean ready = false;
    private boolean batteryAvailable = true;
    private int batteryLevel;
    private byte[] masterKey;
    private String readerType = "";
    private static final byte[] AUTO_POLLING_START = {(byte) 0xE0, 0x00, 0x00,
            0x40, 0x01};
    private OnGetResultListener onTouchListener;
    private static final int CARDON = 2;
    private static final int CARDOFF = 1;
    private OnDataListener onDataListener;
    private OnDataListener onPowerListener;
    private OperateDataListener operateDataListener;

    private int connectState = DISCONNECTED;
    private static final int DISCONNECTED = 0;
    private static final int CONNECTING = 1;
    private static final int CONNECTED = 2;

    private int scanState = NONSCANNING;
    private static final int NONSCANNING = 0;
    private static final int SCANNING = 1;

    private static final int READE_REAL_CLOSED = 133;
    private boolean isReaderNotClosed = true;

    @Override
    public void setNfcReader(NFCReader nfcReader) {
        this.nfcReader = nfcReader;
    }

    @Override
    public byte[] getReceiveBuffer() {
        return receiveBuffer;
    }

    private byte[] receiveBuffer;

    public BTReader(BluetoothManager bluetoothManager, Activity activity) {
        this.activity = activity;
        this.bluetoothManager = bluetoothManager;
        bluetoothReaderManager = new BluetoothReaderManager();
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        this.readerType = "BT_READER";
        findBondedDevice();
        initGattCallback();
    }

    private synchronized boolean connectReader(String mDeviceAddress) {
        if (bluetoothManager == null || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            this.operateDataListener.onError(new OperateResult("Bluetooth error, please check your bluetooth setting!"));
            return false;
        }

        if (mBluetoothGatt != null) {
            Log.d(TAG, "Clear old gatt");
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        Log.d(TAG, "address..: " + mDeviceAddress);

        final BluetoothDevice device;
        try {
            device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        } catch (Exception e) {
            this.operateDataListener.onError(new OperateResult("Device address format error."));
            return false;
        }
        Log.d(TAG, "bluetooth device:" + device);
        this.device = device;
        connectState = CONNECTING;
        mBluetoothGatt = device.connectGatt(activity, false, gattCallback);
        return true;
    }

    public void initGattCallback() {
        gattCallback = new BluetoothReaderGattCallback();
        gattCallback.setOnConnectionStateChangeListener(new BluetoothReaderGattCallback.OnConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int state, int newState) {
                Log.d(TAG, "onConnectionStateChange:" + String.valueOf(newState));
                Log.d(TAG, "connection state:" + state);
                Log.d(TAG, "connection newState:" + newState);
                isReaderNotClosed = true;
                if (state != BluetoothGatt.GATT_SUCCESS) {
                    connectState = DISCONNECTED;
                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        if (BTReader.this.operateDataListener != null) {
                            BTReader.this.operateDataListener.onError(new OperateResult("Connect fail!"));
                        }
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        if (BTReader.this.operateDataListener != null) {
                            BTReader.this.operateDataListener.onError(new OperateResult("Disconnect fail!"));
                        }
                    }
                    return;
                }

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (bluetoothReaderManager != null) {
                        Log.d(TAG, "detectReader");
                        bluetoothReaderManager.detectReader(bluetoothGatt, gattCallback);
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    BTReader.this.reader = null;

                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                    }
                    connectState = DISCONNECTED;
                    if (BTReader.this.operateDataListener != null) {
                        BTReader.this.operateDataListener.onData(new OperateResult("Reader has been disconnected!"));
                    }
                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
                }
//                if (newState == BluetoothReader.STATE_CONNECTED && state == BluetoothReader.STATE_DISCONNECTED) {
//                    if (bluetoothReaderManager != null) {
//                        Log.d(TAG, "detectReader");
//                        bluetoothReaderManager.detectReader(bluetoothGatt, gattCallback);
//                    }
//                } else if (newState == BluetoothReader.STATE_DISCONNECTED) {
//                    Log.d(TAG, "Disconnect!!!!!");
//                    BTReader.this.reader = null;
//                    ready = false;
//                    BTReader.this.closeGatt();
//                    connectState = DISCONNECTED;
//                    if (BTReader.this.operateDataListener != null) {
//                        BTReader.this.operateDataListener.onData(new OperateResult("Reader has been disconnected!"));
//                        BluetoothGatt
//                    }
//                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
//                } else {
//                    BTReader.this.reader = null;
//                    BTReader.this.closeGatt();
//                    connectState = DISCONNECTED;
//                    Log.d(TAG, "---------on detach++++++++++++");
//                    if (BTReader.this.operateDataListener != null) {
//                        BTReader.this.operateDataListener.onError(new OperateResult("Device Not support"));
//                    }
//                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
//                }
            }
        });
    }

    private void closeGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public void findBondedDevice() {
        Log.d(TAG, "bluetoothsupport....................");

        bluetoothReaderManager.setOnReaderDetectionListener(new BluetoothReaderManager.OnReaderDetectionListener() {
            @Override
            public void onReaderDetection(BluetoothReader bluetoothReader) {
                if (bluetoothReader instanceof Acr1255uj1Reader) {
                            /* The connected reader is ACR1255U-J1 reader. */
                    Log.d(TAG, "On Acr1255uj1Reader Detected.");
                    reader = (Acr1255uj1Reader) bluetoothReader;
                    setListener(reader);
                    reader.enableNotification(true);
                    return;
                }

                Log.d(TAG, "Device Not support");
                BTReader.this.closeGatt();
                connectState = DISCONNECTED;
                BTReader.this.operateDataListener.onError(new OperateResult("Device Not support"));
                BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
            }
        });

    }

    @Override
    public void startScan(CallbackContext startScanCallbackContext) {
        if (bluetoothManager == null || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            startScanCallbackContext.error(Util.customJSON(false, "Bluetooth error, please check your bluetooth setting!"));
            return;
        }

        if (this.startScanCallbackContext != null) {
            Log.d(TAG, "Already in Scanning!");
            this.startScanCallbackContext.error(Util.customJSON(false, "Already in Scanning!"));
            this.startScanCallbackContext = startScanCallbackContext;
            return;
        }
        Log.d(TAG, "startScan!!!");
        this.scanLeDevice(true, startScanCallbackContext);
    }

    @Override
    public void stopScan() {
        if (bluetoothManager == null || mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            this.startScanCallbackContext.error(Util.customJSON(false, "Bluetooth error, please check your bluetooth setting!"));
            return;
        }

        if (this.startScanCallbackContext != null) {
            this.scanLeDevice(false, null);
            return;
        }
        Log.d(TAG, "Already not in Scanning!");
    }

    private synchronized void scanLeDevice(boolean enable, final CallbackContext callbackContext) {
        if (enable) {
            // Stops scanning after a predefined scan period.
            Log.d(TAG, "Scanning!!!");
            this.startScanCallbackContext = callbackContext;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    Log.d("BTReader", "Scan Reader Complete!!!");
                    BTReader.this.startScanCallbackContext.success(Util.customJSON(true, "Scan complete!"));
                    BTReader.this.startScanCallbackContext = null;
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.removeCallbacksAndMessages(null);
            this.startScanCallbackContext.success(Util.customJSON(true, "Scan complete"));
            this.startScanCallbackContext = null;
        }
    }

    @Override
    public void setOnStateChangeListener(Reader.OnStateChangeListener onStateChangeListener) {

    }

    @Override
    public void setOnStatusChangeListener(StatusChangeListener onStatusChangeListener) {
        this.onStatusChangeListener = onStatusChangeListener;
    }

    @Override
    public void detach(Intent intent) {

    }

    @Override
    public void attach(Intent intent) {

    }

    @Override
    public void listen(OnGetResultListener listener) {
        this.onTouchListener = listener;
    }

    @Override
    public String getReaderName() {
        if (this.device != null) {
            return device.getName();
        }
        return null;
    }

    @Override
    public String getReaderType() {
        return readerType;
    }

    @Override
    public int getNumSlots() {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public StatusChangeListener getOnStatusChangeListener() {
        return onStatusChangeListener;
    }

    @Override
    public PendingIntent getmPermissionIntent() {
        return null;
    }

    @Override
    public void setPermissionIntent(PendingIntent permissionIntent) {

    }

    @Override
    public OnGetResultListener getOnTouchListener() {
        return onTouchListener;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public List<String> getmSlotList() {
        return null;
    }

    @Override
    public void setmSlotList(List<String> mSlotList) {

    }

    @Override
    public void setReady(boolean ready) {

    }

    @Override
    public int setProtocol(int slotNum, int preferredProtocols) {
        return 0;
    }

    @Override
    public byte[] power(int slotNum, int action, OnDataListener listener) {
        this.onPowerListener = listener;
        reader.powerOnCard();
        return null;
    }

    public void setListener(Acr1255uj1Reader acrReader) {
        acrReader.setOnAtrAvailableListener(new BluetoothReader.OnAtrAvailableListener() {
            @Override
            public void onAtrAvailable(BluetoothReader bluetoothReader, byte[] atr, int i) {
                Log.d(TAG, "ATR: " + Util.ByteArrayToHexString(atr));
                if (BTReader.this.onPowerListener != null) {
                    BTReader.this.onPowerListener.onData(atr, atr.length);
                }
            }
        });

        acrReader.setOnBatteryLevelAvailableListener(new Acr1255uj1Reader.OnBatteryLevelAvailableListener() {
            @Override
            public void onBatteryLevelAvailable(BluetoothReader bluetoothReader, int batteryLevel, int status) {
                BTReader.this.batteryLevel = batteryLevel;
                Log.d(TAG, "**********bluetoothReader Battery level [available]*******" + batteryLevel);
            }
        });


        acrReader.setOnBatteryLevelChangeListener(new Acr1255uj1Reader.OnBatteryLevelChangeListener() {
            @Override
            public void onBatteryLevelChange(BluetoothReader bluetoothReader, int batteryLevel) {
                BTReader.this.batteryLevel = batteryLevel;
                Log.d(TAG, "**********bluetoothReader Battery level [change]*******" + batteryLevel);
            }
        });

        acrReader.setOnCardStatusChangeListener(new BluetoothReader.OnCardStatusChangeListener() {
            @Override
            public void onCardStatusChange(BluetoothReader bluetoothReader, int i) {
                Log.d(TAG, "--------bluetoothReader On Card Listener----------" + i);
                if (i == CARDON) {
                    nfcReader.reset(0);
                } else if (i == CARDOFF) {
                    nfcReader.getCordovaWebView().sendJavascript("ACR.runCardAbsent();");
                }
            }
        });

        acrReader.setOnCardStatusAvailableListener(new BluetoothReader.OnCardStatusAvailableListener() {
            @Override
            public void onCardStatusAvailable(BluetoothReader bluetoothReader, int i, int i1) {
                Log.d(TAG, "----------bluetoothReader ON CardStatusAvailable -----" + i);
                Log.d(TAG, "----------bluetoothReader ON CardStatusAvailable -----" + i1);
            }
        });

        acrReader.setOnEnableNotificationCompleteListener(new BluetoothReader.OnEnableNotificationCompleteListener() {
            @Override
            public void onEnableNotificationComplete(BluetoothReader bluetoothReader, int i) {
                masterKey = initMasterKey();
                Log.d(TAG, "bluetoothReader On Enable Notification listener: --" + i);
                reader.authenticate(masterKey);
            }
        });

        acrReader.setOnAuthenticationCompleteListener(new BluetoothReader.OnAuthenticationCompleteListener() {
            @Override
            public void onAuthenticationComplete(BluetoothReader bluetoothReader, int i) {
                Log.d(TAG, "onAuthenticationComplete ------" + i);
                reader.transmitEscapeCommand(AUTO_POLLING_START);
                BTReader.this.getBatteryLevel();
                ready = true;
                connectState = CONNECTED;
                if (BTReader.this.operateDataListener != null) {
                    CustomDevice customDevice = new CustomDevice(device.getName(), device.getAddress());
                    BTReader.this.operateDataListener.onData(new OperateResult(customDevice));
                }
                if (BTReader.this.getOnStatusChangeListener() != null) {
                    BTReader.this.getOnStatusChangeListener().onReady(BTReader.this);
                }
            }
        });

        acrReader.setOnDeviceInfoAvailableListener(new BluetoothReader.OnDeviceInfoAvailableListener() {
            @Override
            public void onDeviceInfoAvailable(BluetoothReader bluetoothReader, int i, Object o, int i1) {
                Log.d(TAG, "setOnDeviceInfoAvailableListener --------");
            }
        });

        acrReader.setOnResponseApduAvailableListener(new BluetoothReader.OnResponseApduAvailableListener() {
            @Override
            public void onResponseApduAvailable(BluetoothReader bluetoothReader, byte[] receiveBuffer, int errorCode) {
                Log.d(TAG, "APDU Receive: " + Util.ByteArrayToHexString(receiveBuffer));
                Log.d(TAG, "code: " + String.valueOf(errorCode));
                if (BTReader.this.onDataListener != null) {
                    BTReader.this.onDataListener.onData(receiveBuffer, receiveBuffer.length);
                }
                BTReader.this.receiveBuffer = receiveBuffer;
            }
        });

        acrReader.setOnEscapeResponseAvailableListener(new BluetoothReader.OnEscapeResponseAvailableListener() {
            @Override
            public void onEscapeResponseAvailable(BluetoothReader bluetoothReader, byte[] receiveBuffer, int i) {
                Log.d(TAG, "Escape Receive: " + Util.ByteArrayToHexString(receiveBuffer));
                Log.d(TAG, "code: " + String.valueOf(i));
                if (BTReader.this.onDataListener != null) {
                    BTReader.this.onDataListener.onData(receiveBuffer, receiveBuffer.length);
                }
                BTReader.this.receiveBuffer = receiveBuffer;
            }
        });
    }

    @Override
    public void getBatteryLevel() {
        boolean b = this.reader.getBatteryLevel();
        Log.d(TAG, "&&&&&&&get batterylevel&&&&&&&&&" + b);
    }

    @Override
    public int getBatteryLevelValue() {
        return batteryLevel;
    }

    @Override
    public void disconnectReader(OperateDataListener operateDataListener) {
        this.operateDataListener = operateDataListener;
        this.disconnect();
    }

    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        } else {
            operateDataListener.onError(new OperateResult("No Connected Device!"));
        }
    }

    private boolean initialized = false;

    @Override
    public void start() {
        initialized = true;
//        connectReader();

    }

    private byte[] initMasterKey() {
        try {
            String key = Utils.toHexString("ACR1255U-J1 Auth".getBytes("UTF-8"));
            if (key == null) {
                return null;
            }
            String rawdata = key.toString();

            if (rawdata == null || rawdata.isEmpty()) {
                return null;
            }
            String command = rawdata.replace(" ", "").replace("\n", "");

            if (command.isEmpty() || command.length() % 2 != 0
                    || Utils.isHexNumber(command) == false) {
                return null;
            }
            return Utils.hexString2Bytes(command);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void transmit(int slot, byte[] sendBuffer, OnDataListener listener) {
        this.onDataListener = listener;
        Log.d(TAG, "APDU Send: " + Util.ByteArrayToHexString(sendBuffer));
        reader.transmitApdu(sendBuffer);
    }

    @Override
    public int transmit(int slotNum, byte[] sendBuffer, int sendBufferLength, byte[] recvBuffer, int recvBufferLength) {
        return 0;
    }

    @Override
    public void control(int slot, byte[] sendBuffer, OnDataListener listener) {
        this.onDataListener = listener;
        Log.d(TAG, "escape Send: " + Util.ByteArrayToHexString(sendBuffer));
        reader.transmitEscapeCommand(sendBuffer);
    }

//    @Override
//    public void connect() {
//        Log.d(TAG, "current connectState:" + connectState);
//        if (initialized && connectState == DISCONNECTED) {
//            connectReader();
//        } else {
//            return;
//        }
//    }

    @Override
    public boolean connect(String address, OperateDataListener operateDataListener) {
        Log.d(TAG, "current connectState:" + connectState);
        if (initialized && connectState == DISCONNECTED) {
            this.operateDataListener = operateDataListener;
            return connectReader(address);
        } else {
            return false;
        }
    }

    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            JSONObject deviceJson = new JSONObject();
            try {
                deviceJson.put("name", device.getName());
                deviceJson.put("address", device.getAddress());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nfcReader.getCordovaWebView().sendJavascript("ACR.onScan(" + deviceJson + ")");
        }
    };
}
