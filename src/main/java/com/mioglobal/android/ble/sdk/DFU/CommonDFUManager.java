package com.mioglobal.android.ble.sdk.DFU;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;
//import com.breeze.android.util.log.LogHelper;
import com.mioglobal.android.ble.sdk.DFUCallbacks;
import com.mioglobal.android.ble.sdk.MioHelper;
import com.mioglobal.android.ble.sdk.StringUtil;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.builder.DiffResult;

public class CommonDFUManager extends BaseDFUManager {
    private static final String TAG;
    private final int ACTIVATE_FIRMWARE_AND_RESET;
    protected final int BYTES_IN_ONE_PACKET;
    private final int INITIALIZE_DFU;
    private final int NUMBER_OF_PACKETS;
    private final int PACKET_RECEIVED_NOTIFICATION;
    private final int PACKET_RECEIVED_NOTIFICATION_REQUEST;
    private final int RECEIVED_OPCODE;
    private final int RECEIVE_FIRMWARE_IMAGE;
    private final int REPORT_RECEIVED_IMAGE_SIZE;
    private final int RESPONSE;
    private final int START_DFU;
    private final int SYSTEM_RESET;
    private final int VALIDATE_FIRMWARE_IMAGE;
    private boolean isReset;
    private boolean isUpdateSuccess;
    private int mAlignmentBytesCount;
    public BluetoothGattCharacteristic mDFUControlPointCharacteristic;
    public BluetoothGattCharacteristic mDFUPacketCharacteristic;
    private BinInputStream mFileStream;
    private boolean mReceivedIsOverflow;

    static {
        TAG = CommonDFUManager.class.getSimpleName();
    }

    public CommonDFUManager() {
        this.BYTES_IN_ONE_PACKET = 20;
        this.START_DFU = 1;
        this.INITIALIZE_DFU = 2;
        this.RECEIVE_FIRMWARE_IMAGE = 3;
        this.VALIDATE_FIRMWARE_IMAGE = 4;
        this.ACTIVATE_FIRMWARE_AND_RESET = 5;
        this.SYSTEM_RESET = 6;
        this.REPORT_RECEIVED_IMAGE_SIZE = 7;
        this.RESPONSE = 16;
        this.PACKET_RECEIVED_NOTIFICATION_REQUEST = 8;
        this.NUMBER_OF_PACKETS = 1;
        this.PACKET_RECEIVED_NOTIFICATION = 17;
        this.RECEIVED_OPCODE = 16;
        this.mAlignmentBytesCount = 0;
        this.isUpdateSuccess = false;
        this.isReset = false;
        this.mReceivedIsOverflow = false;
        this.isUpdateSuccess = false;
        this.isReset = false;
        this.mReceivedIsOverflow = false;
    }

    public void setGattCharacteristic(BluetoothGattCharacteristic mDFUPacket, BluetoothGattCharacteristic mDFUControlPoint) {
        this.mDFUPacketCharacteristic = mDFUPacket;
        this.mDFUControlPointCharacteristic = mDFUControlPoint;
    }

