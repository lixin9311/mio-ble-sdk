package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.BikeNum;
import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.VeloAppType;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloMemoryState;

public interface MioBikeSensorCallBack {
    void onBikeSensorGetting(MioBikeSensorSetting mioBikeSensorSetting);

    void onBikeSensorScan(int i, int i2, short s, short s2);

    void onBikeSensorSetting(short s);

    void onNotificationBikeCadence(int i);

    void onNotificationBikeSpeed(float f, float f2);

    void onNotificationSensorData(short s, int i, int i2);

    void onVeloWorkModeGet(VeloAppType veloAppType, BikeNum bikeNum, VeloMemoryState veloMemoryState, short s);

    void onVeloWorkModeSet(short s);
}
