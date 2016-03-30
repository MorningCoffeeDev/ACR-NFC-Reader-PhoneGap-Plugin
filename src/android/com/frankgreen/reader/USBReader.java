package com.frankgreen.reader;


import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.acs.smartcard.Reader;
import com.acs.smartcard.ReaderException;
import com.frankgreen.ACRDevice;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

import java.util.ArrayList;
import java.util.List;

public class USBReader implements ACRReader {

    public static StatusChangeListener onStatusChangeListener;
    private static final String TAG = "ACRReader";
    private UsbManager mManager;
    private Reader mReader;
    private List<String> mReaderList;
    private List<String> mSlotList;
    private OnGetResultListener onTouchListener;
    private PendingIntent mPermissionIntent;
    private boolean ready = false;
    private String readerType = "";
    private NFCReader nfcReader;

    @Override
    public void setNfcReader(NFCReader nfcReader) {
        this.nfcReader = nfcReader;
    }

    public USBReader(UsbManager mManager) {
        this.mManager = mManager;
        this.mReader = new Reader(mManager);
        this.readerType = "USB_READER";
    }

    public int getNumSlots() {
        return mReader.getNumSlots();
    }

//    @Override
//    public int transmit(int slotNum, byte[] sendBuffer, int sendBufferLength, byte[] recvBuffer, int recvBufferLength) throws ACRReaderException {
//        try {
//            return mReader.transmit(slotNum, sendBuffer, sendBufferLength, recvBuffer, recvBufferLength);
//        } catch (ReaderException e) {
//            throw new ACRReaderException(e);
//        }
//    }

    @Override
    public void setOnStateChangeListener(Reader.OnStateChangeListener onStateChangeListener) {
        mReader.setOnStateChangeListener(onStateChangeListener);
    }

    @Override
    public void setOnStatusChangeListener(StatusChangeListener onStatusChangeListener) {
        this.onStatusChangeListener = onStatusChangeListener;
    }

    @Override
    public void attach(Intent intent) {
        UsbDevice device = (UsbDevice) intent
                .getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (intent.getBooleanExtra(
                UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

            if (device != null) {
                Log.d(TAG, "Opening reader: " + device.getDeviceName()
                        + "...");
                if (onStatusChangeListener != null) {
                    onStatusChangeListener.onAttach(new ACRDevice<UsbDevice>(device));
                }
                open(device);
            }

        } else {
            Log.w(TAG, "Permission denied for device "
                    + device.getDeviceName());

        }
    }

    @Override
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
            onStatusChangeListener.onDetach(new ACRDevice<UsbDevice>(device));
        }
        ready = false;
        close();

    }

    @Override
    public void listen(OnGetResultListener listener) {
        onTouchListener = listener;
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

    public StatusChangeListener getOnStatusChangeListener() {
        return onStatusChangeListener;
    }

    public String getReaderName() {
        return mReader.getReaderName();
    }

    @Override
    public void close() {
        mReader.close();
    }

    public void open(UsbDevice usbDevice) {
        new OpenTask().execute(usbDevice);
    }

    public PendingIntent getmPermissionIntent() {
        return mPermissionIntent;
    }

    public void setPendingIntent(PendingIntent permissionIntent) {
        this.mPermissionIntent = permissionIntent;
    }

    public OnGetResultListener getOnTouchListener() {
        return onTouchListener;
    }

    public boolean isReady() {
        return ready;
    }

    public List<String> getmSlotList() {
        return mSlotList;
    }

    public void setmSlotList(List<String> mSlotList) {
        this.mSlotList = mSlotList;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getReaderType() {
        return readerType;
    }

    @Override
    public void setPermissionIntent(PendingIntent permissionIntent) {
        this.mPermissionIntent = permissionIntent;
    }

    @Override
    public byte[] power(int slotNum, int action, OnDataListener listener) throws ACRReaderException {
        try {
            listener.onData(this.mReader.power(slotNum, action), this.mReader.power(slotNum, action).length);
            return null;
        } catch (ReaderException e) {
            throw new ACRReaderException(e);
        }
    }

    @Override
    public int setProtocol(int slotNum, int preferredProtocols) throws ACRReaderException {
        try {
            return this.mReader.setProtocol(slotNum, preferredProtocols);
        } catch (ReaderException e) {
            throw new ACRReaderException(e);
        }
    }

    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {
            Exception result = null;
            try {
                mReader.open(params[0]);
//                acrReader.open(params[0]);
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
                Log.d(TAG, "Reader name: " + USBReader.this.getReaderName());

                int numSlots = USBReader.this.getNumSlots();
                Log.d(TAG, "Number of slots: " + numSlots);
                USBReader.this.setReady(true);
                if (USBReader.this.getOnStatusChangeListener() != null) {
                    USBReader.this.getOnStatusChangeListener().onReady(USBReader.this);
                }
                // Add slot items
                if (USBReader.this.getmSlotList() == null) {
                    USBReader.this.setmSlotList(new ArrayList<String>());
                }
                USBReader.this.getmSlotList().clear();
                for (int i = 0; i < numSlots; i++) {
                    USBReader.this.getmSlotList().add(Integer.toString(i));
                }
            }
        }
    }

    @Override
    public byte[] getReceiveBuffer() {
        return new byte[0];
    }

    @Override
    public void transmit(int slot, byte[] sendBuffer, OnDataListener listener) {
        byte[] receiveBuffer = new byte[300];
        try {
            int len = mReader.transmit(slot, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            listener.onData(receiveBuffer, len);
        } catch (ReaderException e) {
            listener.onError(new ACRReaderException(e));
        }
    }

    @Override
    public void control(int slot, byte[] sendBuffer, OnDataListener listener) {
        Log.d(TAG, "****slot***" + slot);
        byte[] receiveBuffer = new byte[30];
        try {
            int len = mReader.control(slot, Reader.IOCTL_CCID_ESCAPE, sendBuffer, sendBuffer.length, receiveBuffer, receiveBuffer.length);
            listener.onData(receiveBuffer, len);
        } catch (ReaderException e) {
            listener.onError(new ACRReaderException(e));
        }
    }

    @Override
    public int transmit(int slotNum, byte[] sendBuffer, int sendBufferLength, byte[] recvBuffer, int recvBufferLength) {
        return 0;
    }

    @Override
    public void connect() {
    }

    @Override
    public int getBatteryLevelValue() {
        return 0;
    }

    @Override
    public void getBatteryLevel() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void start() {

    }
}