    public void onconnecttionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onconnecttionStateChange(gatt, status, newState);
        if (newState != 2 && newState == 0) {
            Log.e("ligen", "BluetoothProfile.STATE_DISCONNECTED");
            if (mBluetoothGatt != null) {
                mBluetoothGatt = null;
            }
            if (isBluetoothEnable()) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                if (this.isUpdateSuccess) {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    resetData();
                    if (this.mDFUManagerCallbacks != null) {
                        this.mDFUManagerCallbacks.onUpdateSuccess();
                        return;
                    }
                    return;
                }
                resetData();
            } else {
                if (this.mDFUManagerCallbacks != null) {
                    this.mDFUManagerCallbacks.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED, 6);
                }
                resetData();
            }
        }
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
    }

    public void oncharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.oncharacteristicWrite(gatt, characteristic, status);
        Log.e("oncharacteristicWrite", characteristic.getUuid().toString());
        Log.e("oncharacteristicWrite", StringUtil.toHexCode(characteristic.getValue()));
        Log.e("oncharacteristicWrite", "status:" + status + " isFileSizeWritten:" + this.isFileSizeWritten + " isEnablePacketNotificationWritten:" + this.isEnablePacketNotificationWritten + " isReceiveFirmwareImageWritten:" + this.isReceiveFirmwareImageWritten);
        if (status == 0) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (characteristic.getUuid().equals(MioHelper.DFU_CONTROLPOINT_CHARACTERISTIC_UUID)) {
                if (!this.isFileSizeWritten) {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    writeFileSize();
                    this.isFileSizeWritten = true;
                } else if (!this.isEnablePacketNotificationWritten) {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    receiveFirmwareImage();
                    this.isEnablePacketNotificationWritten = true;
                } else if (!this.isReceiveFirmwareImageWritten) {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    startUploadingFile();
                    this.isReceiveFirmwareImageWritten = true;
                }
            }
            Log.e("oncharacteristicWrite", "oncharacteristicWrite-4");
        } else {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (characteristic.getUuid().equals(MioHelper.DFU_CONTROLPOINT_CHARACTERISTIC_UUID) && this.isReset) {
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                systemReset();
                Log.e("oncharacteristicWrite", "oncharacteristicWrite-3");
            }
        }
        Log.e("oncharacteristicWrite", "oncharacteristicWrite-2");
    }

    public void oncharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.oncharacteristicChanged(gatt, characteristic);
        Log.e("oncharacteristicChanged", "oncharacteristicChanged-1");
        Log.e("oncharacteristicChanged", "uid:" + characteristic.getUuid().toString());
        Log.e("oncharacteristicChanged", "characteristic.value:" + StringUtil.toHexCode(characteristic.getValue()));
        int opCode = characteristic.getValue()[0];
        int request = characteristic.getValue()[1];
        StringBuilder r19 = new StringBuilder("opCode:").append(opCode).append(" request:");
        Log.e("oncharacteristicChanged", r19.append(request).append(" isReset:").append(this.isReset).toString());
        if (opCode == 16 && request == 1) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (characteristic.getValue()[2] == 1) {
                BaseDFUManager.log(TAG, "File length is valid: " + characteristic.getValue()[2]);
                enablePacketNotification();
            } else {
                this.isReset = true;
                systemReset();
            }
            Log.e("oncharacteristicChanged", "oncharacteristicChanged-2");
        } else if (opCode == 17) {
            if (this.isReset) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                mBluetoothGatt.disconnect();
                return;
            }
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            long receivedBytes = ((long) (((((characteristic.getValue()[2] & 255) << 8) | (characteristic.getValue()[1] & 255)) | ((characteristic.getValue()[3] & 255) << 16)) | ((characteristic.getValue()[4] & 255) << 24))) & -1;
            Log.e("m13e","receivedBytes=" + receivedBytes);
            Log.e("m13e","receivedBytes=" + receivedBytes);
            if (this.mReceivedIsOverflow) {
                receivedBytes += 65535;
            }
            if (!this.mReceivedIsOverflow && receivedBytes > 65515) {
                if (this.mFileSize - 65515 > 20) {
                    this.mReceivedIsOverflow = true;
                }
            }
            Log.e("m13e","receivedBytes=" + receivedBytes);
            Log.e("m13e","receivedBytes=" + receivedBytes);
            long temp = receivedBytes + 65535;
            Log.e("m13e","receivedBytes=" + temp);
            Log.e("m13e","receivedBytes=" + temp);
            if (this.mDFUManagerCallbacks != null) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e","receivedBytes: " + receivedBytes + InternalZipConstants.ZIP_FILE_SEPARATOR + this.mFileSize + "=" + ((100 * receivedBytes) / this.mFileSize));
                Log.e("m13e","receivedBytes: " + receivedBytes + InternalZipConstants.ZIP_FILE_SEPARATOR + this.mFileSize + "=" + ((100 * receivedBytes) / this.mFileSize));
                this.mDFUManagerCallbacks.onFileTranfering((int) ((100 * receivedBytes) / this.mFileSize));
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            }
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (this.isLastPacket || this.mStopSendingPacket) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            } else {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                sendPacket();
            }
            Log.e("oncharacteristicChanged", "oncharacteristicChanged-3");
        } else if (opCode == 16 && request == 3) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (characteristic.getValue()[2] == 1) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                if (this.mDFUManagerCallbacks != null) {
                    this.mDFUManagerCallbacks.onFileTransferCompleted();
                    closeFile();
                }
                validateFirmware();
            } else {
                int errorStatus = characteristic.getValue()[2];
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_UPDATE_FILE_TRANSFER_FAILED, 11);
                this.isReset = true;
                systemReset();
            }
            Log.e("oncharacteristicChanged", "oncharacteristicChanged-4");
        } else if (opCode == 16 && request == 4) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (characteristic.getValue()[2] == 1) {
                this.isUpdateSuccess = true;
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                if (this.mDFUManagerCallbacks != null) {
                    this.mDFUManagerCallbacks.onFileTransferValidation();
                }
                activateAndReset();
                this.isNotificationEnable = false;
            } else {
                byte b = characteristic.getValue()[2];
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_UPDATE_FILE_VALIDATION_FAILED, 12);
                this.isReset = true;
                systemReset();
            }
            Log.e("oncharacteristicChanged", "oncharacteristicChanged-5");
        } else if (opCode == 16 && request == 7) {
            if (characteristic.getValue()[2] == 1) {
                long receivedImageSize = ((long) (((((characteristic.getValue()[4] & 255) << 8) | (characteristic.getValue()[3] & 255)) | ((characteristic.getValue()[5] & 255) << 16)) | ((characteristic.getValue()[6] & 255) << 24))) & -1;
                Log.e("m13e","receivedImageSize=" + receivedImageSize);
                Log.e("m13e","receivedImageSize=" + receivedImageSize);
                if (receivedImageSize == 0) {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    startDFU();
                } else {
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                    systemReset();
                }
            } else {
                this.isReset = true;
                systemReset();
            }
            Log.e("oncharacteristicChanged", "oncharacteristicChanged-6");
        }
        Log.e("oncharacteristicChanged", "oncharacteristicChanged-7");
    }

    public void ondescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.ondescriptorWrite(gatt, descriptor, status);
        Log.e("ondescriptorWrite", "ondescriptorWrite-1");
        Log.e("ondescriptorWrite", "status:" + status + " isNotificationEnable:" + this.isNotificationEnable);
        if (status == 0) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (this.isNotificationEnable) {
                startDFU();
            }
        } else {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_WRITE_DESCRIPTOR, 13);
            systemReset();
        }
        Log.e("ondescriptorWrite", "ondescriptorWrite-2");
    }

    protected void systemReset() {
        super.systemReset();
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        if (mBluetoothGatt != null && this.mDFUControlPointCharacteristic != null) {
            this.mDFUControlPointCharacteristic.setValue(new byte[]{(byte) 6});
            BaseDFUManager.log(TAG, "mBluetoothGatt.writeCharacteristic(mDFUControlPointCharacteristic):" + mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic));
        }
    }

    public boolean startUpdate(String updateFilePath) {
        Log.e("startUpdate", "startUpdate--1");
        if (super.startUpdate(updateFilePath)) {
            Log.e("startUpdate", "startUpdate--2");
            return openFileAndUpdate(updateFilePath);
        }
        Log.e("startUpdate", "startUpdate--3");
        return false;
    }

    public void resetStatus() {
        super.resetData();
        this.isNotificationEnable = false;
        this.isFileSizeWritten = false;
        this.isEnablePacketNotificationWritten = false;
        this.isReceiveFirmwareImageWritten = false;
        this.mStopSendingPacket = false;
        this.isLastPacket = false;
        this.mFileSize = 0;
        this.mTotalPackets = 0;
        this.mPacketNumber = 0;
        this.isValidateSuccess = false;
        this.isReset = false;
        this.mReceivedIsOverflow = false;
        this.isUpdateSuccess = false;
    }

    private void receiveFirmwareImage() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        BaseDFUManager.log(TAG, "sending Receive Firmware Image message");
        this.mDFUControlPointCharacteristic.setValue(3, 17, 0);
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
    }

    private void startDFU() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        BaseDFUManager.log(TAG, "startDFU");
        this.mDFUControlPointCharacteristic.setValue(1, 17, 0);
        BaseDFUManager.log(TAG, "writing start DFU value");
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
        this.isFileSizeWritten = false;
    }

    private void writeFileSize() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        if (mBluetoothGatt == null || this.mDFUPacketCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            return;
        }
        this.mDFUPacketCharacteristic.setWriteType(1);
        this.mAlignmentBytesCount = (int) (4 - (this.mFileSize % 4));
        Log.e("m13e","mFileSize=" + this.mFileSize + " mAlignmentBytesCount:" + this.mAlignmentBytesCount);
        Log.e("m13e","mFileSize=" + this.mFileSize + " mAlignmentBytesCount:" + this.mAlignmentBytesCount);
        this.mDFUPacketCharacteristic.setValue(((int) this.mFileSize) + this.mAlignmentBytesCount, 20, 0);
        Log.e("m13e","iflag=" + mBluetoothGatt.writeCharacteristic(this.mDFUPacketCharacteristic));
        Log.e("m13e","iflag=" + mBluetoothGatt.writeCharacteristic(this.mDFUPacketCharacteristic));
    }

    private void activateAndReset() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        this.mDFUControlPointCharacteristic.setValue(5, 17, 0);
        BaseDFUManager.log(TAG, "writing activate and reset value");
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
    }

    private void startUploadingFile() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        sendPacket();
        if (this.mDFUManagerCallbacks != null) {
            this.mDFUManagerCallbacks.onFileTransferStarted();
        }
    }

    public boolean openFileAndUpdate(String updateFilePath) {
        Log.e("openFileAndUpdate", "openFileAndUpdate--1");
        try {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            this.mPacketNumber = 0;
            this.mFileStream = new BinInputStream(updateFilePath, 20);
            this.mFileSize = this.mFileStream.available();
            Log.e("openFileAndUpdate", "mFileSize=" + this.mFileSize);
            this.mTotalPackets = (long) getNumberOfPackets();
            Log.e("openFileAndUpdate", "mTotalPackets=" + this.mTotalPackets);
            enableNotification();
            Log.e("openFileAndUpdate", "openFileAndUpdate--2");
            return true;
        } catch (FileNotFoundException e) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_NOT_EXITS, 14);
            return false;
        } catch (IOException e2) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_OPEN, 15);
            return false;
        }
    }

    public void closeFile() {
        if (this.mFileStream != null) {
            try {
                this.mFileStream.close();
                this.mFileStream = null;
            } catch (IOException e) {
                BaseDFUManager.logE(TAG, "Error on closing file " + e.toString());
                this.mDFUManagerCallbacks.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_CLOSE, 17);
            }
        }
    }

    private int getNumberOfPackets() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        return ((int) (this.mFileSize / 20)) + 1;
    }

    private byte[] getNextPacket() {
        try {
            byte[] buffer = new byte[20];
            if (this.mFileStream == null) {
                this.mFileStream = new BinInputStream(this.mUpdateFilePath, 20);
                this.mFileSize = this.mFileStream.available();
            }
            this.mFileStream.readPacket(buffer);
            return buffer;
        } catch (Exception e) {
            BaseDFUManager.logE(TAG, DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_READ);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_READ, 18);
            return null;
        }
    }

    private int getBytesInLastPacket() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        return (int) (this.mFileSize % 20);
    }

    protected void sendPacket() {
        super.sendPacket();
        this.mPacketNumber++;
        if (this.mPacketNumber == this.mTotalPackets) {
            BaseDFUManager.log(TAG, "This is last packet");
            BaseDFUManager.log(TAG, "sendPacket: " + this.mPacketNumber);
            this.isLastPacket = true;
            byte[] buffer = getNextPacket();
            BaseDFUManager.log(TAG, "buffer: " + buffer.length);
            int lastPacketCount = getBytesInLastPacket();
            BaseDFUManager.log(TAG, "lastPacketCount: " + lastPacketCount);
            byte[] data = new byte[(this.mAlignmentBytesCount + lastPacketCount)];
            if (buffer != null) {
                for (int i = 0; i < getBytesInLastPacket(); i++) {
                    data[i] = buffer[i];
                }
            }
            for (int index = 0; index < this.mAlignmentBytesCount; index++) {
                data[lastPacketCount + index] = (byte) -1;
            }
            BaseDFUManager.log(TAG, "data: " + data.length);
            this.mDFUPacketCharacteristic.setWriteType(1);
            this.mDFUPacketCharacteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(this.mDFUPacketCharacteristic);
            BaseDFUManager.log(TAG, "sent last packet");
            return;
        }
        this.mDFUPacketCharacteristic.setWriteType(1);
        this.mDFUPacketCharacteristic.setValue(getNextPacket());
        mBluetoothGatt.writeCharacteristic(this.mDFUPacketCharacteristic);
    }

    private void validateFirmware() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        this.mDFUControlPointCharacteristic.setValue(4, 17, 0);
        BaseDFUManager.log(TAG, "writing validate Firmware value");
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
    }

    public void disableNotification() {
        if (this.isNotificationEnable) {
            BaseDFUManager.log(TAG, "Disable Notification");
            mBluetoothGatt.setCharacteristicNotification(this.mDFUControlPointCharacteristic, false);
            BluetoothGattDescriptor descriptor = this.mDFUControlPointCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            this.isNotificationEnable = false;
        }
    }

    public void enableNotification() {
        BaseDFUManager.log(TAG, "Enable Notification");
        Log.e("enableNotification", "enableNotification--1");
        try {
            mBluetoothGatt.setCharacteristicNotification(this.mDFUControlPointCharacteristic, true);
            BluetoothGattDescriptor descriptor = this.mDFUControlPointCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            this.isNotificationEnable = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("enableNotification", "enableNotification--2");
    }

    private void enablePacketNotification() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        BaseDFUManager.log(TAG, "Enable Packet Notification");
        BaseDFUManager.log(TAG, "NUMBER_OF_PACKETS=1");
        byte[] value = new byte[3];
        value[0] = (byte) 8;
        value[1] = (byte) 1;
        this.mDFUControlPointCharacteristic.setValue(value);
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
    }

    private void reportReceivedImageSize() {
        if (!isBluetoothEnable()) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED, 6);
        } else if (mBluetoothGatt == null || this.mDFUControlPointCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        } else {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            this.mDFUControlPointCharacteristic.setValue(7, 17, 0);
            mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
        }
    }
}
