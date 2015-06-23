package com.frankgreen.task;

import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;

import com.frankgreen.NFCReader;
import com.frankgreen.Util;
import com.frankgreen.apdu.command.UpdateBinaryBlock;

/**
 * Created by kevin on 6/10/15.
 */
public class WriteAuthenticate extends AsyncTask<AuthParams, Void, Boolean> {
    @Override
    protected Boolean doInBackground(AuthParams... authParamses) {

        AuthParams authParams = authParamses[0];
        if (authParams == null) {
            return false;
        }
        int block = 3;
        if (authParams.getBlock() >=4){
           block = (authParams.getBlock() / 4) * 4 + 3;
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
        byte[] keyA = Util.toNFCByte(authParams.getKeyA(), 16);
        byte[] keyB = Util.toNFCByte(authParams.getKeyB(), 16);

        System.arraycopy(keyA, 0, data, 0, 6);
        System.arraycopy(keyB, 0, data, 10, 6);
        WriteParams writeParams = new WriteParams(authParams.getSlotNumber(),authParams.getBlock(), data);
        writeParams.setReader(authParams.getReader());
        writeParams.setOnGetResultListener(authParams.getOnGetResultListener());
        UpdateBinaryBlock update = new UpdateBinaryBlock(writeParams);
        return update.run();
    }
}
