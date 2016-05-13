package com.mioglobal.android.ble.sdk;

import android.support.v4.media.TransportMediator;
import com.alibaba.fastjson.asm.Opcodes;

public class MioDeviceHRZoneSetting {
    public static final int HR5ZONE_TARGET_ZONE_1 = 0;
    public static final int HR5ZONE_TARGET_ZONE_2 = 1;
    public static final int HR5ZONE_TARGET_ZONE_3 = 2;
    public static final int HR5ZONE_TARGET_ZONE_4 = 3;
    public static final int HR5ZONE_TARGET_ZONE_5 = 4;
    public static final int HRZONE_TYPE_3 = 1;
    public static final int HRZONE_TYPE_5 = 0;
    private int LEDAlertCycle;
    private int MHR;
    private int hr3ZoneHRLowerLimit;
    private int hr3ZoneHRUpperLimit;
    private int hr5ZoneHRLimit0;
    private int hr5ZoneHRLimit1;
    private int hr5ZoneHRLimit2;
    private int hr5ZoneHRLimit3;
    private int hr5ZoneHRLimit4;
    private int hr5ZoneTargetZone;
    private int hrZoneType;
    private boolean isEnableHRAlertAudio;
    private boolean isEnableHRAlertLED;
    private boolean isEnableHRAlertVibro;
    private boolean isEnableHijack;
    private boolean isSupportAntPlusTx;

    public MioDeviceHRZoneSetting() {
        this.isEnableHRAlertLED = true;
    }

    public boolean isHRAlertAudioEnabled_MIO() {
        return this.isEnableHRAlertAudio;
    }

    public void setHRAlertAudioEnable_MIO(boolean isEnableHRAlertAudio) {
        this.isEnableHRAlertAudio = isEnableHRAlertAudio;
    }

    public boolean isHRAlertLEDEnabled_MIO() {
        return this.isEnableHRAlertLED;
    }

    public void setHRAlertLEDEnable_MIO(boolean isEnableHRAlertLED) {
        this.isEnableHRAlertLED = isEnableHRAlertLED;
    }

    public boolean isHRAlertVibroEnabled_MIO() {
        return this.isEnableHRAlertVibro;
    }

    public void setHRAlertVibroEnable_MIO(boolean isEnableHRAlertVibro) {
        this.isEnableHRAlertVibro = isEnableHRAlertVibro;
    }

    public boolean isHijackEnabled_MIO() {
        return this.isEnableHijack;
    }

    public void setHijackEnable_MIO(boolean isEnableHijack) {
        this.isEnableHijack = isEnableHijack;
    }

    public int getLEDAlertCycle() {
        return this.LEDAlertCycle;
    }

    public void setLEDAlertCycle(int lEDAlertCycle) {
        this.LEDAlertCycle = lEDAlertCycle;
    }

    public int getMaxHeartRate() {
        return this.MHR;
    }

    public void setMaxHeartRate(int mHR) {
        this.MHR = mHR;
    }

    public int GetHRZoneType_MIO() {
        return this.hrZoneType;
    }

    public void SetHRZoneType_MIO(int type) {
        this.hrZoneType = type;
    }

    public boolean IsAntPlusTxEnabled_MIO() {
        return this.isSupportAntPlusTx;
    }

    public void SetAntPlusTx_MIO(boolean enabled) {
        this.isSupportAntPlusTx = enabled;
    }

    public int GetHR5ZoneTargetZone_MIO() {
        return this.hr5ZoneTargetZone;
    }

    public void SetHR5ZoneTargetZone_MIO(int target) {
        if (target < 0 || target > HR5ZONE_TARGET_ZONE_5) {
            this.hr5ZoneTargetZone = HR5ZONE_TARGET_ZONE_1;
        } else {
            this.hr5ZoneTargetZone = target;
        }
    }

    public int GetHR5ZoneLimit0_MIO() {
        return this.hr5ZoneHRLimit0;
    }

    public void SetHR5ZoneLimit0_MIO(int limit) {
        if (limit < 30 || limit > 220) {
            this.hr5ZoneHRLimit0 = 100;
        }
        this.hr5ZoneHRLimit0 = limit;
    }

    public int GetHR5ZoneLimit1_MIO() {
        return this.hr5ZoneHRLimit1;
    }

    public void SetHR5ZoneLimit1_MIO(int limit) {
        if (limit < 30 || limit > 220) {
            this.hr5ZoneHRLimit1 = 120;
        }
        this.hr5ZoneHRLimit1 = limit;
    }

    public int GetHR5ZoneLimit2_MIO() {
        return this.hr5ZoneHRLimit2;
    }

    public void SetHR5ZoneLimit2_MIO(int limit) {
        if (limit < 30 || limit > 220) {
            this.hr5ZoneHRLimit2 = 140;
        }
        this.hr5ZoneHRLimit2 = limit;
    }

    public int GetHR5ZoneLimit3_MIO() {
        return this.hr5ZoneHRLimit3;
    }

    public void SetHR5ZoneLimit3_MIO(int limit) {
        if (limit < 30 || limit > 220) {
            this.hr5ZoneHRLimit3 = Opcodes.IF_ICMPNE;
        }
        this.hr5ZoneHRLimit3 = limit;
    }

    public int GetHR5ZoneLimit4_MIO() {
        return this.hr5ZoneHRLimit4;
    }

    public void SetHR5ZoneLimit4_MIO(int limit) {
        if (limit < 30 || limit > 220) {
            this.hr5ZoneHRLimit4 = Opcodes.GETFIELD;
        }
        this.hr5ZoneHRLimit4 = limit;
    }

    public int GetHR3ZoneLowLimit_MIO() {
        return this.hr3ZoneHRLowerLimit;
    }

    public void SetHR3ZoneLowLimit_MIO(int limit) {
        if (limit < 30 || limit > 215) {
            this.hr3ZoneHRLowerLimit = 110;
        }
        this.hr3ZoneHRLowerLimit = limit;
    }

    public int GetHR3ZoneUpperLimit_MIO() {
        return this.hr3ZoneHRUpperLimit;
    }

    public void SetHR3ZoneUpperLimit_MIO(int limit) {
        if (limit < 35 || limit > 220) {
            this.hr3ZoneHRUpperLimit = TransportMediator.KEYCODE_MEDIA_RECORD;
        }
        this.hr3ZoneHRUpperLimit = limit;
    }
}
