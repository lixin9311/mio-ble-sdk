package com.mioglobal.android.ble.sdk;

import android.util.Log;
import org.apache.commons.lang3.builder.DiffResult;

public class MioDeviceInformation {
    public static final int DEVICE_TYPE_MIO_ALL = 1;
    public static final int DEVICE_TYPE_MIO_ALPHA = 256;
    public static final int DEVICE_TYPE_MIO_ALPHA_1_5 = 257;
    public static final int DEVICE_TYPE_MIO_LINK = 512;
    public static final int DEVICE_TYPE_MIO_LINK_2 = 513;
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    private String companyID;
    private String deviceUID;
    private String firmwareRevisionString;
    private String hardwareRevisionString;
    private boolean isBatterySupported;
    private boolean isDFUServiceSupported;
    private boolean isHRZoneSettingSupported;
    private boolean isHeartRateSupported;
    private boolean isRSSISupported;
    private String manufactureNameString;
    private String modelNumberString;
    private String serialNumberString;
    private String softwareRevisionString;
    private String systemIdString;

    public MioDeviceInformation() {
        this.isHeartRateSupported = false;
        this.isBatterySupported = false;
        this.isRSSISupported = false;
        this.isHRZoneSettingSupported = false;
        this.isDFUServiceSupported = false;
        this.deviceUID = DiffResult.OBJECTS_SAME_STRING;
        this.companyID = DiffResult.OBJECTS_SAME_STRING;
    }

    public String getDeviceUID() {
        return this.deviceUID;
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public String getCompanyID() {
        return this.companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String GetDeviceSystemId_MIO() {
        return this.systemIdString;
    }

    public void SetDeviceSystemId_MIO(String content) {
        this.systemIdString = content;
    }

    public String GetDeviceModelNumber_MIO() {
        return this.modelNumberString;
    }

    public void SetDeviceModelNumber_MIO(String content) {
        this.modelNumberString = content;
    }

    public String GetDeviceSerialNumber_MIO() {
        return this.serialNumberString;
    }

    public void SetDeviceSerialNumber_MIO(String content) {
        this.serialNumberString = content;
    }

    public String GetDeviceUIFirmwareRevision_MIO() {
        return this.firmwareRevisionString;
    }

    public void SetDeviceUIFirmwareRevision_MIO(String content) {
        try {
            Log.e("DeviceUIFirmwareRevision", "DeviceUIFirmwareRevision=" + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.firmwareRevisionString = content;
    }

    public String GetDeviceOHRFirmwareRevision_MIO() {
        return this.softwareRevisionString;
    }

    public void SetDeviceOHRFirmwareRevision_MIO(String content) {
        this.softwareRevisionString = content;
    }

    public String GetDeviceHardwareRevision_MIO() {
        return this.hardwareRevisionString;
    }

    public void SetDeviceHardwareRevision_MIO(String content) {
        this.hardwareRevisionString = content;
    }

    public String GetDeviceManufactureName_MIO() {
        return this.manufactureNameString;
    }

    public void SetDeviceManufactureName_MIO(String content) {
        this.manufactureNameString = content;
    }

    public boolean IsHeartRateSupported_MIO() {
        return this.isHeartRateSupported;
    }

    public void SetHeartRateSupported_MIO() {
        this.isHeartRateSupported = true;
    }

    public boolean IsDFUServiceSupported_MIO() {
        return this.isDFUServiceSupported;
    }

    public void SetDFUServiceSupported_MIO() {
        this.isDFUServiceSupported = true;
    }

    public void SetDFUServiceNoSupported_MIO() {
        this.isDFUServiceSupported = false;
    }

    public boolean IsBatterySupported_MIO() {
        return this.isBatterySupported;
    }

    public void SetBatterySupported_MIO() {
        this.isBatterySupported = true;
    }

    public boolean IsRSSISupported_MIO() {
        return this.isRSSISupported;
    }

    public void SetRSSISupported_MIO() {
        this.isRSSISupported = true;
    }

    public boolean IsHRZoneSettingSupported_MIO() {
        return this.isHRZoneSettingSupported;
    }

    public void SetHRZoneSettingSupported_MIO() {
        this.isHRZoneSettingSupported = true;
    }

    public boolean IsMHRSupported_MIO(String deviceName) {
        if (deviceName != null && (deviceName.endsWith("LINK2") || deviceName.endsWith("LINK") || deviceName.toUpperCase().endsWith("OTBEAT MIO LINK"))) {
            float vissoin = 0.0f;
            try {
                vissoin = Float.valueOf(this.firmwareRevisionString).floatValue();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (vissoin >= 1.13f) {
                return true;
            }
            return false;
        } else if (deviceName == null || !deviceName.contains("VELO")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean IsRTCDateSupported_MIO(String deviceName) {
        if (deviceName == null || (!deviceName.endsWith("FUSE") && !deviceName.contains("ALPHA2"))) {
            return false;
        }
        return true;
    }

    public boolean IsSetNameSupported_MIO(String deviceName) {
        if (deviceName == null) {
            return false;
        }
        if (deviceName.endsWith("FUSE") || deviceName.contains("ALPHA2") || deviceName.endsWith("VELO") || deviceName.toUpperCase().endsWith("OTBEAT MIO LINK")) {
            return true;
        }
        if (!deviceName.endsWith("LINK") && !deviceName.endsWith("LINK2")) {
            return false;
        }
        float vissoin = 0.0f;
        try {
            vissoin = Float.valueOf(this.firmwareRevisionString).floatValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (vissoin >= 1.13f) {
            return true;
        }
        return false;
    }

    public boolean IsThirdBLE(String deviceName) {
        if (deviceName == null || (!deviceName.contains("LINK") && !deviceName.toUpperCase().endsWith("OTBEAT MIO LINK") && !deviceName.endsWith("FUSE") && !deviceName.endsWith("VELO") && !deviceName.contains("ALPHA") && !deviceName.startsWith("MIOUP"))) {
            return true;
        }
        return false;
    }

    public boolean IsSetUseInfoSupported_MIO(String deviceName) {
        if (deviceName == null || (!deviceName.endsWith("FUSE") && !deviceName.contains("ALPHA2"))) {
            return false;
        }
        return true;
    }

    public boolean IsDisplaySupported_MIO(String deviceName) {
        if (deviceName == null || !deviceName.endsWith("FUSE")) {
            return false;
        }
        return true;
    }

    public boolean IsDialyGoalSupported_MIO(String deviceName) {
        if (deviceName == null || (!deviceName.endsWith("FUSE") && !deviceName.contains("LINK2") && !deviceName.contains("ALPHA2"))) {
            return false;
        }
        return true;
    }

    public boolean IsRecordSupported_MIO(String deviceName) {
        if (deviceName == null || (!deviceName.endsWith("FUSE") && !deviceName.endsWith("ALPHA2"))) {
            return false;
        }
        return true;
    }
}
