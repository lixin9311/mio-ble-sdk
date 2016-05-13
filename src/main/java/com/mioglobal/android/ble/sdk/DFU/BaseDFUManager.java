package com.mioglobal.android.ble.sdk.DFU;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;
//import com.breeze.android.util.log.LogHelper;
import com.mioglobal.android.ble.sdk.DFUCallbacks;
import java.util.UUID;
import org.apache.commons.lang3.builder.DiffResult;

public class BaseDFUManager {
    protected static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID;
    private static final String TAG;
    protected static BluetoothGatt mBluetoothGatt;
    protected boolean isEnablePacketNotificationWritten;
    protected boolean isFileSizeWritten;
    protected boolean isLastPacket;
    protected boolean isNotificationEnable;
    protected boolean isReceiveFirmwareImageWritten;
    protected boolean isValidateSuccess;
    protected BluetoothAdapter mBluetoothAdapter;
    protected long mCurrent4kb;
    protected long mCurrentsize;
    protected DFUCallbacks mDFUManagerCallbacks;
    protected long mFileSize;
    protected long mPacketNumber;
    public int mSelectDevicesType;
    protected boolean mStopSendingPacket;
    protected long mTotalPackets;
    protected String mUpdateFilePath;
    protected long mWaiting01;

    static {
        TAG = BaseDFUManager.class.getSimpleName();
        CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }

    public BaseDFUManager() {
        this.isNotificationEnable = false;
        this.isFileSizeWritten = false;
        this.isEnablePacketNotificationWritten = false;
        this.isReceiveFirmwareImageWritten = false;
        this.mStopSendingPacket = false;
        this.isLastPacket = false;
        this.mFileSize = 0;
        this.mTotalPackets = 0;
        this.mPacketNumber = 0;
        this.mWaiting01 = 0;
        this.mCurrentsize = 0;
        this.mCurrent4kb = 0;
        this.isValidateSuccess = false;
        this.mSelectDevicesType = 0;
    }

    public void onconnecttionStateChange(BluetoothGatt gatt, int status, int newState) {
    }

    public void onservicesDiscovered(BluetoothGatt gatt, int status) {
    }

    public void oncharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    public void oncharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    }

    public void ondescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    }

    public boolean setInitParm(BluetoothAdapter bleAdapter, BluetoothGatt bleGatt, DFUCallbacks callbacks, String filePath, String deviceType) {
        this.mBluetoothAdapter = bleAdapter;
        mBluetoothGatt = bleGatt;
        this.mDFUManagerCallbacks = callbacks;
        this.mUpdateFilePath = filePath;
        return true;
    }

    public boolean isBluetoothEnable() {
        if (this.mBluetoothAdapter == null) {
            return false;
        }
        return this.mBluetoothAdapter.isEnabled();
    }

    public boolean startUpdate(String updateFilePath) {
        this.mUpdateFilePath = updateFilePath;
        if (isBluetoothEnable()) {
            return true;
        }
        return false;
    }

    private void startUploadingFile() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        log(TAG, "Preparing to send file");
        sendPacket();
        if (this.mDFUManagerCallbacks != null) {
            this.mDFUManagerCallbacks.onFileTransferStarted();
        }
    }

    protected void sendPacket() {
    }

    protected void closeFile() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
    }

    public void close() {
        closeFile();
        if (!this.isValidateSuccess) {
            stopSendingPacket();
        }
    }

    protected void systemReset() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
    }

    public long getFileSize() {
        return this.mFileSize;
    }

    public void stopSendingPacket() {
        this.mStopSendingPacket = true;
    }

    public void resumeSendingPacket() {
        this.mStopSendingPacket = false;
        sendPacket();
    }

    public static void log(String tag, String text) {
        Log.d(tag, text);
    }

    public static void logE(String tag, String text) {
        Log.e(tag, text);
    }

    public void resetData() {
        this.isValidateSuccess = false;
        this.isNotificationEnable = false;
        this.isFileSizeWritten = false;
        this.isEnablePacketNotificationWritten = false;
        this.isReceiveFirmwareImageWritten = false;
        this.mStopSendingPacket = false;
        this.isLastPacket = false;
        this.mFileSize = 0;
        this.mTotalPackets = 0;
        this.mPacketNumber = 0;
    }

    public void stopUpdate() {
        stopSendingPacket();
    }

    public void onError(String errorMsg, int errorCode) {
        if (this.mDFUManagerCallbacks != null) {
            Log.e(TAG, "errorMsg:" + errorMsg + " errorCode:" + errorCode);
            this.mDFUManagerCallbacks.onError(errorMsg, errorCode);
        }
    }
}
