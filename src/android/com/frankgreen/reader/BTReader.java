package com.frankgreen.reader;


import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.hardware.usb.UsbDevice;

import android.util.Log;
import com.acs.bluetooth.*;
import com.acs.smartcard.Reader;
import com.frankgreen.ACRDevice;
import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.Utils;
import com.frankgreen.apdu.OnGetResultListener;
import com.frankgreen.apdu.Result;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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

    private int connectState = DISCONNECTED;
    private static final int DISCONNECTED = 0;
    private static final int CONNECTING = 1;
    private static final int CONNECTED = 2;

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
        this.readerType = "BT_READER";
        findBondedDevice();
        initGattCallback();
    }

    private synchronized void connectReader() {
        connectState = CONNECTING;
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Unable to obtain a BluetoothAdapter.");
        }

        /* Create a new connection. */
        Set<BluetoothDevice> devices = (Set<BluetoothDevice>) bluetoothManager.getAdapter().getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.d(TAG, device.getAddress());
            Log.d(TAG, device.getName());
            Log.d(TAG, "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&current bond state*************************" + device.getBondState());
        /* Connect to GATT server. */
            if (device.getName().contains("ACR12") && device.getBondState() == BluetoothDevice.BOND_BONDED) {
                this.device = device;
                found = true;
                if (isReaderNotClosed) {
                    BTReader.this.getOnStatusChangeListener().onAttach(new ACRDevice<BluetoothDevice>(device));
                }
                mBluetoothGatt = device.connectGatt(activity, false, gattCallback);
                boolean cr = mBluetoothGatt.discoverServices();
//                int state =  mBluetoothGatt.getConnectionState(device);
//                Log.d(TAG, "Current device state" + state);
                return;
            }
        }
        if (!found) {
            connectState = DISCONNECTED;
        }
    }

    public void initGattCallback() {
        gattCallback = new BluetoothReaderGattCallback();
        gattCallback.setOnConnectionStateChangeListener(new BluetoothReaderGattCallback.OnConnectionStateChangeListener() {
            @Override
            public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int state, int newState) {
                Log.d(TAG, "onConnectionStateChange:" + String.valueOf(newState));
                Log.d(TAG, "connection state:" + state);
                Log.d(TAG, "connection newState:" + newState);
//                mConnectState = BluetoothReader.STATE_DISCONNECTED;
                isReaderNotClosed = true;
                if (newState == BluetoothReader.STATE_CONNECTED) {
                    if (bluetoothReaderManager != null) {
                        Log.d(TAG, "detectReader");
                        bluetoothReaderManager.detectReader(bluetoothGatt, gattCallback);
                    }
                } else if (newState == BluetoothReader.STATE_DISCONNECTED) {
                    ready = false;
                    BTReader.this.disconnect();
                    connectState = DISCONNECTED;
                    Log.d(TAG, "---------on detach++++++++++++");
                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
                }
                if (state != BluetoothReader.STATE_CONNECTED && state != BluetoothReader.STATE_DISCONNECTED) {
                    Log.d(TAG, "connection---------------:" + state);
                    connectState = DISCONNECTED;
                    isReaderNotClosed = false;
                    BTReader.this.getOnStatusChangeListener().onDetach(new ACRDevice<BluetoothDevice>(device));
                }
            }
        });

    }

    public void findBondedDevice() {
        Log.d(TAG, "bluetoothsupport....................");

        bluetoothReaderManager.setOnReaderDetectionListener(new BluetoothReaderManager.OnReaderDetectionListener() {
            @Override
            public void onReaderDetection(BluetoothReader bluetoothReader) {
                if (bluetoothReader instanceof Acr3901us1Reader) {
                            /* The connected reader is ACR3901U-S1 reader. */
                    Log.d(TAG, "On Acr3901us1Reader Detected.");
                } else if (bluetoothReader instanceof Acr1255uj1Reader) {
                            /* The connected reader is ACR1255U-J1 reader. */
                    Log.d(TAG, "On Acr1255uj1Reader Detected.");
                } else {
                    Log.d(TAG, "Not this reader");
                }
                reader = (Acr1255uj1Reader) bluetoothReader;
                setListener(reader);
                reader.enableNotification(true);
            }
        });

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
                Log.d(TAG, "---------------[Response]------------" + receiveBuffer);
                Log.d(TAG, "Data: " + Util.ByteArrayToHexString(receiveBuffer));
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
                Log.d(TAG, "---------------[Escape Response]------------" + receiveBuffer);
                Log.d(TAG, "Data: " + Util.ByteArrayToHexString(receiveBuffer));
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
    public void disconnect() {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    private boolean initialized = false;

    @Override
    public void start() {
        initialized = true;
        connectReader();

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
        reader.transmitApdu(sendBuffer);
    }

    @Override
    public int transmit(int slotNum, byte[] sendBuffer, int sendBufferLength, byte[] recvBuffer, int recvBufferLength) {
        return 0;
    }

    @Override
    public void control(int slot, byte[] sendBuffer, OnDataListener listener) {
        this.onDataListener = listener;
        reader.transmitEscapeCommand(sendBuffer);
    }

    @Override
    public void connect() {
//        if (isReady() || connectState == CONNECTING) {
//            return;
//        } else {
//            connectReader();
//        }
        Log.d(TAG, "current connectState:" + connectState);
        if (initialized && connectState == DISCONNECTED) {
            connectReader();
        } else {
            return;
        }
    }
}
