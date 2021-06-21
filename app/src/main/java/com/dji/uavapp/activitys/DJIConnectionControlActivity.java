package com.dji.uavapp.activitys;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;

import com.dji.uavapp.constant.Constant;


/**
 * This activity will launch when a USB accessory is attached and attempt to connect to the USB
 * accessory.
 */
public class DJIConnectionControlActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));

        Intent usbIntent = getIntent();
        if (usbIntent != null) {
            String action = usbIntent.getAction();
            if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                Intent attachedIntent = new Intent();
                attachedIntent.setAction(Constant.ACCESSORY_ATTACHED);
                sendBroadcast(attachedIntent);
            }
        }

        finish();
    }
}