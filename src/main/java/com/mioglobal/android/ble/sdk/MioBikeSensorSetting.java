package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.BikeNum;

public class MioBikeSensorSetting {
    public static final short BIKE_1 = (short) 1;
    public static final short BIKE_2 = (short) 2;
    public static final short BIKE_3 = (short) 3;
    public static final short BIKE_4 = (short) 4;
    private int bikeCandenceDeviceNumber;
    private int bikeCandenceManufacturerID;
    private int bikePowerDeviceNumber;
    private int bikePowerManufacturerID;
    private int bikeSCDeviceNumber;
    private int bikeSCManufacturerID;
    private int bikeSpeedDeviceNumber;
    private int bikeSpeedManufacturerID;
    private BikeNum curBikeNum;
    private boolean enableBike;
    private boolean enableBikeCandence;
    private boolean enableBikePower;
    private boolean enableBikeSC;
    private boolean enableBikeSpeed;

    public MioBikeSensorSetting() {
        this.enableBike = false;
        this.enableBikeSpeed = false;
        this.enableBikeCandence = false;
        this.enableBikeSC = false;
        this.enableBikePower = false;
        this.bikeSpeedManufacturerID = 0;
        this.bikeSpeedDeviceNumber = 0;
        this.bikeCandenceManufacturerID = 0;
        this.bikeCandenceDeviceNumber = 0;
        this.bikeSCManufacturerID = 0;
        this.bikeSCDeviceNumber = 0;
        this.bikePowerManufacturerID = 0;
        this.bikePowerDeviceNumber = 0;
        this.curBikeNum = BikeNum.BIKE_TYPE1;
    }

    public MioBikeSensorSetting clone() {
        MioBikeSensorSetting s = new MioBikeSensorSetting();
        s.bikeCandenceDeviceNumber = this.bikeCandenceDeviceNumber;
        s.bikeCandenceManufacturerID = this.bikeCandenceManufacturerID;
        s.bikePowerDeviceNumber = this.bikePowerDeviceNumber;
        s.bikePowerManufacturerID = this.bikePowerManufacturerID;
        s.bikeSCDeviceNumber = this.bikeSCDeviceNumber;
        s.bikeSCManufacturerID = this.bikeSCManufacturerID;
        s.bikeSpeedDeviceNumber = this.bikeSpeedDeviceNumber;
        s.bikeSpeedManufacturerID = this.bikeSpeedManufacturerID;
        s.curBikeNum = this.curBikeNum;
        s.enableBike = this.enableBike;
        s.enableBikeCandence = this.enableBikeCandence;
        s.enableBikePower = this.enableBikePower;
        s.enableBikeSC = this.enableBikeSC;
        s.enableBikeSpeed = this.enableBikeSpeed;
        return s;
    }

    public boolean isEnableBike() {
        return this.enableBike;
    }

    public BikeNum getCurBikeNum() {
        return this.curBikeNum;
    }

    public void setCurBikeNum(BikeNum curBikeNum) {
        this.curBikeNum = curBikeNum;
    }

    public void setEnableBike(BikeNum bikeNum, boolean enableBike) {
        this.curBikeNum = bikeNum;
        this.enableBike = enableBike;
    }

    public boolean isEnableBikeSpeed() {
        return this.enableBikeSpeed;
    }

    public void setEnableBikeChannel(boolean enableBikeSpeed, boolean enableBikeCandence, boolean enableBikeSC, boolean enableBikePower) {
        this.enableBikeSpeed = enableBikeSpeed;
        this.enableBikeCandence = enableBikeCandence;
        this.enableBikeSC = enableBikeSC;
        this.enableBikePower = enableBikePower;
    }

    public void setEnableBikeSpeed(boolean enableBikeSpeed) {
        this.enableBikeSpeed = enableBikeSpeed;
    }

    public boolean isEnableBikeCandence() {
        return this.enableBikeCandence;
    }

    public void setEnableBikeCandence(boolean enableBikeCandence) {
        this.enableBikeCandence = enableBikeCandence;
    }

    public boolean isEnableBikeSC() {
        return this.enableBikeSC;
    }

    public void setEnableBikeSC(boolean enableBikeSC) {
        this.enableBikeSC = enableBikeSC;
    }

    public boolean isEnableBikePower() {
        return this.enableBikePower;
    }

    public void setEnableBikePower(boolean enableBikePower) {
        this.enableBikePower = enableBikePower;
    }

    public int getBikeSpeedManufacturerID() {
        return this.bikeSpeedManufacturerID;
    }

    public int getBikeSpeedDeviceNumber() {
        return this.bikeSpeedDeviceNumber;
    }

    public int getBikeCandenceManufacturerID() {
        return this.bikeCandenceManufacturerID;
    }

    public int getBikeCandenceDeviceNumber() {
        return this.bikeCandenceDeviceNumber;
    }

    public int getBikeSCManufacturerID() {
        return this.bikeSCManufacturerID;
    }

    public int getBikeSCDeviceNumber() {
        return this.bikeSCDeviceNumber;
    }

    public int getBikePowerManufacturerID() {
        return this.bikePowerManufacturerID;
    }

    public int getBikePowerDeviceNumber() {
        return this.bikePowerDeviceNumber;
    }

    public void setBikeSpeed(int bikeSpeedManufacturerID, int bikeSpeedDeviceNumber) {
        this.bikeSpeedManufacturerID = bikeSpeedManufacturerID;
        this.bikeSpeedDeviceNumber = bikeSpeedDeviceNumber;
    }

    public void setBikeCandence(int bikeCandenceManufacturerID, int bikeCandenceDeviceNumber) {
        this.bikeCandenceManufacturerID = bikeCandenceManufacturerID;
        this.bikeCandenceDeviceNumber = bikeCandenceDeviceNumber;
    }

    public void setBikeSC(int bikeSCManufacturerID, int bikeSCDeviceNumber) {
        this.bikeSCManufacturerID = bikeSCManufacturerID;
        this.bikeSCDeviceNumber = bikeSCDeviceNumber;
    }

    public void setBikePower(int bikePowerManufacturerID, int bikePowerDeviceNumber) {
        this.bikePowerManufacturerID = bikePowerManufacturerID;
        this.bikePowerDeviceNumber = bikePowerDeviceNumber;
    }
}
