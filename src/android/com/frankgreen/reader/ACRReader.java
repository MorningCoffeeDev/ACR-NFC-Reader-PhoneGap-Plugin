package com.frankgreen.reader;


import android.app.PendingIntent;
import android.content.Intent;

import com.acs.smartcard.Reader;
import com.frankgreen.ACRDevice;
import com.frankgreen.NFCReader;
import com.frankgreen.apdu.OnGetResultListener;

import java.util.List;

public interface ACRReader {

    public void setNfcReader(NFCReader nfcReader);
    public byte[] getReceiveBuffer();
    public int transmit(int slotNum, byte[] sendBuffer, int sendBufferLength, byte[] recvBuffer, int recvBufferLength);


    public void control(int slot, byte[] sendBuffer, OnDataListener listener);
    public void transmit(int slot, byte[] sendBuffer, OnDataListener listener);
    public void connect();

    public void setOnStateChangeListener(Reader.OnStateChangeListener onStateChangeListener);
    public void setOnStatusChangeListener(StatusChangeListener onStatusChangeListener);
    public void detach(Intent intent);
    public void attach(Intent intent);
    public void listen(OnGetResultListener listener);
    public String getReaderName();
    public int getNumSlots();

    public interface StatusChangeListener {
        void onReady(ACRReader reader);

        void onAttach(ACRDevice device);

        void onDetach(ACRDevice device);
    }
    public void close();
    public StatusChangeListener getOnStatusChangeListener();
    public PendingIntent getmPermissionIntent();
    public void setPermissionIntent(PendingIntent permissionIntent);
    public OnGetResultListener getOnTouchListener();
    public boolean isReady();
    public List<String> getmSlotList();
    public void setmSlotList(List<String> mSlotList);
    public void setReady(boolean ready);
    public byte[] power(int slotNum, int action, OnDataListener listener);
    public int setProtocol(int slotNum, int preferredProtocols);
    public String getReaderType();
    public void getBatteryLevel();
    public int getBatteryLevelValue();
    public void disconnect();
}
