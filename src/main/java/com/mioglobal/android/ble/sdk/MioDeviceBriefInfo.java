package com.mioglobal.android.ble.sdk;

import org.apache.commons.lang3.builder.DiffResult;

public class MioDeviceBriefInfo {
    public String companyID;
    public String deviceAddress;
    public String deviceName;
    public String deviceUID;

    public MioDeviceBriefInfo() {
        this.deviceUID = DiffResult.OBJECTS_SAME_STRING;
        this.deviceName = DiffResult.OBJECTS_SAME_STRING;
        this.deviceAddress = DiffResult.OBJECTS_SAME_STRING;
        this.companyID = DiffResult.OBJECTS_SAME_STRING;
    }
}
