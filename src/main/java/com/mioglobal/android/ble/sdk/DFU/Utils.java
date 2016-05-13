package com.mioglobal.android.ble.sdk.DFU;

import android.bluetooth.BluetoothGatt;
import android.util.Log;
import java.lang.reflect.Method;

public final class Utils {
    private static final String TAG;

    static {
        TAG = Utils.class.getName();
    }

    public static boolean refress(BluetoothGatt bluetoothgatt) {
        boolean z = false;
        BluetoothGatt localBluetoothGatt = bluetoothgatt;
        try {
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                z = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
            }
        } catch (Exception e) {
            Log.e(TAG, "An exception occured while refreshing device");
        }
        return z;
    }

    public static boolean isMIODevice(String devicename) {
        if (devicename.toUpperCase().startsWith("MIOUP") || devicename.toUpperCase().endsWith("LINK") || devicename.toUpperCase().endsWith("OTBEAT MIO LINK") || devicename.toUpperCase().endsWith("VELO") || devicename.toUpperCase().endsWith("FUSE") || devicename.toUpperCase().endsWith("ALPHA2") || devicename.toUpperCase().endsWith("ALPHA2_OTA")) {
            return true;
        }
        return false;
    }
}
