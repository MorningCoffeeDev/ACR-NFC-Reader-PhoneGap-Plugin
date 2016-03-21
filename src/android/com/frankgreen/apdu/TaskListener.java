package com.frankgreen.apdu;

import com.acs.smartcard.ReaderException;

/**
 * Created by kevin on 16/3/18.
 */
public interface TaskListener {
   void onSuccess();
   void onFailure();
}
