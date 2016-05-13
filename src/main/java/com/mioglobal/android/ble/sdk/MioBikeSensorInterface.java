package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.MioUserSetting.VeloMemoryState;

public interface MioBikeSensorInterface {
    public static final byte CADENCE_CHANNEL = (byte) 2;
    public static final byte COMBO_CHANNEL = (byte) 3;
    public static final byte POWER_CHANNEL = (byte) 4;
    public static final byte SPEED_CHANNEL = (byte) 1;

    public enum BikeNum {
        BIKE_TYPE1,
        BIKE_TYPE2,
        BIKE_TYPE3,
        BIKE_TYPE4,
        BIKE_ALL,
        BIKE_NONE
    }

    public enum SensorType {
        SENSOR_TYPE_SPEED,
        SENSOR_TYPE_CADENCE,
        SENSOR_TYPE_COMBO,
        SENSOR_TYPE_POWER,
        SENSOR_TYPE_ALL
    }

    public enum VeloAppType {
        TYPE_MIO,
        TYPE_WAHOO
    }

    boolean ClearAllBikeSensorSetting_MIO();

    boolean ClearBikeSensorSetting_MIO(boolean z, BikeNum bikeNum);

    boolean EnableBikeSensor_MIO(BikeNum bikeNum, SensorType sensorType, boolean z);

    void EnableNotificationBikeSensor_MIO(boolean z);

    boolean GetBikeSensorSetting_MIO(BikeNum bikeNum);

    float GetBikeWheelCircumference_MIO();

    boolean GetVeloWorkMode_MIO();

    void SetBikeSensorCallBack_MIO(MioBikeSensorCallBack mioBikeSensorCallBack);

    boolean SetBikeSensorSetting_MIO(MioBikeSensorSetting mioBikeSensorSetting);

    void SetBikeWheelCircumference_MIO(float f);

    boolean SetVeloWorkMode_MIO(VeloAppType veloAppType, BikeNum bikeNum, VeloMemoryState veloMemoryState);

    boolean StartPairBikeSensor_MIO(int i);

    boolean StartPairBikeSensor_MIO(SensorType sensorType, int i);
}
