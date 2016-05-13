package com.mioglobal.android.ble.sdk.DFU;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
//import com.breeze.android.util.log.LogHelper;
import com.mioglobal.android.ble.sdk.DFUCallbacks;
import com.mioglobal.android.ble.sdk.MioDeviceManager;
import com.mioglobal.android.ble.sdk.MioHelper;
import com.mioglobal.android.ble.sdk.StringUtil;
//import com.oeday.libs.base.DBModel;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.FileNotFoundException;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.builder.DiffResult;

public class Alpha2DFUManager extends BaseDFUManager {
    private static byte[] MSG_START_DFU_MODE_ALPHA2;
    private static final String TAG;
    private int LINE_LENGTH;
    private long historyMaxRecivedByte;
    private boolean is01responsereceived;
    private boolean isnotify51Flag;
    private boolean isnotify52Flag;
    private boolean isnotify53Flag;
    private boolean isenablePacketNotifWritten;
    public BluetoothGattCharacteristic mAlpha2DFUSendPackageCharacteristic;
    private BinInputStream mAlpha2FileStream;
    public BluetoothGattCharacteristic mDFUControlPointCharacteristic;
    public BluetoothGattCharacteristic mDFUPacketCharacteristic;
    private Handler mDelaySendHandler;
    private Runnable mSendDataCallback;

    /* renamed from: com.mioglobal.android.ble.sdk.DFU.Alpha2DFUManager.1 */
    class C02651 implements Runnable {
        C02651() {
        }

