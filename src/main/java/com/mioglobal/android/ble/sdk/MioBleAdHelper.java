package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.DFU.UUIDUtils;
import java.util.UUID;

public class MioBleAdHelper {
    public static final int DATA_TYPE_BLE_DEVICE_INVALID = 0;
    public static final int DATA_TYPE_BLE_DEVICE_NAME = 9;
    public static final int DATA_TYPE_BLE_DEVICE_UUID = 7;
    private String deviceName;

    public MioBleAdHelper(byte[] scanRecord) {
        int index = DATA_TYPE_BLE_DEVICE_INVALID;
        while (index < scanRecord.length) {
            int index2 = index + 1;
            int length = scanRecord[index];
            if (length == 0) {
                index = index2;
                return;
            }
            int type = scanRecord[index2];
            if (type == 0) {
                index = index2;
                return;
            } else if (type == DATA_TYPE_BLE_DEVICE_NAME) {
                try {
                    if (((index2 + 1) + index2) + length > scanRecord.length) {
                        this.deviceName = new String(scanRecord, index2 + 1, (scanRecord.length - index2) - 1);
                    } else {
                        this.deviceName = new String(scanRecord, index2 + 1, index2 + length);
                    }
                    this.deviceName = this.deviceName.trim();
                    index = index2;
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    this.deviceName = "unknown device";
                    index = index2;
                    return;
                }
            } else {
                index = index2;
            }
        }
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public static String parseDeviceUUID(byte[] scanRecord) {
        byte[] reversedUUIDbytes = new byte[16];
        for (int i = DATA_TYPE_BLE_DEVICE_INVALID; i < 16; i++) {
            reversedUUIDbytes[i] = scanRecord[i + 5];
        }
        UUID uuid = UUIDUtils.fromByteArray(UUIDUtils.reverse(reversedUUIDbytes), DATA_TYPE_BLE_DEVICE_INVALID);
        if (uuid == null) {
            return null;
        }
        return uuid.toString().toUpperCase();
    }
}
