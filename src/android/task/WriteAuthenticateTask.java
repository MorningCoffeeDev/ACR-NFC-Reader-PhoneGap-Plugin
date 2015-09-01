package com.frankgreen.task;

import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.command.UpdateBinaryBlock;

/**
 * Created by kevin on 6/10/15.
 */
public class WriteAuthenticateTask extends AsyncTask<AuthParams, Void, Boolean> {
    @Override
    protected Boolean doInBackground(AuthParams... paramses) {

        AuthParams params = paramses[0];
        if (params == null) {
            return false;
        }
        if(!params.getReader().isReady()){
            params.getReader().raiseNotReady(params.getOnGetResultListener());
            return false;
        }
        int block = 3;
        if (params.getBlock() >=4){
           block = (params.getBlock() / 4) * 4 + 3;
        }
        byte[] data = new byte[]{
                (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff, // keyA
                // data: keyA for read, keyB for write
                // control: keyA never been read, write by KeyB
                //          keyB never been read, write by KeyB
                //          control byte:keyA or KeyB for read, write by KeyB
                (byte)0x08,(byte)0x77,(byte)0x8f,(byte)0x69,
                (byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff  // keyB
        };
        byte[] keyA = Util.convertHexAsciiToByteArray(params.getKeyA(), 6);
        byte[] keyB = Util.convertHexAsciiToByteArray(params.getKeyB(), 6);

        System.arraycopy(keyA, 0, data, 0, 6);
        System.arraycopy(keyB, 0, data, 10, 6);
        WriteParams writeParams = new WriteParams(params.getSlotNumber(),params.getBlock(), data);
        writeParams.setReader(params.getReader());
        writeParams.setOnGetResultListener(params.getOnGetResultListener());
        UpdateBinaryBlock update = new UpdateBinaryBlock(writeParams);
        return update.run();
    }
}