        public void run() {
            Alpha2DFUManager.this.sendPacket();
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.DFU.Alpha2DFUManager.2 */
    class C02662 implements Runnable {
        C02662() {
        }

        public void run() {
            Alpha2DFUManager.this.enablePacketNotif53();
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.DFU.Alpha2DFUManager.3 */
    class C02673 implements Runnable {
        C02673() {
        }

        public void run() {
            Alpha2DFUManager.this.enablePacketNotif52();
        }
    }

    static {
        TAG = Alpha2DFUManager.class.getSimpleName();
        byte[] bArr = new byte[7];
        bArr[0] = (byte) 1;
        bArr[1] = (byte) 83;
        bArr[2] = (byte) 69;
        bArr[3] = (byte) 54;
        bArr[4] = (byte) 49;
        bArr[5] = (byte) 56;
        MSG_START_DFU_MODE_ALPHA2 = bArr;
    }

    public Alpha2DFUManager() {
        this.historyMaxRecivedByte = 0;
        this.LINE_LENGTH = 18;
        this.isnotify51Flag = false;
        this.isnotify52Flag = false;
        this.isnotify53Flag = false;
        this.is01responsereceived = true;
        this.mDelaySendHandler = null;
        this.mSendDataCallback = new C02651();
    }

    public void setGattCharacteristic(BluetoothGattCharacteristic mDFUPacket, BluetoothGattCharacteristic mDFUControlPoint, BluetoothGattCharacteristic mAlpha2DFUSendPackage) {
        this.mDFUPacketCharacteristic = mDFUPacket;
        this.mDFUControlPointCharacteristic = mDFUControlPoint;
        this.mAlpha2DFUSendPackageCharacteristic = mAlpha2DFUSendPackage;
        if (this.mDelaySendHandler == null) {
            this.mDelaySendHandler = new Handler();
        }
    }

    public void oncharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.oncharacteristicChanged(gatt, characteristic);
        Log.e("oncharacteristicChanged", "oncharacteristicChanged1");
        int opCode = characteristic.getValue()[0];
        int request = characteristic.getValue()[1];
        int firstByte = characteristic.getValue()[1] & 255;
        int secondByte = characteristic.getValue()[2] & 255;
        Log.e("oncharacteristicChanged", StringUtil.toHexCode(characteristic.getValue()));
        Log.e("oncharacteristicChanged", "opCode:" + opCode + " request:" + request + " firstByte:" + firstByte + " secondByte:" + secondByte);
        Log.v("LeeeeL", "oncharacteristicChanged characteristic.getUuid():" + characteristic.getUuid());
        long receivePackagenumber = (long) ((secondByte << 8) | firstByte);
        if ((characteristic.getValue()[0] == 4 && secondByte == 0) || receivePackagenumber == 0) {
            this.mPacketNumber = 0;
            MioDeviceManager.GetMioDeviceManager_MIO().setAlpha2TranSlowState();
            this.mDFUManagerCallbacks.onFileTransferStarted();
            sendPacket();
        } else if ((characteristic.getValue()[0] == 2 && firstByte == 255 && secondByte == 255) || ((characteristic.getValue()[0] == 3 && firstByte == 230 && secondByte == 24) || receivePackagenumber == this.mTotalPackets)) {
            if (this.mDFUManagerCallbacks != null) {
                this.mDFUManagerCallbacks.onFileTransferCompleted();
                this.mDFUManagerCallbacks.onUpdateSuccess();
                Log.e("onUpdateSuccess", "onUpdateSuccess");
            }
            closeFile();
        } else {
            long receivedBytes = receivePackagenumber * ((long) this.LINE_LENGTH);
            if (this.historyMaxRecivedByte < receivedBytes) {
                this.historyMaxRecivedByte = receivedBytes;
            }
            if (this.mDFUManagerCallbacks != null) {
                Log.e("oncharacteristicChanged", "receivedBytes: " + receivedBytes + InternalZipConstants.ZIP_FILE_SEPARATOR + this.mFileSize + "=" + ((100 * receivedBytes) / this.mFileSize));
                if (this.historyMaxRecivedByte == 0) {
                    Log.e("setAlpha2TranSlowState", "setAlpha2TranSlowState1");
                    MioDeviceManager.GetMioDeviceManager_MIO().setAlpha2TranSlowState();
                }
                this.mDFUManagerCallbacks.onFileTranfering((int) ((this.historyMaxRecivedByte * 100) / this.mFileSize));
            }
            Log.e("oncharacteristicChanged", "firstByte | (secondByte << 8):" + ((secondByte << 8) | firstByte) + "mPacketNumber " + this.mPacketNumber);
            this.mPacketNumber = (long) ((secondByte << 8) | firstByte);
            if (this.mPacketNumber >= this.mTotalPackets) {
                this.mPacketNumber = this.mTotalPackets - 1;
            }
            if (characteristic.getValue()[0] == 3) {
                this.is01responsereceived = true;
                this.mPacketNumber = 0;
                Log.e("Ldd", "oncharacteristicChanged characteristic.getValue()[0]==3 mPacketNumber:" + this.mPacketNumber);
            }
            if (this.mAlpha2FileStream == null) {
                try {
                    this.mAlpha2FileStream = new BinInputStream(this.mUpdateFilePath, this.LINE_LENGTH);
                    this.mFileSize = this.mAlpha2FileStream.available();
                    this.mTotalPackets = (long) getNumberOfPackets();
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_READ, 18);
                    return;
                }
            }
            this.mAlpha2FileStream.seek(this.mPacketNumber);
            sendPacket();
            this.mWaiting01 = 0;
        }
        Log.e("oncharacteristicChanged", "oncharacteristicChanged2");
    }

    public void oncharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.oncharacteristicWrite(gatt, characteristic, status);
        String retValue = StringUtil.toHexCode(characteristic.getValue());
        if (status == 0) {
            if (characteristic.getUuid().equals(MioHelper.ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID) && !this.isFileSizeWritten) {
                byte[] raw = characteristic.getValue();
                if (raw[0] == 1) {
                    int length = raw.length - 7;
                    if (raw.length <= 0 || length <= 0) {
                        String str = "n/a";
                    } else {
                        StringBuilder sb = new StringBuilder(length);
                        for (int i = 0; i < length; i++) {
                            byte byteChar = raw[i + 7];
                            sb.append(String.format("%c", new Object[]{Byte.valueOf(byteChar)}));
                        }
                    }
                }
                writeFileSize();
                this.isFileSizeWritten = true;
            } else if (characteristic.getUuid().equals(MioHelper.ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID)) {
                if (this.historyMaxRecivedByte < this.mPacketNumber * ((long) this.LINE_LENGTH)) {
                    this.historyMaxRecivedByte = this.mPacketNumber * ((long) this.LINE_LENGTH);
                }
                if (this.mDFUManagerCallbacks != null) {
                    if (this.historyMaxRecivedByte == 0) {
                        Log.e("setAlpha2TranSlowState", "setAlpha2TranSlowState2");
                        MioDeviceManager.GetMioDeviceManager_MIO().setAlpha2TranSlowState();
                    }
                    this.mDFUManagerCallbacks.onFileTranfering((int) ((this.historyMaxRecivedByte * 100) / this.mFileSize));
                }
                long rec = (long) (((characteristic.getValue()[19] & 255) << 8) | (characteristic.getValue()[18] & 255));
                if (this.is01responsereceived) {
                    int tempPacketNum = 20;
                    if (VERSION.SDK_INT < 21) {
                        tempPacketNum = 100;
                    }
                    if (MioDeviceManager.GetMioDeviceManager_MIO().isSlowTran()) {
                        tempPacketNum = 100;
                    }
                    if (this.mWaiting01 == 0) {
                        this.mCurrentsize = (this.mPacketNumber - 1) * 18;
                        if ((this.mPacketNumber <= 0 || this.mCurrentsize / PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM > this.mCurrent4kb) && this.mPacketNumber <= 6371) {
                            Log.e("LLLLLLLLLL", " mPacketNumber:" + this.mPacketNumber);
                            this.mWaiting01 = 1;
                        } else if (this.mCurrentsize % ((long) (tempPacketNum * 18)) != 0 || this.mPacketNumber > 6371) {
                            sendPacket();
                        } else {
                            Log.e("speed", "tempPacketNum=" + tempPacketNum + "  isSlowTran=" + MioDeviceManager.GetMioDeviceManager_MIO().isSlowTran());
                            Log.e("LLLLLLLLLL", " mPacketNumber:" + this.mPacketNumber);
                            sendPacket();
                        }
                        this.mCurrent4kb = this.mCurrentsize / PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
                    }
                }
            }
        } else if (characteristic.getUuid().equals(MioHelper.ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID)) {
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
            systemReset();
        }
    }

    private void delaySendPacket(int timeout) {
        if (this.mDelaySendHandler == null) {
            this.mDelaySendHandler = new Handler();
        }
        this.mDelaySendHandler.removeCallbacks(this.mSendDataCallback);
        this.mDelaySendHandler.postDelayed(this.mSendDataCallback, (long) timeout);
    }

    public void onconnecttionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onconnecttionStateChange(gatt, status, newState);
        Log.e("onconnStateChange", "onconnecttionStateChange1");
        if (newState == 2) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        } else if (newState == 0) {
            Log.e("onconnStateChange", "STATE_DISCONNECTED");
            closeFile();
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (this.isValidateSuccess) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                resetData();
                return;
            }
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        }
        Log.e("onconnStateChange", "onconnecttionStateChange2");
    }

    public void ondescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.ondescriptorWrite(gatt, descriptor, status);
        Log.e("ondescriptorWrite", "ondescriptorWrite1");
        Log.e("ondescriptorWrite", "status:" + status + " isControlPointNotificationEnable:" + this.isNotificationEnable);
        if (status == 0) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (this.isnotify51Flag && !this.isnotify52Flag) {
                delayenablePacketNotif52();
            } else if (this.isnotify51Flag && this.isnotify52Flag && !this.isnotify53Flag) {
                delayenablePacketNotif53();
            } else if (this.isnotify51Flag && this.isnotify52Flag && this.isnotify53Flag) {
                startDFU();
            }
        } else {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_WRITE_DESCRIPTOR, 13);
            systemReset();
        }
        Log.e("ondescriptorWrite", "ondescriptorWrite2");
    }

    protected void sendPacket() {
        super.sendPacket();
        if (!isBluetoothEnable()) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED, 6);
        } else if (this.mPacketNumber <= this.mTotalPackets && !this.mStopSendingPacket) {
            byte[] sendpack = getNextPacket();
            byte lowbyte = (byte) ((int) (this.mPacketNumber & 255));
            byte heightbyte = (byte) ((int) (this.mPacketNumber >>> 8));
            if (this.mPacketNumber == this.mTotalPackets) {
                int bytesInLastPacket = (int) (this.mFileSize % ((long) this.LINE_LENGTH));
                byte[] data = new byte[20];
                for (int i = 0; i < this.LINE_LENGTH; i++) {
                    if (i < bytesInLastPacket) {
                        data[i] = sendpack[i];
                    } else {
                        data[i] = (byte) -1;
                    }
                }
                data[this.LINE_LENGTH] = lowbyte;
                data[this.LINE_LENGTH + 1] = heightbyte;
                this.mAlpha2DFUSendPackageCharacteristic.setWriteType(1);
                this.mAlpha2DFUSendPackageCharacteristic.setValue(data);
                this.mPacketNumber++;
                if (!mBluetoothGatt.writeCharacteristic(this.mAlpha2DFUSendPackageCharacteristic)) {
                    Log.e("sendPacket", "sendPacket23");
                    return;
                }
                return;
            }
            sendpack[18] = lowbyte;
            sendpack[19] = heightbyte;
            this.mAlpha2DFUSendPackageCharacteristic.setWriteType(1);
            this.mAlpha2DFUSendPackageCharacteristic.setValue(sendpack);
            this.mPacketNumber++;
            if (!mBluetoothGatt.writeCharacteristic(this.mAlpha2DFUSendPackageCharacteristic)) {
                Log.e("sendPacket", "sendPacket24");
            }
        }
    }

    public boolean startUpdate(String updateFilePath) {
        if (super.startUpdate(updateFilePath)) {
            Log.e("startUpdate", "startUpdate1");
            try {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                this.mPacketNumber = 0;
                this.mAlpha2FileStream = new BinInputStream(updateFilePath, this.LINE_LENGTH);
                this.mFileSize = this.mAlpha2FileStream.available();
                this.mTotalPackets = (long) getNumberOfPackets();
                enablePacketNotif51();
                Log.e("startUpdate", "startUpdate2");
                return true;
            } catch (Exception e) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_OPEN, 15);
                return false;
            }
        }
        Log.e("startUpdate", "startUpdate2");
        return false;
    }

    private int getNumberOfPackets() {
        Log.e("getNumberOfPackets", "getNumberOfPackets1");
        int numOfPackets = (int) (this.mFileSize / ((long) this.LINE_LENGTH));
        if (this.mFileSize % ((long) this.LINE_LENGTH) > 0) {
            numOfPackets++;
        }
        Log.e("getNumberOfPackets", "getNumberOfPackets2");
        return numOfPackets;
    }

    private byte[] getNextPacket() {
        try {
            byte[] buffer = new byte[20];
            if (this.mAlpha2FileStream == null) {
                this.mAlpha2FileStream = new BinInputStream(this.mUpdateFilePath, this.LINE_LENGTH);
                this.mFileSize = this.mAlpha2FileStream.available();
                this.mTotalPackets = (long) getNumberOfPackets();
            }
            this.mAlpha2FileStream.readPacket(buffer);
            return buffer;
        } catch (Exception e) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_READ, 18);
            return null;
        }
    }

    protected void closeFile() {
        super.closeFile();
        Log.e("closeFile", "closeFile1");
        if (this.mAlpha2FileStream != null) {
            try {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                this.mAlpha2FileStream.close();
                this.mAlpha2FileStream = null;
            } catch (Exception e) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_CLOSE, 17);
            }
        }
        Log.e("closeFile", "closeFile2");
    }

    public void resetStatus() {
        super.resetData();
        this.historyMaxRecivedByte = 0;
        this.isNotificationEnable = false;
        this.isenablePacketNotifWritten = false;
    }

    private void startDFU() {
        Log.e("startDFU", "startDFU1");
        if (!isBluetoothEnable()) {
            Log.e("startDFU", "isBluetoothEnable");
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED, 6);
        } else if (mBluetoothGatt == null || this.mDFUControlPointCharacteristic == null) {
            Log.e("startDFU", "mBluetoothGatt==null");
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        } else {
            this.historyMaxRecivedByte = 0;
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            Log.e("startDFU", "startDFU=" + this.mSelectDevicesType);
            this.mDFUControlPointCharacteristic.setValue(MSG_START_DFU_MODE_ALPHA2);
            mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
            this.isFileSizeWritten = false;
            Log.e("startDFU", "startDFU2");
        }
    }

    private void writeFileSize() {
        Log.e("writeFileSize", "writeFileSize1");
        if (mBluetoothGatt == null || this.mDFUControlPointCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            return;
        }
        if (this.mAlpha2FileStream == null) {
            try {
                this.mAlpha2FileStream = new BinInputStream(this.mUpdateFilePath, this.LINE_LENGTH);
                this.mFileSize = this.mAlpha2FileStream.available();
                this.mTotalPackets = (long) getNumberOfPackets();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_READ, 18);
                return;
            }
        }
        long checksum = this.mAlpha2FileStream.getCheckSum();
        Log.e("checksum", "checksum=" + checksum);
        byte[] ALPHA2_SEND_FILESIZE = new byte[11];
        ALPHA2_SEND_FILESIZE[0] = (byte) 4;
        ALPHA2_SEND_FILESIZE[3] = (byte) ((int) (this.mFileSize & 255));
        ALPHA2_SEND_FILESIZE[4] = (byte) ((int) ((this.mFileSize >> 8) & 255));
        ALPHA2_SEND_FILESIZE[5] = (byte) ((int) ((this.mFileSize >> 16) & 255));
        ALPHA2_SEND_FILESIZE[6] = (byte) ((int) ((this.mFileSize >> 24) & 255));
        ALPHA2_SEND_FILESIZE[7] = (byte) ((int) (checksum & 255));
        ALPHA2_SEND_FILESIZE[8] = (byte) ((int) ((checksum >> 8) & 255));
        ALPHA2_SEND_FILESIZE[9] = (byte) ((int) ((checksum >> 16) & 255));
        ALPHA2_SEND_FILESIZE[10] = (byte) ((int) ((checksum >> 24) & 255));
        this.mDFUControlPointCharacteristic.setValue(ALPHA2_SEND_FILESIZE);
        mBluetoothGatt.writeCharacteristic(this.mDFUControlPointCharacteristic);
        Log.e("writeFileSize", "writeFileSize3");
    }

    public void enablePacketNotif53() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("enablePacketNotif53", "enablePacketNotif531");
        if (!isBluetoothEnable()) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            onError(DFUCallbacks.MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED, 6);
        } else if (mBluetoothGatt == null || this.mDFUControlPointCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        } else {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            this.isNotificationEnable = true;
            this.isnotify53Flag = true;
            mBluetoothGatt.setCharacteristicNotification(this.mAlpha2DFUSendPackageCharacteristic, true);
            BluetoothGattDescriptor descriptor = this.mAlpha2DFUSendPackageCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            Log.e("enablePacketNotif53", "enablePacketNotif532");
        }
    }

    private void enablePacketNotif52() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("enablePacketNotif52", "enablePacketNotif521");
        if (mBluetoothGatt == null || this.mDFUPacketCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            return;
        }
        this.isenablePacketNotifWritten = false;
        this.isnotify52Flag = true;
        this.isnotify53Flag = false;
        mBluetoothGatt.setCharacteristicNotification(this.mDFUPacketCharacteristic, true);
        BluetoothGattDescriptor descriptor = this.mDFUPacketCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        if (mBluetoothGatt.writeDescriptor(descriptor)) {
            Log.e("enablePacketNotif52", "enablePacketNotif523");
        }
        Log.e("enablePacketNotif52", "enablePacketNotif522");
    }

    private void enablePacketNotif51() {
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        Log.e("enablePacketNotif51", "enablePacketNotif511");
        if (mBluetoothGatt == null || this.mAlpha2DFUSendPackageCharacteristic == null) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            return;
        }
        this.isnotify51Flag = true;
        this.isnotify52Flag = false;
        this.isnotify53Flag = false;
        mBluetoothGatt.setCharacteristicNotification(this.mAlpha2DFUSendPackageCharacteristic, true);
        BluetoothGattDescriptor descriptor = this.mAlpha2DFUSendPackageCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
        Log.e("enablePacketNotif51", "enablePacketNotif512");
    }

    private void delayenablePacketNotif53() {
        Log.e("enablePacketNotif53", "enablePacketNotif532");
        this.mDelaySendHandler.postDelayed(new C02662(), 1500);
        Log.e("enablePacketNotif53", "enablePacketNotif532");
    }

    private void delayenablePacketNotif52() {
        Log.e("delayenPacketNotif52", "delayenablePacketNotif521");
        this.mDelaySendHandler.postDelayed(new C02673(), 1500);
        Log.e("delayenPacketNotif52", "delayenablePacketNotif522");
    }
}
