package com.mioglobal.android.ble.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.support.v4.media.TransportMediator;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.asm.Opcodes;
import com.alibaba.fastjson.parser.JSONScanner;
//import com.breeze.android.util.log.LogHelper;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.places.Place;
import com.mioglobal.android.ble.sdk.DFU.Alpha2DFUManager;
import com.mioglobal.android.ble.sdk.DFU.CommonDFUManager;
import com.mioglobal.android.ble.sdk.DFU.MIOCommon;
import com.mioglobal.android.ble.sdk.DFU.Utils;
import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.BikeNum;
import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.SensorType;
import com.mioglobal.android.ble.sdk.MioBikeSensorInterface.VeloAppType;
import com.mioglobal.android.ble.sdk.MioDeviceInterface.CMD_TYPE;
import com.mioglobal.android.ble.sdk.MioDeviceInterface.RUN_CMD;
import com.mioglobal.android.ble.sdk.MioUserSetting.DATEFORMAT;
import com.mioglobal.android.ble.sdk.MioUserSetting.DelOPType;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseTimerSyncData;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDeviceStatus;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.GPSDATAFLAG;
import com.mioglobal.android.ble.sdk.MioUserSetting.GPSData;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalData;
import com.mioglobal.android.ble.sdk.MioUserSetting.MIOMisc1Data;
import com.mioglobal.android.ble.sdk.MioUserSetting.OneMinuteSleepTracking;
import com.mioglobal.android.ble.sdk.MioUserSetting.RTCSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.RType;
import com.mioglobal.android.ble.sdk.MioUserSetting.StridCaliData;
import com.mioglobal.android.ble.sdk.MioUserSetting.TIMEFORMAT;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserInfo;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreenData;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloDeviceStatus;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloMemoryState;
//import com.oeday.miogo.GPSTrackerActivity;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.time.DateUtils;

public class MioDeviceConnection implements MioBikeSensorInterface, MioDeviceInterface {
    static final int AIRPLANE_MODE_ENABLE = 13;
    static final int DEL_RECORD = 1;
    static final int DEL_SLEEP_RECORD = 19;
    static final int DEL_VELO_RECORD = 17;
    static final int ENABLE_STREAM_MODE = 11;
    static final int GET_ALPHA2_RECORD_N = 14;
    static final int GET_RECORD_ADL_D_F = 3;
    static final int GET_RECORD_ADL_D_N = 4;
    static final int GET_RECORD_ADL_T = 2;
    static final int GET_RECORD_WO_N = 6;
    static final int GET_RECORD_WO_T = 5;
    static final int GET_SLEEP_RECORD_CURHOUR_N = 20;
    static final int GET_SLEEP_RECORD_N = 18;
    static final int GET_SLEEP_RECORD_T = 21;
    static final int GET_VELO_RECORD_N = 16;
    static final int RESET_ADL_TODAY_DATA = 12;
    static final int SEND_RUN_CMD = 15;
    BikeNum activeBikeNum;
    private List<OneMinuteSleepTracking> allSleepRecords;
    private Alpha2DFUManager alpha2DFUManager;
    private boolean autoRetry;
    private boolean autoRetrySearchSensor;
    private MioDeviceBatteryCallback batteryStateCallback;
    private int batteryValue;
    private boolean bleCmdState;
    private boolean bleIsConnected;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCallback bluetoothGattCallback;
    private int cadenceZeroCount;
    private BluetoothGattCharacteristic characteristicSpeedAndCadence;
    private CMD_TYPE cmdType;
    private CommonDFUManager commonDFUManager;
    private MioDeviceConnectionCallback connectionStateCallback;
    int curSessionID;
    int dataLength;
    private boolean delRecordFlag;
    private String deviceAddress;
    private DFUCallbacks deviceDFUCallBack;
    private String deviceName;
    private String deviceUID;
    int downLoadType;
    private boolean enableNotifyBikeSensorData;
    private boolean getAllRecordFlag;
    private BikeNum getBikeNum;
    int getCurSessionDataLen;
    private boolean getEnable;
    private boolean hasSensorData;
    private MioDeviceHRZoneSetting hrZoneSetting;
    private MioDeviceHRZoneSettingCallback hrZoneSettingCallback;
    private MioDeviceHRMCallback hrmStateCallback;
    private boolean inGetFlag;
    private boolean isCOMBO;
    private boolean isConnectBle;
    private boolean isConnected;
    private boolean isCoonecting;
    private boolean isDebug;
    public boolean isDeviceUpdate;
    private boolean isDisconnectedByUser;
    private boolean isEndSync;
    public boolean isEnterDFUMode;
    private boolean isExceptionDisconnection;
    private boolean isHRNotificationEnabled;
    private boolean isHRZoneGet;
    private boolean isNotificationInit;
    private boolean isReconnect;
    private boolean isSleepFullMemory;
    private boolean isSync;
    int logDataPos;
    private Runnable mAutoConnTimeoutThread;
    private Runnable mBluetoothSettingTimeOut;
    private Runnable mCadenceThread;
    private BluetoothGattCharacteristic mCharacteristicAlpha2DFUSendPackage;
    private BluetoothGattCharacteristic mCharacteristicBatteryLevel;
    private BluetoothGattCharacteristic mCharacteristicDFUControlPoint;
    private BluetoothGattCharacteristic mCharacteristicDFUPacket;
    private BluetoothGattCharacteristic mCharacteristicFirmwareRevisionString;
    private BluetoothGattCharacteristic mCharacteristicHardwareRevisionString;
    private BluetoothGattCharacteristic mCharacteristicHeartRate;
    private BluetoothGattCharacteristic mCharacteristicManufactureNameString;
    private BluetoothGattCharacteristic mCharacteristicMioRecordMsg;
    private BluetoothGattCharacteristic mCharacteristicMioSenserData;
    private BluetoothGattCharacteristic mCharacteristicMioSportMsg;
    private BluetoothGattCharacteristic mCharacteristicMioSportMsgResp;
    private BluetoothGattCharacteristic mCharacteristicModelNumberString;
    private BluetoothGattCharacteristic mCharacteristicSerialNumberString;
    private BluetoothGattCharacteristic mCharacteristicSoftwareRevisionString;
    private BluetoothGattCharacteristic mCharacteristicSystemIdString;
    private Runnable mConnectionTimeoutThread;
    private Handler mConnectionTimerHandler;
    private Context mContext;
    private Runnable mDownWorkoutRecordTimeOut;
    private Runnable mHRThread;
    private Runnable mSearchSensorTimeoutThread;
    private Runnable mSpeendThread;
    private final int minLimitTimeOut;
    private MioBikeSensorCallBack mioBikeSensorCallBack;
    private MioBikeSensorSetting mioBikeSensorSetting;
    private MioDeviceCallBack mioDeviceCallBack;
    private MioDeviceInformation mioDeviceInformation;
    private boolean needGoOnDownLoadRecord;
    private MioDeviceHRZoneSetting newMIOHRZoneSettings;
    private Thread notifyThread;
    int numberOfRecord;
    DelOPType opType;
    private int prevCrankMeasTime;
    private int prevCrankRevCount;
    private int prevMeasTime;
    private int prevRevCount;
    private int rType;
    byte[] recordBuf;
    int recordDataType;
    int recordIndex;
    int recordSize;
    RType recordType;
    boolean resumeDown;
    boolean resumeDownLoadTask;
    private boolean retryConnect;
    private int retryCount;
    private boolean retryEnableHRNotification;
    private boolean retryEnableHRZoneMsgRespNotification;
    private MioDeviceRSSICallback rssiStateCallback;
    private boolean searchSensorEndFlag;
    private int searchSensorFlag;
    private SensorType sensorType;
    int sessionDataSize;
    int sessionTotal;
    private int speedZeroCount;
    private int startRevCount;
    private BikeNum tempActiveBikeNum;
    private VeloAppType tempAppType;
    int tempCurSessionID;
    private RUN_CMD tempRunCmd;
    int tempgetCurSessionDataLen;
    int tempsessionDataSize;
    private boolean thirdBLE;
    private long timeout;
    private MIODevicesType updateDeviceType;
    private String updateFilePath;
    public boolean userStopNotify;
    VeloAppType veloAppType;
    private LeScanCallback veloLeScanCallback;
    VeloMemoryState veloMemoryState;
    private float wheelCircumference;
    private int workoutRecordIndex;

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.10 */
    class AnonymousClass10 implements Runnable {
        private final /* synthetic */ boolean val$resetBluetooth;

        /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.10.1 */
        class C02681 implements LeScanCallback {
            C02681() {
            }

            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String address = device.getAddress();
                String name = device.getName();
                if (name == null) {
                    name = new MioBleAdHelper(scanRecord).getDeviceName();
                }
                if (name != null && name.contains("VELO")) {
                    Log.e("velo-retry-connecting", "name=" + name);
                    if (MioDeviceConnection.this.deviceUID.compareToIgnoreCase(MioHelper.paserVeloSerial(scanRecord)) == 0) {
                        System.out.println("velo-retry-connecting");
                        MioSportMsgParserUtil.writeLogtoFile("velo-retry-connecting", "Address", "oldAddress=" + MioDeviceConnection.this.deviceAddress + "     newAddress= " + address);
                        System.out.println("oldAddress=" + MioDeviceConnection.this.deviceAddress + "     newAddress= " + address);
                        if (!address.equals(MioDeviceConnection.this.deviceAddress)) {
                            MioDeviceManager.GetMioDeviceManager_MIO().changeDeviceAdress(MioDeviceConnection.this, address, MioDeviceConnection.this.deviceAddress);
                        }
                        MioDeviceConnection.this.deviceAddress = address;
                        MioDeviceConnection.this.bluetoothAdapter.stopLeScan(MioDeviceConnection.this.veloLeScanCallback);
                    }
                }
            }
        }

        AnonymousClass10(boolean z) {
            this.val$resetBluetooth = z;
        }

        public void run() {
            MioDeviceConnection.this.isConnected = false;
            MioDeviceConnection.this.notifyThread = null;
            MioDeviceConnection.this.isHRNotificationEnabled = false;
            MioDeviceConnection.this.isNotificationInit = false;
            MioDeviceConnection.this.hasSensorData = false;
            try {
                if (MioDeviceConnection.this.bluetoothGatt != null) {
                    MioDeviceConnection.this.bluetoothGatt.close();
                }
            } catch (Exception e1) {
                Log.e("doBluetoothReset", "doBluetoothReset2");
                e1.printStackTrace();
            }
            if (this.val$resetBluetooth) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MioDeviceConnection.this.bluetoothGatt = null;
            boolean ret = MioDeviceConnection.this.bluetoothAdapter.isEnabled();
            System.out.println("-----------bluetoothAdapter.isEnabled()=" + ret);
            if (ret && !MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.isReconnect && MioDeviceConnection.this.bluetoothAdapter != null && ret) {
                MioDeviceConnection.this.veloLeScanCallback = new C02681();
                if (MioDeviceConnection.this.bluetoothAdapter != null && !MioDeviceConnection.this.bluetoothAdapter.isDiscovering()) {
                    if (MioDeviceConnection.this.isReconnect) {
                        MioDeviceConnection.this.bluetoothAdapter.startLeScan(MioDeviceConnection.this.veloLeScanCallback);
                        try {
                            Thread.sleep(2000);
                            if (MioDeviceConnection.this.bluetoothAdapter != null && MioDeviceConnection.this.bluetoothAdapter.isDiscovering()) {
                                MioDeviceConnection.this.bluetoothAdapter.stopLeScan(MioDeviceConnection.this.veloLeScanCallback);
                            }
                            if (!MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.isReconnect) {
                                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                                MioDeviceConnection.this.Connect_MIO(MioDeviceConnection.this.mContext);
                                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mAutoConnTimeoutThread, MioDeviceConnection.this.timeout);
                                return;
                            }
                            return;
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    }
                    MioDeviceConnection.this.Disconnect_MIO();
                }
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.18 */
    class AnonymousClass18 implements Runnable {
        private final /* synthetic */ boolean val$enableHRNotification;

        AnonymousClass18(boolean z) {
            this.val$enableHRNotification = z;
        }

        public void run() {
            try {
                Thread.sleep(500);
                MioDeviceConnection.this.enableHRNotification(this.val$enableHRNotification);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.1 */
    class C02701 implements Runnable {
        C02701() {
        }

        public void run() {
            if (MioDeviceConnection.this.hrmStateCallback != null) {
                MioDeviceConnection.this.hrmStateCallback.OnDeviceHRMChanged_MIO(0);
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.26 */
    class AnonymousClass26 extends Thread {
        private final /* synthetic */ int val$sessionID;

        AnonymousClass26(int i) {
            this.val$sessionID = i;
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
            delCommand[0] = (byte) 2;
            delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_SESSION_GET;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) (this.val$sessionID & 255);
            delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = (byte) ((this.val$sessionID & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
            MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SESSION_GET, delCommand);
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.28 */
    class AnonymousClass28 extends Thread {
        private final /* synthetic */ DelOPType val$opType;
        private final /* synthetic */ RType val$type;

        AnonymousClass28(RType rType, DelOPType delOPType) {
            this.val$type = rType;
            this.val$opType = delOPType;
        }

        public void run() {
            byte[] delCommand;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte type1 = (byte) 1;
            if (this.val$type == RType.TYPE_ADL_DAILY) {
                type1 = (byte) 2;
            } else if (this.val$type == RType.TYPE_ACTIVITY) {
                type1 = (byte) 5;
            }
            byte type2 = (byte) 0;
            if (this.val$opType == DelOPType.DELETE_ALL_RECORD) {
                type2 = (byte) 1;
            }
            if (MioDeviceConnection.this.deviceName.endsWith("ALPHA2")) {
                delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
                delCommand[0] = (byte) 2;
                delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK2_MEM_RECORD_DELETE;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = type1;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = type2;
                MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RECORD_DELETE, delCommand);
            }
            delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
            delCommand[0] = (byte) 2;
            delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_DEL;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = type1;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = type2;
            MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RECORD_DELETE, delCommand);
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.29 */
    class AnonymousClass29 implements Runnable {
        private final /* synthetic */ DelOPType val$opType;

        AnonymousClass29(DelOPType delOPType) {
            this.val$opType = delOPType;
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte type2 = (byte) 0;
            if (this.val$opType == DelOPType.DELETE_ALL_RECORD) {
                type2 = (byte) 1;
            }
            byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
            delCommand[0] = (byte) 2;
            delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.MSG_VELO_MEM_RECORD_DEL;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 1;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = type2;
            MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_VELO_MEM_RECORD_DEL, delCommand);
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.2 */
    class C02712 implements Runnable {
        C02712() {
        }

        public void run() {
            if (MioDeviceConnection.this.mioBikeSensorCallBack != null) {
                MioDeviceConnection.this.mioBikeSensorCallBack.onNotificationBikeSpeed(0.0f, 0.0f);
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.31 */
    class AnonymousClass31 extends Thread {
        private final /* synthetic */ DelOPType val$opType;
        private final /* synthetic */ RType val$type;

        AnonymousClass31(RType rType, DelOPType delOPType) {
            this.val$type = rType;
            this.val$opType = delOPType;
        }

        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte type1 = (byte) 1;
            if (this.val$type == RType.TYPE_ACTIVITY) {
                type1 = (byte) 5;
            }
            byte type2 = (byte) 0;
            if (this.val$opType == DelOPType.DELETE_ALL_RECORD) {
                type2 = (byte) 1;
            }
            byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
            delCommand[0] = (byte) 2;
            delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_DEL;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = type1;
            delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = type2;
            MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SLEEP_RECORD_DELETE, delCommand);
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.3 */
    class C02723 implements Runnable {
        C02723() {
        }

        public void run() {
            if (MioDeviceConnection.this.mioBikeSensorCallBack != null) {
                MioDeviceConnection.this.mioBikeSensorCallBack.onNotificationBikeCadence(0);
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.4 */
    class C02734 implements Runnable {
        C02734() {
        }

        public void run() {
            if (!MioDeviceConnection.this.isConnected) {
                if (MioDeviceConnection.this.bluetoothGatt != null) {
                    try {
                        MioDeviceConnection.this.bluetoothGatt.close();
                    } catch (Exception e) {
                        Log.e("mAutoConTimeoutThread", "mAutoConnTimeoutThread2");
                        e.printStackTrace();
                    }
                }
                if (MioDeviceConnection.this.autoRetry) {
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.autoRetry = false;
                    MioDeviceConnection.this.isConnected = false;
                    MioDeviceConnection.this.notifyThread = null;
                    MioDeviceConnection.this.isHRNotificationEnabled = false;
                    if (!MioDeviceConnection.this.deviceName.contains("VELO") || MioDeviceConnection.this.deviceUID == null || MioDeviceConnection.this.deviceUID.length() <= 0) {
                        Log.e("DoReConnect", "DoReConnect2");
                        if (MioDeviceConnection.this.connectionStateCallback != null && !MioDeviceConnection.this.isDisconnectedByUser) {
                            Log.e("mAutoConTimeoutThread", "OnDeviceDisconnected_MIO");
                            MioDeviceConnection.this.isCoonecting = false;
                            MioDeviceConnection.this.retryCount = 0;
                            MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                            return;
                        }
                        return;
                    }
                    MioDeviceConnection.this.handleVeloReconnect();
                } else if (MioDeviceConnection.this.connectionStateCallback != null && !MioDeviceConnection.this.isDisconnectedByUser) {
                    Log.e("mAutoConTimeoutThread", "OnDeviceDisconnected_MIO");
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.retryCount = 0;
                    MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                }
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.5 */
    class C02745 implements Runnable {
        C02745() {
        }

        public void run() {
            Log.e("mAutoConTimeoutThread", "mAutoConnTimeoutThread23");
            if (!MioDeviceConnection.this.isConnected) {
                if (MioDeviceConnection.this.bluetoothGatt != null) {
                    Log.e("mAutoConnTimeoutThread", "mAutoConnTimeoutThread1");
                    try {
                        MioDeviceConnection.this.bluetoothGatt.close();
                    } catch (Exception e) {
                        Log.e("mAutoConnTimeoutThread", "mAutoConnTimeoutThread2");
                        e.printStackTrace();
                    }
                }
                MioDeviceConnection.this.bluetoothGatt = null;
                if (MioDeviceConnection.this.connectionStateCallback != null && !MioDeviceConnection.this.isDisconnectedByUser) {
                    MioDeviceConnection.this.isDisconnectedByUser = true;
                    Log.e("mConnTimeoutThread", "OnDeviceDisconnected_MIO");
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.retryCount = 0;
                    MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                }
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.6 */
    class C02786 extends BluetoothGattCallback {

        /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.6.1 */
        class C02751 implements Runnable {
            private final /* synthetic */ BluetoothGattCharacteristic val$hrCharacteristic;

            C02751(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                this.val$hrCharacteristic = bluetoothGattCharacteristic;
            }

            public void run() {
                int hrValue;
                if (MioHelper.isHeartRateInUINT16(this.val$hrCharacteristic.getValue()[0])) {
                    hrValue = this.val$hrCharacteristic.getIntValue(MioDeviceConnection.GET_SLEEP_RECORD_N, MioDeviceConnection.DEL_RECORD).intValue();
                } else {
                    hrValue = this.val$hrCharacteristic.getIntValue(MioDeviceConnection.DEL_VELO_RECORD, MioDeviceConnection.DEL_RECORD).intValue();
                }
                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mHRThread, 5000);
                if (MioDeviceConnection.this.hrmStateCallback != null) {
                    MioDeviceConnection.this.hrmStateCallback.OnDeviceHRMChanged_MIO(hrValue);
                }
            }
        }

        /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.6.2 */
        class C02762 implements Runnable {
            private final /* synthetic */ byte[] val$raw;

            C02762(byte[] bArr) {
                this.val$raw = bArr;
            }

            public void run() {
                MioDeviceConnection.this.parserBikeSensorData(this.val$raw);
            }
        }

        /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.6.3 */
        class C02773 implements Runnable {
            C02773() {
            }

            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!MioDeviceConnection.this.GetCurHourSleepRecord_MIO() && MioDeviceConnection.this.mioDeviceCallBack != null) {
                    MioDeviceConnection.this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
                }
            }
        }

        C02786() {
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            Log.e("onCharChange", "uuid=" + uuid.toString());
            byte[] raw = characteristic.getValue();
            if (MioHelper.DFU_CONTROLPOINT_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.DFU_PACKET_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.DFU_STATUS_REPORT_CHARACTERISTIC_UUID.equals(uuid)) {
                if (MioDeviceConnection.this.commonDFUManager != null) {
                    MioDeviceConnection.this.commonDFUManager.oncharacteristicChanged(gatt, characteristic);
                }
            } else if (MioHelper.ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.ALPHA2_DFU_SENDPACKET_CHARACTERISTIC_UUID.equals(uuid)) {
                if (MioDeviceConnection.this.alpha2DFUManager != null) {
                    MioDeviceConnection.this.alpha2DFUManager.oncharacteristicChanged(gatt, characteristic);
                }
            } else if (MioHelper.UUID_CHARACTERISTIC_HEART_RATE.equals(uuid)) {
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mHRThread);
                if (!MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.isConnected) {
                    new Thread(new C02751(characteristic)).start();
                }
            } else if (MioHelper.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(uuid)) {
                MioDeviceConnection.this.batteryValue = raw[0];
                if (MioDeviceConnection.this.batteryStateCallback != null && MioDeviceConnection.this.isConnected) {
                    MioDeviceConnection.this.batteryStateCallback.OnDeviceBatteryChanged_MIO(MioDeviceConnection.this.batteryValue);
                }
            } else if (MioHelper.UUID_CHARACTERISTIC_MIO_SPORT_MSG_RESP.equals(uuid)) {
                Log.e("onCharacteristicChanged", "uuid=" + uuid.toString());
                MioDeviceConnection.this.bleCmdState = false;
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mBluetoothSettingTimeOut);
                MioSportMsgParserUtil.writeLogtoFile("cmd_resp", DiffResult.OBJECTS_SAME_STRING, MioSportMsgParserUtil.printRaw("cmd_resp", raw));
                if (raw != null && raw.length > MioDeviceConnection.DEL_RECORD) {
                    if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.FUSE_FACTORY_DEFAULT) {
                        if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                            MioDeviceConnection.this.mioDeviceCallBack.onSendCMD(CMD_TYPE.CMD_TYPE_FACTORY_DEFAULT, raw[MioDeviceConnection.GET_RECORD_ADL_D_F]);
                        }
                    } else if (raw[0] == MioDeviceConnection.GET_RECORD_ADL_T && raw[MioDeviceConnection.DEL_RECORD] == MIOCommon.MSG_ID_RESPONSE && raw[MioDeviceConnection.GET_RECORD_ADL_T] == MIOCommon.MSG_ID_ALPHA2_DFU_CMD) {
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_D_F] == MIOCommon.MSG_RESPONSE_CODE_NO_ERROR) {
                            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                            MioDeviceConnection.this.deviceDFUCallBack.onEnterDFUModeSuccess();
                        } else if (MioDeviceConnection.this.deviceDFUCallBack != null) {
                            MioDeviceConnection.this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
                        }
                    } else if (raw[0] == MioDeviceConnection.GET_RECORD_ADL_T && raw[MioDeviceConnection.DEL_RECORD] == MIOCommon.MSG_ID_RESPONSE && raw[MioDeviceConnection.GET_RECORD_ADL_T] == MIOCommon.MSG_ID_LINK_DFU_CMD) {
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_D_F] == MIOCommon.MSG_RESPONSE_CODE_NO_ERROR) {
                            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                            MioDeviceConnection.this.deviceDFUCallBack.onEnterDFUModeSuccess();
                        } else if (raw[MioDeviceConnection.GET_RECORD_ADL_D_F] == MIOCommon.MSG_RESPONSE_CODE_UNIT_IN_WRONG_STATE) {
                            if (MioDeviceConnection.this.deviceDFUCallBack != null) {
                                MioDeviceConnection.this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
                            }
                        } else if (MioDeviceConnection.this.deviceDFUCallBack != null) {
                            MioDeviceConnection.this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
                        }
                    } else if (raw[MioDeviceConnection.DEL_RECORD] == MioHelper.MSG_FOUND_SONSER || raw[MioDeviceConnection.DEL_RECORD] == MioHelper.MSG_CHANNCEL_STATUS || raw[MioDeviceConnection.DEL_RECORD] == MioHelper.MSG_INFO_ASYNC) {
                        MioDeviceConnection.this.doBikeSensorMsgResponseResult(raw);
                    } else if (raw[MioDeviceConnection.DEL_RECORD] != MioHelper.MSG_ID_RESPONSE) {
                        MioDeviceConnection.this.doMsgResponseResult(raw);
                    } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_SENSOR_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_SENSOR_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_STATUS_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_STATUS_SET) {
                        MioDeviceConnection.this.doBikeSensorMsgResponseResult(raw);
                    } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_CUST_CMD) {
                        MioSportMsgParserUtil.printRaw("ALPHA2 Battery", raw);
                        if (raw.length >= MioDeviceConnection.GET_RECORD_WO_N && raw[MioDeviceConnection.GET_RECORD_ADL_D_F] == 0 && raw[MioDeviceConnection.GET_RECORD_ADL_D_N] == MioDeviceConnection.DEL_RECORD) {
                            MioDeviceConnection.this.batteryValue = raw[MioDeviceConnection.GET_RECORD_WO_T];
                            if (MioDeviceConnection.this.batteryStateCallback != null) {
                                MioDeviceConnection.this.batteryStateCallback.OnDeviceBatteryChanged_MIO(MioDeviceConnection.this.batteryValue);
                            }
                        }
                    } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_USER_SETTING_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_USER_SETTING_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_DEVICE_NAME_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_DEVICE_NAME_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTC_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTC_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTCTIME_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTCTIME_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RUN_CMD || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_SENSOR_DATA || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_EXER_SETTINGS_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_EXER_SETTINGS_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_USER_SCREEN_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_USER_SCREEN_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_STRIDE_CALI_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_STRIDE_CALI_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_CUST_DEVICE_OPTION_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_CUST_DEVICE_OPTION_SET) {
                        MioSportMsgParserUtil.printRaw("CommonMsgResponse", raw);
                        if (MioDeviceConnection.this.isEndSync && (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTC_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RTCTIME_GET)) {
                            MioDeviceConnection.this.isEndSync = false;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioDeviceConnection.this.mioDeviceCallBack.DidEndSYNC(raw[MioDeviceConnection.GET_RECORD_ADL_D_F]);
                                return;
                            }
                            return;
                        }
                        MioDeviceConnection.this.isEndSync = false;
                        MioSportMsgParserUtil.doCommonMsgResponseResult(raw, MioDeviceConnection.this.mioDeviceCallBack);
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_RUN_CMD) {
                            MioDeviceConnection.this.doCMDMsgRsp(raw);
                        }
                    } else if (MioHelper.LINK2_MEM_RECORD_GET == raw[MioDeviceConnection.GET_RECORD_ADL_T] || MioHelper.LINK2_MEM_NEXT_PACKET_GET == raw[MioDeviceConnection.GET_RECORD_ADL_T] || MioHelper.LINK2_MEM_RECORD_DELETE == raw[MioDeviceConnection.GET_RECORD_ADL_T]) {
                        MioSportMsgParserUtil.printRaw("Alpha2MsgResponse", raw);
                        MioDeviceConnection.this.parserRecord(raw);
                    } else if (MioHelper.MSG_VELO_DEVICE_STATUS_GET == raw[MioDeviceConnection.GET_RECORD_ADL_T] || MioHelper.MSG_VELO_RECORD_GET == raw[MioDeviceConnection.GET_RECORD_ADL_T] || MioHelper.MSG_VELO_MEM_SESSION_GET == raw[MioDeviceConnection.GET_RECORD_ADL_T] || MioHelper.MSG_VELO_MEM_RECORD_DEL == raw[MioDeviceConnection.GET_RECORD_ADL_T]) {
                        MioSportMsgParserUtil.printRaw("VeloMsgResponse", raw);
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_DEVICE_STATUS_GET) {
                            MioDeviceConnection.this.doVeloDeviceStatusMsgRsp(raw);
                        } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_MEM_RECORD_DEL) {
                            byte msgCode = raw[MioDeviceConnection.GET_RECORD_ADL_D_F];
                            Log.e("rType14", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioDeviceConnection.this.mioDeviceCallBack.onDeleteVeloRecord(MioDeviceConnection.this.recordType, MioDeviceConnection.this.opType, msgCode);
                            }
                        } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_RECORD_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.MSG_VELO_MEM_SESSION_GET) {
                            MioDeviceConnection.this.parserVeloRecord(raw);
                        }
                    } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DISP_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DISP_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DAILY_GOAL_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DAILY_GOAL_SET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DEVICE_STATUS_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_SESSION_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_DEL || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DISP_SET2 || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MISC1_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MISC1_SET) {
                        MioSportMsgParserUtil.printRaw("FuseMsgResponse", raw);
                        MioSportMsgParserUtil.doFuseMsgResponseResult(raw, MioDeviceConnection.this.mioDeviceCallBack, MioDeviceConnection.this.deviceName);
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_DEVICE_STATUS_GET) {
                            MioDeviceConnection.this.doDeviceStatusMsgRsp(raw);
                        } else if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_DEL) {
                            byte msgCode = raw[MioDeviceConnection.GET_RECORD_ADL_D_F];
                            Log.e("rType15", "rType=" + MioDeviceConnection.this.rType);
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                if (MioDeviceConnection.this.rType == MioDeviceConnection.DEL_SLEEP_RECORD) {
                                    MioDeviceConnection.this.mioDeviceCallBack.DidDeleteAllActivityRecord_MIO(MioDeviceConnection.this.recordType, MioDeviceConnection.this.opType, msgCode);
                                } else {
                                    MioDeviceConnection.this.mioDeviceCallBack.onDeleteRecord(MioDeviceConnection.this.recordType, MioDeviceConnection.this.opType, msgCode);
                                }
                            }
                            MioDeviceConnection.this.rType = 0;
                        }
                        if (raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_GET || raw[MioDeviceConnection.GET_RECORD_ADL_T] == MioHelper.LINK_MEM_SESSION_GET) {
                            MioDeviceConnection.this.parserRecord(raw);
                        }
                    } else {
                        MioDeviceConnection.this.doMsgResponseResult(raw);
                    }
                }
            } else if (MioHelper.CHARACTERISTIC_CHAR_CSC_MEASUREMENT.equals(uuid)) {
                if (MioDeviceConnection.this.isConnected && !MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.enableNotifyBikeSensorData) {
                    new Thread(new C02762(raw)).start();
                }
            } else if (MioHelper.UUID_CHARACTERISTIC_MIO_SENSER.equals(uuid)) {
                Log.e("SENSERData", MioSportMsgParserUtil.printRaw("RecordData", raw));
                ExerciseTimerSyncData timerData = MioSportMsgParserUtil.parserTimerSyncDataMsgResponseResult(raw);
                if (raw[0] == MioHelper.TIMER_SYNC_DATA) {
                    if (timerData != null) {
                        timerData.rawLog = MioSportMsgParserUtil.printRaw("RecordData", raw);
                        if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                            MioDeviceConnection.this.mioDeviceCallBack.onSyscTimerSenserData_MIO(0, timerData);
                        }
                    } else if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                        MioDeviceConnection.this.mioDeviceCallBack.onSyscTimerSenserData_MIO(-1, null);
                    }
                } else if (raw[0] != MioHelper.STEP_DATA) {
                } else {
                    if (timerData == null) {
                        if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                            MioDeviceConnection.this.mioDeviceCallBack.OnSyscDeviceSensorData_MIO(-1, null);
                        }
                    } else if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                        MioDeviceConnection.this.mioDeviceCallBack.OnSyscDeviceSensorData_MIO(0, timerData);
                    }
                }
            } else if (MioHelper.UUID_CHARACTERISTIC_MIO_RECORD.equals(uuid)) {
                int gotoNum;
                int i;
                MioDeviceConnection mioDeviceConnection;
                Log.e("onCharacteristicChanged", "uuid=" + uuid.toString());
                Log.e("UUID_CHAR_MIO_RECORD", MioSportMsgParserUtil.printRaw("RecordData", raw));
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 50000);
                Log.e("needGoOnDownLoadRecord", "needGoOnDownLoadRecord=" + MioDeviceConnection.this.needGoOnDownLoadRecord);
                if (MioDeviceConnection.this.needGoOnDownLoadRecord) {
                    if (MioDeviceConnection.this.tempgetCurSessionDataLen == 0) {
                        MioDeviceConnection.this.tempCurSessionID = (raw[MioDeviceConnection.GET_RECORD_WO_T] & 255) + ((raw[MioDeviceConnection.GET_RECORD_WO_N] & 255) << 8);
                    }
                    if (MioDeviceConnection.this.curSessionID != MioDeviceConnection.this.tempCurSessionID) {
                        gotoNum = 0;
                        if (MioDeviceConnection.this.tempgetCurSessionDataLen == 0) {
                            MioDeviceConnection.this.tempsessionDataSize = (raw[7] & 255) + ((raw[8] & 255) << 8);
                            gotoNum = 9;
                        }
                        for (i = gotoNum; i < raw.length && MioDeviceConnection.this.tempgetCurSessionDataLen < MioDeviceConnection.this.tempsessionDataSize; i += MioDeviceConnection.DEL_RECORD) {
                            mioDeviceConnection = MioDeviceConnection.this;
                            mioDeviceConnection.tempgetCurSessionDataLen += MioDeviceConnection.DEL_RECORD;
                        }
                        Log.e("temp", "tempsessionDataSize=" + MioDeviceConnection.this.tempsessionDataSize + " tempgetCurSessionDataLen= " + MioDeviceConnection.this.tempgetCurSessionDataLen);
                        if (MioDeviceConnection.this.tempgetCurSessionDataLen >= MioDeviceConnection.this.tempsessionDataSize) {
                            MioDeviceConnection.this.doResumeDownLoadSession();
                            MioDeviceConnection.this.needGoOnDownLoadRecord = false;
                            return;
                        }
                        return;
                    }
                    MioDeviceConnection.this.needGoOnDownLoadRecord = false;
                }
                Log.e("recordSize", "recordSize=" + MioDeviceConnection.this.recordSize);
                if (MioDeviceConnection.this.recordBuf == null && MioDeviceConnection.this.recordSize > 0) {
                    MioDeviceConnection.this.recordBuf = new byte[MioDeviceConnection.this.recordSize];
                    MioDeviceConnection.this.dataLength = 0;
                    MioDeviceConnection.this.logDataPos = 0;
                }
                gotoNum = 0;
                try {
                    if (MioDeviceConnection.this.getCurSessionDataLen == 0 && raw.length >= 9) {
                        MioDeviceConnection.this.curSessionID = (raw[MioDeviceConnection.GET_RECORD_WO_T] & 255) + ((raw[MioDeviceConnection.GET_RECORD_WO_N] & 255) << 8);
                        MioDeviceConnection.this.sessionDataSize = (raw[7] & 255) + ((raw[8] & 255) << 8);
                        gotoNum = 9;
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Log.e("gotoNum1", "gotoNum=" + gotoNum + " raw.length=" + raw.length + " dataLength=" + MioDeviceConnection.this.dataLength + " getAllRecordFlag=" + MioDeviceConnection.this.getAllRecordFlag);
                for (i = gotoNum; i < raw.length && MioDeviceConnection.this.getCurSessionDataLen < MioDeviceConnection.this.sessionDataSize && MioDeviceConnection.this.dataLength < MioDeviceConnection.this.recordBuf.length; i += MioDeviceConnection.DEL_RECORD) {
                    MioDeviceConnection.this.recordBuf[MioDeviceConnection.this.dataLength] = raw[i];
                    mioDeviceConnection = MioDeviceConnection.this;
                    mioDeviceConnection.getCurSessionDataLen += MioDeviceConnection.DEL_RECORD;
                    mioDeviceConnection = MioDeviceConnection.this;
                    mioDeviceConnection.dataLength += MioDeviceConnection.DEL_RECORD;
                }
                Log.e("gotoNum2", "gotoNum=" + gotoNum + " raw.length=" + raw.length + " dataLength=" + MioDeviceConnection.this.dataLength);
                Log.e("onCharacteristicChanged", "recordIndex=" + MioDeviceConnection.this.recordIndex + " curSessionID= " + MioDeviceConnection.this.curSessionID + " sessionTotal=" + MioDeviceConnection.this.sessionTotal + " rType=" + MioDeviceConnection.this.rType + "----sessionDataSize=" + MioDeviceConnection.this.sessionDataSize + " getCurSessionDataLen=" + MioDeviceConnection.this.getCurSessionDataLen + " dataLength=" + MioDeviceConnection.this.dataLength + "  recordBuf.length=" + MioDeviceConnection.this.recordBuf.length + " getCurSessionDataLen=" + MioDeviceConnection.this.getCurSessionDataLen);
                MioSportMsgParserUtil.writeLogtoFile("downRecord", DiffResult.OBJECTS_SAME_STRING, "recordIndex=" + MioDeviceConnection.this.recordIndex + " curSessionID= " + MioDeviceConnection.this.curSessionID + "----sessionDataSize=" + MioDeviceConnection.this.sessionDataSize + " getCurSessionDataLen=" + MioDeviceConnection.this.getCurSessionDataLen);
                if (MioDeviceConnection.this.sessionTotal > MioDeviceConnection.this.curSessionID && MioDeviceConnection.this.dataLength < MioDeviceConnection.this.recordBuf.length && (MioDeviceConnection.this.getCurSessionDataLen >= MioDeviceConnection.this.sessionDataSize || MioDeviceConnection.this.sessionDataSize == 0)) {
                    Log.e("onCharacteristicChanged", "onCharacteristicChanged-1");
                    mioDeviceConnection = MioDeviceConnection.this;
                    mioDeviceConnection.curSessionID += MioDeviceConnection.DEL_RECORD;
                    if (MioDeviceConnection.GET_ALPHA2_RECORD_N == MioDeviceConnection.this.rType) {
                        if (MioDeviceConnection.this.curSessionID == MioDeviceConnection.GET_RECORD_ADL_T) {
                            MioDeviceConnection.this.logDataPos = MioDeviceConnection.this.dataLength;
                        }
                        MioDeviceConnection.this.GetRecordPacket_MIO(MioDeviceConnection.this.curSessionID);
                    } else if (MioDeviceConnection.GET_VELO_RECORD_N == MioDeviceConnection.this.rType) {
                        Log.e("GetVeloSession_MIO", "GetVeloSession_MIO1");
                        MioDeviceConnection.this.GetVeloSession_MIO(MioDeviceConnection.this.curSessionID);
                    } else {
                        Log.e("GetSession_MIO", "3");
                        MioDeviceConnection.this.GetSession_MIO(MioDeviceConnection.this.curSessionID);
                    }
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_VELO_RECORD_N) {
                    Log.e("GET_VELO_RECORD_N", "dataLength" + MioDeviceConnection.this.dataLength + " recordBuf.length=" + MioDeviceConnection.this.recordBuf.length);
                    if (MioDeviceConnection.this.dataLength >= MioDeviceConnection.this.recordBuf.length) {
                        Log.e("GET_VELO_RECORD_N", "GET_VELO_RECORD_N2");
                        MioDeviceConnection.this.curSessionID = MioDeviceConnection.DEL_RECORD;
                        MioDeviceConnection.this.sessionDataSize = 0;
                        MioDeviceConnection.this.getCurSessionDataLen = 0;
                        Log.e("GET_VELO_RECORD_N", "GET_VELO_RECORD_N21");
                        MioDeviceConnection.this.rType = 0;
                        if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                            MioSportMsgParserUtil.parserVeloRecordData(MioDeviceConnection.this.recordBuf, MioDeviceConnection.this.mioDeviceCallBack, MioDeviceConnection.this.numberOfRecord, MioDeviceConnection.this.recordIndex);
                        }
                        MioSportMsgParserUtil.writeLogtoFile("downRecord", DiffResult.OBJECTS_SAME_STRING, "recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                        Log.e("parserVeloRecordData", "recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                        if (MioDeviceConnection.this.getAllRecordFlag) {
                            Log.e("GET_VELO_RECORD_N", "GET_VELO_RECORD_N3");
                            if (MioDeviceConnection.this.recordIndex < MioDeviceConnection.this.numberOfRecord) {
                                MioDeviceConnection.this.doGetVeloRecord_MIO(MioDeviceConnection.this.recordIndex + MioDeviceConnection.DEL_RECORD);
                                return;
                            }
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                            if (MioDeviceConnection.this.delRecordFlag) {
                                MioDeviceConnection.this.DeleteRecord_MIO(RType.TYPE_WORKOUT_EXERCISE, DelOPType.DELETE_ALL_RECORD);
                            }
                            MioDeviceConnection.this.getAllRecordFlag = false;
                            MioDeviceConnection.this.delRecordFlag = false;
                        }
                    }
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_ALPHA2_RECORD_N) {
                    Log.e("onCharacteristicChanged", "onCharacteristicChanged-2");
                    if (MioDeviceConnection.this.dataLength == MioDeviceConnection.this.recordBuf.length) {
                        Log.e("onCharacteristicChanged", "onCharacteristicChanged-3");
                        Log.e("rType16", "rType=" + MioDeviceConnection.this.rType);
                        MioDeviceConnection.this.rType = 0;
                        MioDeviceConnection.this.curSessionID = MioDeviceConnection.DEL_RECORD;
                        MioDeviceConnection.this.sessionDataSize = 0;
                        MioDeviceConnection.this.getCurSessionDataLen = 0;
                        if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                            Log.e("onCharacteristicChanged", "onCharacteristicChanged-4");
                            MioSportMsgParserUtil.parserExerciseRecordData(MioDeviceConnection.this.recordBuf, MioDeviceConnection.this.mioDeviceCallBack, MioDeviceConnection.this.numberOfRecord, MioDeviceConnection.this.recordIndex, MioDeviceConnection.this.logDataPos);
                        }
                        System.out.println("recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                        if (!MioDeviceConnection.this.getAllRecordFlag) {
                            return;
                        }
                        if (MioDeviceConnection.this.recordIndex < MioDeviceConnection.this.numberOfRecord) {
                            MioDeviceConnection.this.doGetAlpha2Record_MIO(MioDeviceConnection.this.recordIndex + MioDeviceConnection.DEL_RECORD);
                            return;
                        }
                        MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                        if (MioDeviceConnection.this.delRecordFlag) {
                            MioDeviceConnection.this.DeleteAlpha2Record_MIO(DelOPType.DELETE_ALL_RECORD);
                        }
                        MioDeviceConnection.this.getAllRecordFlag = false;
                        MioDeviceConnection.this.delRecordFlag = false;
                    }
                } else {
                    Log.e("onCharacteristicChanged", "onCharacteristicChanged-5");
                    if (MioDeviceConnection.this.dataLength == MioDeviceConnection.this.recordBuf.length || (MioDeviceConnection.this.sessionTotal == MioDeviceConnection.this.curSessionID && MioDeviceConnection.this.sessionDataSize == 0)) {
                        Log.e("onCharacteristicChanged", "onCharacteristicChanged-6");
                        MioDeviceConnection.this.curSessionID = MioDeviceConnection.DEL_RECORD;
                        MioDeviceConnection.this.sessionDataSize = 0;
                        MioDeviceConnection.this.getCurSessionDataLen = 0;
                        byte[] tempRecordBuf = MioDeviceConnection.this.recordBuf;
                        if (MioDeviceConnection.this.recordBuf.length > MioDeviceConnection.this.dataLength) {
                            tempRecordBuf = new byte[MioDeviceConnection.this.dataLength];
                            for (i = 0; i < MioDeviceConnection.this.dataLength; i += MioDeviceConnection.DEL_RECORD) {
                                tempRecordBuf[i] = MioDeviceConnection.this.recordBuf[i];
                            }
                        }
                        if (MioDeviceConnection.this.recordDataType == MioDeviceConnection.GET_RECORD_ADL_D_F) {
                            Log.e("rType17", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioDeviceConnection.this.mioDeviceCallBack.onGetTodayADLRecord(MioSportMsgParserUtil.parserADLTodayData(tempRecordBuf), (byte) 0);
                            }
                            MioSportMsgParserUtil.printRaw("TodayData", tempRecordBuf);
                        } else if (MioDeviceConnection.this.recordDataType == MioDeviceConnection.GET_RECORD_ADL_T) {
                            Log.e("rType18", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioDeviceConnection.this.mioDeviceCallBack.onGetRecordOfDailyADL(MioSportMsgParserUtil.parserADLDailyData(tempRecordBuf), (short) MioDeviceConnection.this.numberOfRecord, (short) MioDeviceConnection.this.recordIndex, (byte) 0);
                            }
                            System.out.println("recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord + " getAllRecordFlag=" + MioDeviceConnection.this.getAllRecordFlag);
                            MioSportMsgParserUtil.printRaw("DialyFirstData", tempRecordBuf);
                            if (!MioDeviceConnection.this.getAllRecordFlag) {
                                return;
                            }
                            if (MioDeviceConnection.this.recordIndex < MioDeviceConnection.this.numberOfRecord) {
                                MioDeviceConnection.this.doGetNextRecordOfDailyADL_MIO();
                                return;
                            }
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                            if (MioDeviceConnection.this.delRecordFlag) {
                                MioDeviceConnection.this.DeleteRecord_MIO(RType.TYPE_ADL_DAILY, DelOPType.DELETE_ALL_RECORD);
                            }
                            MioDeviceConnection.this.getAllRecordFlag = false;
                            MioDeviceConnection.this.delRecordFlag = false;
                        } else if (MioDeviceConnection.this.recordDataType == MioDeviceConnection.DEL_RECORD) {
                            Log.e("rType19", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioSportMsgParserUtil.parserWorkoutRecordData(tempRecordBuf, MioDeviceConnection.this.mioDeviceCallBack, MioDeviceConnection.this.numberOfRecord, MioDeviceConnection.this.recordIndex);
                            }
                            MioSportMsgParserUtil.writeLogtoFile("downRecord", DiffResult.OBJECTS_SAME_STRING, "recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                            System.out.println("recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                            if (!MioDeviceConnection.this.getAllRecordFlag) {
                                return;
                            }
                            if (MioDeviceConnection.this.recordIndex < MioDeviceConnection.this.numberOfRecord) {
                                MioDeviceConnection.this.doGetWorkoutRecord_MIO(MioDeviceConnection.this.recordIndex + MioDeviceConnection.DEL_RECORD);
                                return;
                            }
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                            if (MioDeviceConnection.this.delRecordFlag) {
                                MioDeviceConnection.this.DeleteRecord_MIO(RType.TYPE_WORKOUT_EXERCISE, DelOPType.DELETE_ALL_RECORD);
                            }
                            MioDeviceConnection.this.getAllRecordFlag = false;
                            MioDeviceConnection.this.delRecordFlag = false;
                        } else if (MioDeviceConnection.this.recordDataType == MioDeviceConnection.GET_RECORD_WO_T) {
                            Log.e("rType190", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                MioDeviceConnection.this.allSleepRecords = MioSportMsgParserUtil.parserSleepRecordData(tempRecordBuf);
                            }
                            MioSportMsgParserUtil.writeLogtoFile("downRecord", DiffResult.OBJECTS_SAME_STRING, "recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                            System.out.println("recordIndex=" + MioDeviceConnection.this.recordIndex + " numberOfRecord= " + MioDeviceConnection.this.numberOfRecord);
                            Log.e("getAllRecordFlag", "getAllRecordFlag=" + MioDeviceConnection.this.getAllRecordFlag);
                            if (!MioDeviceConnection.this.getAllRecordFlag) {
                                return;
                            }
                            if (MioDeviceConnection.this.recordIndex < MioDeviceConnection.this.numberOfRecord) {
                                MioDeviceConnection.this.doGetSleepRecord_MIO(MioDeviceConnection.this.recordIndex + MioDeviceConnection.DEL_RECORD);
                                return;
                            }
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                            if (MioDeviceConnection.this.delRecordFlag) {
                                MioDeviceConnection.this.DeleteAllActivityRecord_MIO(RType.TYPE_ACTIVITY, DelOPType.DELETE_ALL_RECORD);
                            }
                            MioDeviceConnection.this.getAllRecordFlag = false;
                            MioDeviceConnection.this.delRecordFlag = false;
                            new Thread(new C02773()).start();
                        } else if (MioDeviceConnection.this.recordDataType == MioDeviceConnection.GET_RECORD_WO_N) {
                            Log.e("rType199", "rType=" + MioDeviceConnection.this.rType);
                            MioDeviceConnection.this.rType = 0;
                            if (MioDeviceConnection.this.getAllRecordFlag) {
                                MioDeviceConnection.this.getAllRecordFlag = false;
                                MioDeviceConnection.this.delRecordFlag = false;
                                if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                                    MioSportMsgParserUtil.parserCurHourSleepRecordData(tempRecordBuf, MioDeviceConnection.this.mioDeviceCallBack, MioDeviceConnection.this.allSleepRecords, MioDeviceConnection.this.isSleepFullMemory);
                                }
                                MioSportMsgParserUtil.writeLogtoFile("curSleepRecord", DiffResult.OBJECTS_SAME_STRING, DiffResult.OBJECTS_SAME_STRING);
                            }
                        }
                    }
                }
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("onCharacteristicRead", "uuid=" + characteristic.getUuid().toString());
            Log.e("onCharacteristicRead", "status=" + status);
            if (status == 0) {
                UUID uuid = characteristic.getUuid();
                Log.e("onCharacteristicRead", "uuid=" + uuid.toString());
                byte[] raw = characteristic.getValue();
                Log.d("m9d","DATA=" + MioHelper.toHexCode(raw));
                if (MioHelper.UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING.equals(uuid)) {
                    MioDeviceConnection.this.mioDeviceInformation.SetDeviceManufactureName_MIO(MioHelper.getStringValue(raw, 0));
                    Log.e("readCharacteristic", "readCharacteristic1");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicModelNumberString);
                } else if (MioHelper.UUID_CHARACTERISTIC_MODEL_NUMBER_STRING.equals(uuid)) {
                    MioDeviceConnection.this.mioDeviceInformation.SetDeviceModelNumber_MIO(characteristic.getStringValue(0));
                    Log.e("readCharacteristic", "readCharacteristic2");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicSerialNumberString);
                } else if (MioHelper.UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING.equals(uuid)) {
                    byte intValue = 0;
                    if (raw.length > 0) {
                        intValue = raw[0];
                    }
                    if (raw.length > MioDeviceConnection.DEL_RECORD) {
                        intValue += raw[MioDeviceConnection.DEL_RECORD] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_T) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_T] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_D_F) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_D_F] << 8;
                    }
                    if (raw.length > 0) {
                        StringBuilder sb = new StringBuilder(raw.length);
                        int r9 = raw.length;
                        for (int r8 = 0; r8 < r9; r8 += MioDeviceConnection.DEL_RECORD) {
                            Object[] r11 = new Object[MioDeviceConnection.DEL_RECORD];
                            r11[0] = Byte.valueOf(raw[r8]);
                            sb.append(String.format("%c", r11));
                        }
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceSerialNumber_MIO(sb.toString());
                    } else {
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceSerialNumber_MIO(EnvironmentCompat.MEDIA_UNKNOWN);
                    }
                    Log.e("readCharacteristic", "readCharacteristic3");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicHardwareRevisionString);
                } else if (MioHelper.UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING.equals(uuid)) {
                    int intValue = 0;
                    // TODO
                    // this chagne maybe cause some problems.
                    if (raw.length > 0) {
                        intValue = raw[0];
                    }
                    if (raw.length > MioDeviceConnection.DEL_RECORD) {
                        intValue += raw[MioDeviceConnection.DEL_RECORD] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_T) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_T] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_D_F) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_D_F] << 8;
                    }
                    if (raw.length > 0) {
                        StringBuilder sb = new StringBuilder(raw.length);
                        int r9 = raw.length;
                        for (int r8 = 0; r8 < r9; r8 += MioDeviceConnection.DEL_RECORD) {
                            Object[] r11 = new Object[MioDeviceConnection.DEL_RECORD];
                            r11[0] = Byte.valueOf(raw[r8]);
                            sb.append(String.format("%c", r11));
                        }
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceHardwareRevision_MIO(sb.toString());
                    } else {
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceHardwareRevision_MIO(EnvironmentCompat.MEDIA_UNKNOWN);
                    }
                    Log.e("readCharacteristic", "readCharacteristic4");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicFirmwareRevisionString);
                } else if (MioHelper.UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING.equals(uuid)) {
                    MioDeviceConnection.this.mioDeviceInformation.SetDeviceUIFirmwareRevision_MIO(MioHelper.getStringValue(raw, 0));
                    Log.e("readCharacteristic", "readCharacteristic5");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicSoftwareRevisionString);
                } else if (MioHelper.UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING.equals(uuid)) {
                    int intValue = 0;
                    if (raw.length > 0) {
                        intValue = raw[0];
                    }
                    if (raw.length > MioDeviceConnection.DEL_RECORD) {
                        intValue += raw[MioDeviceConnection.DEL_RECORD] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_T) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_T] << 8;
                    }
                    if (raw.length > MioDeviceConnection.GET_RECORD_ADL_D_F) {
                        intValue += raw[MioDeviceConnection.GET_RECORD_ADL_D_F] << 8;
                    }
                    if (raw.length > 0) {
                        StringBuilder sb = new StringBuilder(raw.length);
                        int r9 = raw.length;
                        for (int r8 = 0; r8 < r9; r8 += MioDeviceConnection.DEL_RECORD) {
                            Object[] r11 = new Object[MioDeviceConnection.DEL_RECORD];
                            r11[0] = Byte.valueOf(raw[r8]);
                            sb.append(String.format("%c", r11));
                        }
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceOHRFirmwareRevision_MIO(sb.toString());
                    } else {
                        MioDeviceConnection.this.mioDeviceInformation.SetDeviceOHRFirmwareRevision_MIO(EnvironmentCompat.MEDIA_UNKNOWN);
                    }
                    Log.e("readCharacteristic", "readCharacteristic6");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicSystemIdString);
                } else if (MioHelper.UUID_CHARACTERISTIC_SYSTEM_ID_STRING.equals(uuid)) {
                    MioDeviceConnection.this.mioDeviceInformation.SetDeviceSystemId_MIO(MioHelper.toHexCode(raw));
                    if (MioDeviceConnection.this.mCharacteristicMioSportMsg != null) {
                        Log.e("readCharacteristic", "readCharacteristic7");
                        boolean readCharacteristicMioSportMsgResp = MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicMioSportMsgResp);
                        System.out.println("readCharacteristicMioSportMsgResp=" + readCharacteristicMioSportMsgResp);
                        if (!readCharacteristicMioSportMsgResp) {
                            Log.e("readCharacteristic", "readCharacteristic8");
                            readCharacteristicMioSportMsgResp = MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicMioSportMsgResp);
                            System.out.println("readCharacteristicMioSportMsgResp2=" + readCharacteristicMioSportMsgResp);
                        }
                        if (!readCharacteristicMioSportMsgResp) {
                            MioDeviceConnection.this.enableHRZoneMsgRespNotification();
                        }
                    }
                } else if (MioHelper.UUID_CHARACTERISTIC_MIO_SPORT_MSG_RESP.equals(uuid)) {
                    MioDeviceConnection.this.enableHRZoneMsgRespNotification();
                } else if (MioHelper.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(uuid)) {
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    MioDeviceConnection.this.batteryValue = characteristic.getValue()[0];
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    if (MioDeviceConnection.this.batteryStateCallback != null) {
                        Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                        MioDeviceConnection.this.batteryStateCallback.OnDeviceBatteryChanged_MIO(MioDeviceConnection.this.batteryValue);
                    }
                }
            } else if (MioDeviceConnection.this.thirdBLE) {
                Log.e("thirdBLE", "doConnectSuccess");
                MioDeviceConnection.this.doConnectSuccess();
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            UUID uuid = characteristic.getUuid();
            Log.e("onCharacteristicWrite", "uuid=" + uuid.toString());
            if ((MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_LINK_ENTER_DFUMODE || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_ALPHA2_ENTER_DFUMODE) && status != 0) {
                if (MioDeviceConnection.this.deviceDFUCallBack != null) {
                    MioDeviceConnection.this.deviceDFUCallBack.onError("Internal error,please retry!(" + status + ")", 9);
                }
            } else if (MioHelper.DFU_CONTROLPOINT_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.DFU_PACKET_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.DFU_STATUS_REPORT_CHARACTERISTIC_UUID.equals(uuid)) {
                if (MioDeviceConnection.this.commonDFUManager != null) {
                    MioDeviceConnection.this.commonDFUManager.oncharacteristicWrite(gatt, characteristic, status);
                }
            } else if ((MioHelper.ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID.equals(uuid) || MioHelper.ALPHA2_DFU_SENDPACKET_CHARACTERISTIC_UUID.equals(uuid)) && MioDeviceConnection.this.alpha2DFUManager != null) {
                MioDeviceConnection.this.alpha2DFUManager.oncharacteristicWrite(gatt, characteristic, status);
            }
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            boolean resetBLE = true;
            Log.e("onConnectionStateChange", "onConnectionStateChange--" + status + "--" + newState);
            if (MioDeviceConnection.this.isEnterDFUMode) {
                if (MioDeviceConnection.this.commonDFUManager != null) {
                    MioDeviceConnection.this.commonDFUManager.onconnecttionStateChange(gatt, status, newState);
                } else if (MioDeviceConnection.this.alpha2DFUManager != null) {
                    MioDeviceConnection.this.alpha2DFUManager.onconnecttionStateChange(gatt, status, newState);
                }
            }
            MioDeviceConnection mioDeviceConnection;
            if (newState == MioDeviceConnection.GET_RECORD_ADL_T) {
                Log.e("onConnectionStateChange", "STATE_CONNECTED--1");
                MioDeviceConnection.this.bleIsConnected = true;
                MioSportMsgParserUtil.writeLogtoFile("ble connected", "discoverServices-ing---------", DiffResult.OBJECTS_SAME_STRING);
                if (status == 0) {
                    Log.e("onConnectionStateChange", "STATE_CONNECTED--223");
                    if (MioDeviceConnection.this.bluetoothGatt != null) {
                        Log.e("onConnectionStateChange", "STATE_CONNECTED--2");
                        MioDeviceConnection.this.bluetoothGatt.discoverServices();
                    }
                } else {
                    MioSportMsgParserUtil.writeLogtoFile("BluetoothGatt_status", "status=" + status, DiffResult.OBJECTS_SAME_STRING);
                    System.out.println("BluetoothGatt_status=" + status);
                    if (!MioDeviceConnection.this.isDisconnectedByUser) {
                        if (!(status == 129 || status == 133)) {
                            resetBLE = false;
                        }
                        Log.e("onConnectionStateChange", "STATE_CONNECTED--234");
                        if (status == 133 && MioDeviceConnection.this.isCoonecting) {
                            Log.e("onConnectionStateChange", "retryCount--" + MioDeviceConnection.this.retryCount);
                            mioDeviceConnection = MioDeviceConnection.this;
                            mioDeviceConnection.retryCount = mioDeviceConnection.retryCount + MioDeviceConnection.DEL_RECORD;
                            if (MioDeviceConnection.this.retryCount > 0) {
                                MioDeviceConnection.this.autoRetry = false;
                            }
                            MioDeviceConnection.this.doBluetoothReset(resetBLE);
                        } else {
                            MioDeviceConnection.this.doBluetoothReset(resetBLE);
                        }
                    }
                }
                Log.e("onConnectionStateChange", "STATE_CONNECTED--2");
            } else if (newState == 0) {
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mBluetoothSettingTimeOut);
                Log.e("onConnectionStateChange", "STATE_DISCONNECTED--1");
                MioDeviceConnection.this.bleIsConnected = false;
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                Log.e("onConnectionStateChange", "isConnected=" + MioDeviceConnection.this.isConnected);
                if (MioDeviceConnection.this.isConnected && MioDeviceConnection.this.recordBuf != null && MioDeviceConnection.this.dataLength < MioDeviceConnection.this.recordBuf.length) {
                    Log.e("onConnectionStateChange", "resumeDown=" + MioDeviceConnection.this.resumeDown);
                    MioDeviceConnection.this.resumeDown = true;
                }
                if (MioDeviceConnection.this.connectionStateCallback == null || MioDeviceConnection.this.bluetoothAdapter == null || MioDeviceConnection.this.bluetoothAdapter.isEnabled()) {
                    MioDeviceConnection.this.isDisconnectedByUser = true;
                } else {
                    MioDeviceConnection.this.isConnected = false;
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.connectionStateCallback.OnBluetoothClosed_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                }
                System.out.println(new StringBuilder(String.valueOf(MioDeviceConnection.this.deviceAddress)).append("-----------------ble disconnected------------status=").append(status).toString());
                MioSportMsgParserUtil.writeLogtoFile("ble disconnected", "disconnected---------", DiffResult.OBJECTS_SAME_STRING);
                if (MioDeviceConnection.this.isDisconnectedByUser) {
                    if (!(MioDeviceConnection.this.connectionStateCallback == null || MioDeviceConnection.this.userStopNotify)) {
                        MioDeviceConnection.this.userStopNotify = true;
                        System.out.println(new StringBuilder(String.valueOf(MioDeviceConnection.this.deviceAddress)).append("-Disconnected-").append(MioDeviceConnection.this.deviceUID).toString());
                        Log.e("onConnectionStateChange", "OnDeviceDisconnected_MIO");
                        MioDeviceConnection.this.isCoonecting = false;
                        MioDeviceConnection.this.retryCount = 0;
                        MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                    }
                    MioDeviceConnection.this.isConnected = false;
                    MioDeviceConnection.this.notifyThread = null;
                    MioDeviceConnection.this.isHRNotificationEnabled = false;
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                    return;
                }
                if (MioDeviceConnection.this.bluetoothGatt != null) {
                    try {
                        MioDeviceConnection.this.bluetoothGatt.close();
                    } catch (Exception e) {
                        Log.e("onConnectionStateChange", "OnDeviceDisconnected_MIO991");
                        e.printStackTrace();
                    }
                    MioDeviceConnection.this.bluetoothGatt = null;
                }
                if (!MioDeviceConnection.this.isExceptionDisconnection && MioDeviceConnection.this.isConnected) {
                    MioDeviceConnection.this.isExceptionDisconnection = true;
                    if (MioDeviceConnection.this.connectionStateCallback != null) {
                        System.out.println(new StringBuilder(String.valueOf(MioDeviceConnection.this.deviceAddress)).append("-Disconnected-").append(MioDeviceConnection.this.deviceUID).toString());
                        Log.e("onConnectionStateChange", "OnDeviceDisconnected_MIO2");
                        MioDeviceConnection.this.isCoonecting = false;
                        MioDeviceConnection.this.retryCount = 0;
                        MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                    }
                }
                if (MioDeviceConnection.this.autoRetry) {
                    String tag;
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.isConnected = false;
                    MioDeviceConnection.this.notifyThread = null;
                    MioDeviceConnection.this.isHRNotificationEnabled = false;
                    if (MioDeviceConnection.this.veloAppType == null || MioDeviceConnection.this.veloAppType != VeloAppType.TYPE_WAHOO) {
                        tag = "TYPE_MIO";
                    } else {
                        tag = "TYPE_WAHOO";
                    }
                    Log.e("STATE_DISCONNECTED", "retrydeviceName=" + MioDeviceConnection.this.deviceName + "deviceUID=" + MioDeviceConnection.this.deviceUID + "veloAppType=" + tag);
                    if (!MioDeviceConnection.this.deviceName.contains("VELO") || MioDeviceConnection.this.deviceUID == null || MioDeviceConnection.this.deviceUID.length() <= 0) {
                        Log.e("retry2", "retryCount=" + MioDeviceConnection.this.retryCount);
                        mioDeviceConnection = MioDeviceConnection.this;
                        mioDeviceConnection.retryCount = mioDeviceConnection.retryCount + MioDeviceConnection.DEL_RECORD;
                        if (MioDeviceConnection.this.retryCount > MioDeviceConnection.GET_RECORD_ADL_T) {
                            MioDeviceConnection.this.autoRetry = false;
                        }
                        if (MioDeviceConnection.this.bluetoothAdapter.isEnabled()) {
                            Log.e("DoReConnect", "DoReConnect1");
                            MioDeviceConnection.this.DoReConnect();
                            return;
                        }
                        MioDeviceConnection.this.isConnected = false;
                        Log.e("onConnectionStateChange", "OnDeviceDisconnected_MIO453");
                        MioDeviceConnection.this.isCoonecting = false;
                        MioDeviceConnection.this.retryCount = 0;
                        MioDeviceConnection.this.connectionStateCallback.OnBluetoothClosed_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                        return;
                    }
                    Log.e("retry1", "retryCount=" + MioDeviceConnection.this.retryCount);
                    mioDeviceConnection = MioDeviceConnection.this;
                    mioDeviceConnection.retryCount = mioDeviceConnection.retryCount + MioDeviceConnection.DEL_RECORD;
                    if (MioDeviceConnection.this.retryCount > MioDeviceConnection.GET_RECORD_ADL_T) {
                        MioDeviceConnection.this.autoRetry = false;
                    }
                    MioDeviceConnection.this.handleVeloReconnect();
                    return;
                }
                Log.e("retry3", "retryCount=" + MioDeviceConnection.this.retryCount);
                MioDeviceConnection.this.isConnected = false;
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                if (MioDeviceConnection.this.connectionStateCallback != null && !MioDeviceConnection.this.isExceptionDisconnection) {
                    System.out.println(new StringBuilder(String.valueOf(MioDeviceConnection.this.deviceAddress)).append("-Disconnected-").append(MioDeviceConnection.this.deviceUID).toString());
                    Log.e("onConnectionStateChange", "OnDeviceDisconnected_MIO3");
                    MioDeviceConnection.this.isCoonecting = false;
                    MioDeviceConnection.this.retryCount = 0;
                    MioDeviceConnection.this.connectionStateCallback.OnDeviceDisconnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                }
            }
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e("onDescriptorRead", "uuid=" + descriptor.getUuid().toString());
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            Log.e("onDescriptorWrite", "uuid=" + descriptor.getUuid().toString());
            if (status == 0 && MioDeviceConnection.this.isEnterDFUMode) {
                if (MioDeviceConnection.this.commonDFUManager != null) {
                    Log.e("onDescriptorWrite", "commonDFUManager");
                    MioDeviceConnection.this.commonDFUManager.ondescriptorWrite(gatt, descriptor, status);
                } else if (MioDeviceConnection.this.alpha2DFUManager != null) {
                    Log.e("onDescriptorWrite", "alpha2DFUManager");
                    MioDeviceConnection.this.alpha2DFUManager.ondescriptorWrite(gatt, descriptor, status);
                }
            } else if (status == 0 && !MioDeviceConnection.this.isHRZoneGet) {
                MioDeviceConnection.this.requestHRZoneParameters();
            }
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (MioDeviceConnection.this.rssiStateCallback != null) {
                MioDeviceConnection.this.rssiStateCallback.OnDeviceRSSIChanged_MIO(rssi);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            boolean resetBLE = false;
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            Log.e("onServicesDiscovered", "onServicesDiscovered--" + status);
            if (status == 0) {
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                BluetoothGattService service = gatt.getService(MioHelper.UUID_SERVICE_DEVICE_INFORMATION);
                if (service == null) {
                    if (MioDeviceConnection.this.retryConnect) {
                        MioDeviceConnection.this.retryConnect = false;
                        MioDeviceConnection.this.discoverServices();
                    }
                    Log.e("onServicesDiscovered", "onServicesDiscovered--1");
                    MioDeviceConnection.this.mCharacteristicSystemIdString = null;
                    MioDeviceConnection.this.mCharacteristicModelNumberString = null;
                    MioDeviceConnection.this.mCharacteristicSerialNumberString = null;
                    MioDeviceConnection.this.mCharacteristicFirmwareRevisionString = null;
                    MioDeviceConnection.this.mCharacteristicHardwareRevisionString = null;
                    MioDeviceConnection.this.mCharacteristicSoftwareRevisionString = null;
                    MioDeviceConnection.this.mCharacteristicSystemIdString = null;
                    MioDeviceConnection.this.mCharacteristicSystemIdString = null;
                    MioDeviceConnection.this.mCharacteristicManufactureNameString = null;
                    if (!MioDeviceConnection.this.isEnterDFUMode) {
                        Log.e("onServicesDiscovered", "onServicesDiscovered--11");
                        if (MioDeviceConnection.this.bluetoothGatt != null) {
                            try {
                                MioDeviceConnection.this.bluetoothGatt.close();
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        return;
                    }
                }
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                MioDeviceConnection.this.mCharacteristicSystemIdString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_SYSTEM_ID_STRING);
                MioDeviceConnection.this.mCharacteristicModelNumberString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MODEL_NUMBER_STRING);
                MioDeviceConnection.this.mCharacteristicSerialNumberString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING);
                MioDeviceConnection.this.mCharacteristicFirmwareRevisionString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING);
                MioDeviceConnection.this.mCharacteristicHardwareRevisionString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING);
                MioDeviceConnection.this.mCharacteristicSoftwareRevisionString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING);
                MioDeviceConnection.this.mCharacteristicManufactureNameString = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING);
                if ((MioDeviceConnection.this.mCharacteristicSystemIdString == null || MioDeviceConnection.this.mCharacteristicModelNumberString == null || MioDeviceConnection.this.mCharacteristicSerialNumberString == null || MioDeviceConnection.this.mCharacteristicFirmwareRevisionString == null || MioDeviceConnection.this.mCharacteristicHardwareRevisionString == null || MioDeviceConnection.this.mCharacteristicSoftwareRevisionString == null || MioDeviceConnection.this.mCharacteristicSystemIdString == null || MioDeviceConnection.this.mCharacteristicSystemIdString == null || MioDeviceConnection.this.mCharacteristicManufactureNameString == null) && !MioDeviceConnection.this.thirdBLE) {
                    if (MioDeviceConnection.this.retryConnect) {
                        MioDeviceConnection.this.retryConnect = false;
                        MioDeviceConnection.this.discoverServices();
                    }
                    Log.e("onServicesDiscovered", "onServicesDiscovered--2");
                    if (!MioDeviceConnection.this.isEnterDFUMode) {
                        Log.e("onServicesDiscovered", "onServicesDiscovered--21");
                        MioDeviceConnection.this.doBluetoothReset(true);
                        return;
                    }
                }
                service = gatt.getService(MioHelper.UUID_SERVICE_DFU);
                if (service != null) {
                    Log.e("onServicesDiscovered", "UUID_SERVICE_DFU--1");
                    MioDeviceConnection.this.mioDeviceInformation.SetDFUServiceSupported_MIO();
                    MioDeviceConnection.this.mCharacteristicDFUControlPoint = service.getCharacteristic(MioHelper.DFU_CONTROLPOINT_CHARACTERISTIC_UUID);
                    MioDeviceConnection.this.mCharacteristicDFUPacket = service.getCharacteristic(MioHelper.DFU_PACKET_CHARACTERISTIC_UUID);
                    MioDeviceConnection.this.commonDFUManager = new CommonDFUManager();
                } else {
                    MioDeviceConnection.this.mioDeviceInformation.SetDFUServiceNoSupported_MIO();
                    MioDeviceConnection.this.mCharacteristicDFUControlPoint = null;
                    MioDeviceConnection.this.mCharacteristicDFUPacket = null;
                    MioDeviceConnection.this.commonDFUManager = null;
                    service = gatt.getService(MioHelper.UUID_SERVICE_ALPHA2_DFU);
                    if (service != null) {
                        Log.e("onServicesDiscovered", "UUID_SERVICE_ALPHA2_DFU--1");
                        MioDeviceConnection.this.mioDeviceInformation.SetDFUServiceSupported_MIO();
                        MioDeviceConnection.this.mCharacteristicDFUControlPoint = service.getCharacteristic(MioHelper.ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID);
                        MioDeviceConnection.this.mCharacteristicDFUPacket = service.getCharacteristic(MioHelper.ALPHA2_DFU_SENDPACKET_CHARACTERISTIC_UUID);
                        MioDeviceConnection.this.mCharacteristicAlpha2DFUSendPackage = service.getCharacteristic(MioHelper.ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID);
                        MioDeviceConnection.this.alpha2DFUManager = new Alpha2DFUManager();
                    } else {
                        MioDeviceConnection.this.mioDeviceInformation.SetDFUServiceNoSupported_MIO();
                        MioDeviceConnection.this.mCharacteristicDFUControlPoint = null;
                        MioDeviceConnection.this.mCharacteristicDFUPacket = null;
                        MioDeviceConnection.this.mCharacteristicAlpha2DFUSendPackage = null;
                        MioDeviceConnection.this.alpha2DFUManager = null;
                    }
                }
                service = gatt.getService(MioHelper.UUID_SERVICE_HEART_RATE);
                if (service == null) {
                    if (MioDeviceConnection.this.retryConnect) {
                        MioDeviceConnection.this.retryConnect = false;
                        MioDeviceConnection.this.discoverServices();
                    }
                    MioDeviceConnection.this.mCharacteristicHeartRate = null;
                    Log.e("onServicesDiscovered", "onServicesDiscovered--3");
                    if (!MioDeviceConnection.this.isEnterDFUMode) {
                        Log.e("onServicesDiscovered", "onServicesDiscovered--31");
                        return;
                    }
                }
                Log.e("onServicesDiscovered", "onServicesDiscovered--4444");
                MioDeviceConnection.this.mioDeviceInformation.SetRSSISupported_MIO();
                MioDeviceConnection.this.mioDeviceInformation.SetHeartRateSupported_MIO();
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                MioDeviceConnection.this.mCharacteristicHeartRate = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_HEART_RATE);
                if (MioDeviceConnection.this.mCharacteristicHeartRate == null) {
                    if (MioDeviceConnection.this.retryConnect) {
                        MioDeviceConnection.this.retryConnect = false;
                        MioDeviceConnection.this.discoverServices();
                    }
                    Log.e("onServicesDiscovered", "onServicesDiscovered--4");
                    if (!MioDeviceConnection.this.isEnterDFUMode) {
                        Log.e("onServicesDiscovered", "onServicesDiscovered--44");
                        return;
                    }
                }
                service = gatt.getService(MioHelper.UUID_SERVICE_BATTERY);
                if (service == null) {
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    MioDeviceConnection.this.mCharacteristicBatteryLevel = null;
                } else {
                    MioDeviceConnection.this.mioDeviceInformation.SetBatterySupported_MIO();
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    MioDeviceConnection.this.mCharacteristicBatteryLevel = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_BATTERY_LEVEL);
                }
                service = gatt.getService(MioHelper.UUID_SERVICE_MIO_SPORTS);
                if (service == null) {
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    MioDeviceConnection.this.mCharacteristicMioSportMsg = null;
                    MioDeviceConnection.this.mCharacteristicMioSportMsgResp = null;
                } else {
                    MioDeviceConnection.this.mioDeviceInformation.SetHRZoneSettingSupported_MIO();
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    MioDeviceConnection.this.mCharacteristicMioSportMsg = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MIO_SPORT_MSG);
                    MioDeviceConnection.this.mCharacteristicMioSportMsgResp = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MIO_SPORT_MSG_RESP);
                    Log.e("onServicesDiscovered", "onServicesDiscovered--884");
                    MioDeviceConnection.this.mCharacteristicMioRecordMsg = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MIO_RECORD);
                    MioDeviceConnection.this.mCharacteristicMioSenserData = service.getCharacteristic(MioHelper.UUID_CHARACTERISTIC_MIO_SENSER);
                    if (MioDeviceConnection.this.mCharacteristicMioSenserData == null) {
                        Log.e("mCharMioSenserData", "mCharacteristicMioSenserData--nbiant");
                    }
                    if (MioDeviceConnection.this.mCharacteristicMioSportMsg == null || MioDeviceConnection.this.mCharacteristicMioSportMsgResp == null) {
                        Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                        MioDeviceConnection.this.mCharacteristicMioSportMsg = null;
                        MioDeviceConnection.this.mCharacteristicMioSportMsgResp = null;
                    }
                }
                service = gatt.getService(MioHelper.SERVICES_SPEED_AND_CADENCE_UUID);
                if (service == null) {
                    MioDeviceConnection.this.characteristicSpeedAndCadence = null;
                } else {
                    MioDeviceConnection.this.characteristicSpeedAndCadence = service.getCharacteristic(MioHelper.CHARACTERISTIC_CHAR_CSC_MEASUREMENT);
                }
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                if (MioDeviceConnection.this.thirdBLE) {
                    Log.e("thirdBLE", "doConnectSuccess2");
                    MioDeviceConnection.this.doConnectSuccess();
                } else if (MioDeviceConnection.this.mCharacteristicManufactureNameString != null) {
                    Log.e("readCharacteristic", "readCharacteristic111");
                    MioDeviceConnection.this.bluetoothGatt.readCharacteristic(MioDeviceConnection.this.mCharacteristicManufactureNameString);
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                    MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mAutoConnTimeoutThread, MioDeviceConnection.this.timeout);
                }
            } else {
                MioSportMsgParserUtil.writeLogtoFile("BluetoothGatt_status", "status=" + status, DiffResult.OBJECTS_SAME_STRING);
                System.out.println("onServicesDiscovered=" + status);
                if (!MioDeviceConnection.this.isDisconnectedByUser) {
                    if (status == 129 || status == 133) {
                        resetBLE = true;
                    }
                    MioDeviceConnection.this.doBluetoothReset(resetBLE);
                }
            }
            Log.e("onServicesDiscovered", "onServicesDiscovered--6");
            if (MioDeviceConnection.this.isEnterDFUMode) {
                MioDeviceConnection.this.doConnectSuccess();
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.7 */
    class C02797 implements Runnable {
        C02797() {
        }

        public void run() {
            MioDeviceConnection.this.autoRetrySearchSensor = false;
            if (!MioDeviceConnection.this.searchSensorEndFlag) {
                MioDeviceConnection.this.searchSensorEndFlag = true;
                MioDeviceConnection.this.searchSensorFlag = 0;
                if (MioDeviceConnection.this.mioBikeSensorCallBack != null) {
                    MioDeviceConnection.this.mioBikeSensorCallBack.onBikeSensorScan(0, 0, (short) 0, (short) 0);
                }
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.8 */
    class C02808 implements Runnable {
        C02808() {
        }

        public void run() {
            MioDeviceConnection.this.bleCmdState = false;
            Log.e("mBTSettingTimeOut", "mBluetoothSettingTimeOut");
            if (MioDeviceConnection.this.mioBikeSensorCallBack != null && MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_BIKE_SET) {
                MioDeviceConnection.this.mioBikeSensorCallBack.onBikeSensorSetting((short) 48);
            }
            if (MioDeviceConnection.this.mioDeviceCallBack == null) {
                return;
            }
            if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_CURHOUR_N && (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_RUN_CMD)) {
                MioDeviceConnection.this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
            } else if ((MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_T) && (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_RUN_CMD)) {
                MioDeviceConnection.this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
            } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_WO_N && (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_RUN_CMD)) {
                MioDeviceConnection.this.mioDeviceCallBack.onGetWorkoutRecord(null, (short) 0, (short) 0, (byte) 48);
            } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_F && (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_RUN_CMD)) {
                MioDeviceConnection.this.mioDeviceCallBack.onGetRecordOfDailyADL(null, (short) 0, (short) 0, (byte) 48);
            } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_T && (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET || MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_RUN_CMD)) {
                MioDeviceConnection.this.mioDeviceCallBack.onGetTodayADLRecord(null, (byte) 48);
            } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_ALPHA2_RECORD_N) {
                Log.e("rType23", "rType=" + MioDeviceConnection.this.rType);
                MioDeviceConnection.this.rType = 0;
                MioDeviceConnection.this.mioDeviceCallBack.onGetAlpha2Record(null, (short) 0, (short) 0, (byte) 48);
            } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_VELO_RECORD_N) {
                Log.e("rType24", "rType=" + MioDeviceConnection.this.rType);
                MioDeviceConnection.this.rType = 0;
                MioDeviceConnection.this.mioDeviceCallBack.onGetVeloRecord(null, (short) 0, (short) 0, (byte) 48);
            } else if (MioDeviceConnection.this.cmdType == CMD_TYPE.CMD_TYPE_FACTORY_DEFAULT) {
                MioDeviceConnection.this.rType = 0;
            } else {
                MioDeviceConnection.this.rType = 0;
                MioDeviceConnection.this.mioDeviceCallBack.DidSendCMDTimeOut_MIO();
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.9 */
    class C02819 implements Runnable {
        C02819() {
        }

        public void run() {
            if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_WO_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_F || MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_T || MioDeviceConnection.this.rType == MioDeviceConnection.GET_VELO_RECORD_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_ALPHA2_RECORD_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_T || MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_CURHOUR_N) {
                Log.e("rType37", "rType=" + MioDeviceConnection.this.rType);
                MioDeviceConnection.this.rType = 0;
                if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                    MioDeviceConnection.this.mioDeviceCallBack.onSyncRecordTimeOut(49);
                }
            }
        }
    }

    public enum MIODevicesType {
        MIO_DEVICE_UNKNOW,
        MIO_DEVICE_LINK,
        MIO_DEVICE_VELO,
        MIO_DEVICE_FUSE,
        MIO_DEVICE_ALPHA2,
        MIO_DEVICE_ALPHA2_OTA,
        MIO_DEVICE_LINK_OTA
    }

    public interface MioDeviceBatteryCallback {
        void OnDeviceBatteryChanged_MIO(int i);
    }

    public interface MioDeviceConnectionCallback {
        void OnBluetoothClosed_MIO(String str, String str2);

        void OnDeviceConnected_MIO(String str, String str2);

        void OnDeviceDisconnected_MIO(String str, String str2);
    }

    public interface MioDeviceHRMCallback {
        void OnDeviceHRMChanged_MIO(int i);
    }

    public interface MioDeviceHRZoneSettingCallback {
        void OnSettingError_MIO(int i);

        void OnSettingSuccess_MIO();
    }

    public interface MioDeviceRSSICallback {
        void OnDeviceRSSIChanged_MIO(int i);
    }

    public MioDeviceInformation GetMioDeviceInformation_MIO() {
        return this.mioDeviceInformation;
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
        this.mioDeviceInformation.setDeviceUID(deviceUID);
    }

    public void setCompanyID(String companyID) {
        this.mioDeviceInformation.setCompanyID(companyID);
    }

    public MioDeviceConnection(String name, String address) {
        this.deviceDFUCallBack = null;
        this.mioDeviceInformation = new MioDeviceInformation();
        this.mContext = null;
        this.bluetoothGatt = null;
        this.isConnected = false;
        this.isDisconnectedByUser = false;
        this.mCharacteristicDFUPacket = null;
        this.mCharacteristicDFUControlPoint = null;
        this.mCharacteristicAlpha2DFUSendPackage = null;
        this.isSleepFullMemory = false;
        this.isReconnect = true;
        this.isHRZoneGet = false;
        this.isNotificationInit = false;
        this.isHRNotificationEnabled = false;
        this.retryConnect = false;
        this.autoRetry = true;
        this.retryCount = 0;
        this.veloLeScanCallback = null;
        this.notifyThread = null;
        this.batteryValue = 0;
        this.thirdBLE = false;
        this.bleIsConnected = false;
        this.isExceptionDisconnection = false;
        this.userStopNotify = false;
        this.timeout = DateUtils.MILLIS_PER_MINUTE;
        this.isConnectBle = false;
        this.isCoonecting = false;
        this.isDebug = false;
        this.workoutRecordIndex = 0;
        this.allSleepRecords = new ArrayList();
        this.isSync = false;
        this.isDeviceUpdate = false;
        this.isEnterDFUMode = false;
        this.updateDeviceType = MIODevicesType.MIO_DEVICE_UNKNOW;
        this.isEndSync = false;
        this.deviceUID = DiffResult.OBJECTS_SAME_STRING;
        this.mConnectionTimerHandler = new Handler();
        this.mHRThread = new C02701();
        this.mSpeendThread = new C02712();
        this.mCadenceThread = new C02723();
        this.mAutoConnTimeoutThread = new C02734();
        this.mConnectionTimeoutThread = new C02745();
        this.numberOfRecord = 0;
        this.recordIndex = 0;
        this.recordSize = 0;
        this.sessionTotal = 0;
        this.dataLength = 0;
        this.sessionDataSize = 0;
        this.recordDataType = 0;
        this.curSessionID = 0;
        this.getCurSessionDataLen = 0;
        this.logDataPos = 0;
        this.recordBuf = null;
        this.tempsessionDataSize = 0;
        this.tempCurSessionID = 0;
        this.tempgetCurSessionDataLen = 0;
        this.resumeDown = false;
        this.resumeDownLoadTask = true;
        this.downLoadType = 0;
        this.bluetoothGattCallback = new C02786();
        this.retryEnableHRZoneMsgRespNotification = true;
        this.retryEnableHRNotification = true;
        this.mioBikeSensorCallBack = null;
        this.mioBikeSensorSetting = null;
        this.enableNotifyBikeSensorData = true;
        this.minLimitTimeOut = 30;
        this.wheelCircumference = 2.07f;
        this.prevRevCount = 0;
        this.prevMeasTime = 0;
        this.prevCrankRevCount = 0;
        this.prevCrankMeasTime = 0;
        this.startRevCount = 0;
        this.inGetFlag = false;
        this.veloAppType = null;
        this.activeBikeNum = null;
        this.veloMemoryState = null;
        this.isCOMBO = false;
        this.speedZeroCount = 0;
        this.cadenceZeroCount = 0;
        this.searchSensorFlag = 0;
        this.searchSensorEndFlag = false;
        this.autoRetrySearchSensor = false;
        this.hasSensorData = false;
        this.mSearchSensorTimeoutThread = new C02797();
        this.tempActiveBikeNum = null;
        this.bleCmdState = false;
        this.cmdType = CMD_TYPE.CMD_TYPE_NONE;
        this.mBluetoothSettingTimeOut = new C02808();
        this.tempAppType = null;
        this.mioDeviceCallBack = null;
        this.needGoOnDownLoadRecord = false;
        this.rType = 0;
        this.getAllRecordFlag = false;
        this.delRecordFlag = false;
        this.mDownWorkoutRecordTimeOut = new C02819();
        this.commonDFUManager = null;
        this.alpha2DFUManager = null;
        Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
        this.deviceName = name;
        this.deviceAddress = address;
        this.thirdBLE = this.mioDeviceInformation.IsThirdBLE(this.deviceName);
    }

    public void setName(String name) {
        this.deviceName = name;
    }

    public void Connect_MIO(Context context) {
        Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
        this.mContext = context.getApplicationContext();
        if (!this.isCoonecting) {
            this.mConnectionTimerHandler.removeCallbacks(this.mBluetoothSettingTimeOut);
            if (this.bluetoothAdapter == null) {
                this.bluetoothAdapter = MioHelper.getBluetoothAdapter(this.mContext);
            }
            if (this.bluetoothAdapter.isEnabled() || false) {
                this.bluetoothAdapter.enable();
                Log.e("Connect_MIO", "Connect_MIO_111");
                MioDeviceManager.GetMioDeviceManager_MIO().DisconnectOtherDevice(this);
                MioSportMsgParserUtil.writeLogtoFile("App", DiffResult.OBJECTS_SAME_STRING, "---------Connect-----deviceAddress=" + this.deviceAddress);
                this.isDisconnectedByUser = false;
                if (this.isConnected) {
                    Log.e("Connect_MIO", "Connect_MIO_121");
                    Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                    if (this.connectionStateCallback != null) {
                        Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                        System.out.println(this.deviceAddress + "-biantao-" + this.deviceUID);
                        this.isCoonecting = false;
                        this.connectionStateCallback.OnDeviceConnected_MIO(this.deviceAddress, this.deviceUID);
                    }
                    Log.e("Connect_MIO", "Connect_MIO_131");
                    return;
                }
                Log.e("Connect_MIO", "Connect_MIO_141");
                this.isCoonecting = true;
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                this.autoRetry = true;
                this.retryCount = 0;
                BluetoothDevice device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);
                this.mConnectionTimerHandler.removeCallbacks(this.mConnectionTimeoutThread);
                if (this.bluetoothGatt != null) {
                    try {
                        Log.e("Connect_MIO", "Connect_MIO1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.e("Connect_MIO", "Connect_MIO2");
                        this.bluetoothGatt.close();
                        Thread.sleep(2000);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    this.bluetoothGatt = null;
                }
                try {
                    this.isConnected = false;
                    this.notifyThread = null;
                    this.isHRNotificationEnabled = false;
                    this.isNotificationInit = false;
                    this.hasSensorData = false;
                    this.isHRZoneGet = false;
                    this.isExceptionDisconnection = false;
                    this.isConnectBle = true;
                    this.mConnectionTimerHandler.postDelayed(this.mConnectionTimeoutThread, this.timeout);
                    Log.e("connectGatt", "connectGatt1");
                    this.bluetoothGatt = device.connectGatt(this.mContext, false, this.bluetoothGattCallback);
                    Utils.refress(this.bluetoothGatt);
                } catch (Exception e22) {
                    e22.printStackTrace();
                }
                Log.e("Connect_MIO", "Connect_MIO_151");
                return;
            }
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            this.isConnected = false;
            if (this.connectionStateCallback != null) {
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                this.isCoonecting = false;
                this.connectionStateCallback.OnBluetoothClosed_MIO(this.deviceAddress, this.deviceUID);
            }
        }
    }

    private void doBluetoothReset(boolean resetBluetooth) {
        new Thread(new AnonymousClass10(resetBluetooth)).start();
    }

    public void ReConnectDisable() {
        this.isReconnect = false;
    }

    public void ReConnectEnable() {
        this.isReconnect = true;
    }

    public void Disconnect_MIO() {
        this.mConnectionTimerHandler.removeCallbacks(this.mSearchSensorTimeoutThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mConnectionTimeoutThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mAutoConnTimeoutThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mHRThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mSpeendThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mCadenceThread);
        this.mConnectionTimerHandler.removeCallbacks(this.mBluetoothSettingTimeOut);
        this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
        this.userStopNotify = false;
        MioSportMsgParserUtil.writeLogtoFile("App", DiffResult.OBJECTS_SAME_STRING, "---------Disconnect-----deviceAddress=" + this.deviceAddress);
        this.isDisconnectedByUser = true;
        this.isExceptionDisconnection = true;
        this.isSync = false;
        if (this.bluetoothGatt != null) {
            try {
                this.bluetoothGatt.close();
            } catch (Exception e) {
                Log.e("Disconnect_MIO", "Disconnect_MIO2");
                e.printStackTrace();
            }
            this.bluetoothGatt = null;
        }
        this.isConnected = false;
        this.bleIsConnected = false;
        if (this.connectionStateCallback != null && !this.userStopNotify) {
            this.userStopNotify = true;
            System.out.println(this.deviceAddress + "-Disconnected-" + this.deviceUID);
            Log.e("Disconnect_MIO", "OnDeviceDisconnected_MIO");
            this.isCoonecting = false;
            this.retryCount = 0;
            this.connectionStateCallback.OnDeviceDisconnected_MIO(this.deviceAddress, this.deviceUID);
        }
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public MioDeviceHRZoneSetting GetHRZoneSetting_MIO() {
        return this.hrZoneSetting;
    }

    public void SetEnterDFUModeCallback(DFUCallbacks callback) {
        this.deviceDFUCallBack = callback;
    }

    public void SetMioDeviceConnectionCallback_MIO(MioDeviceConnectionCallback callback) {
        this.connectionStateCallback = callback;
    }

    public void SetMioDeviceHRMCallback_MIO(MioDeviceHRMCallback callback) {
        this.hrmStateCallback = callback;
    }

    public void SetMioDeviceRSSICallback_MIO(MioDeviceRSSICallback callback) {
        this.rssiStateCallback = callback;
    }

    public void SetMioDeviceHRZoneSettingCallback_MIO(MioDeviceHRZoneSettingCallback callback) {
        this.hrZoneSettingCallback = callback;
    }

    public void SetMioDeviceBatteryCallback_MIO(MioDeviceBatteryCallback callback) {
        this.batteryStateCallback = callback;
    }

    public boolean isHRZoneSupported() {
        return this.mCharacteristicMioSportMsg != null;
    }

    public boolean isLockSupported() {
        return true;
    }

    public boolean SetMioDeviceHRZoneSetting_MIO(MioDeviceHRZoneSetting settings) {
        int i = DEL_RECORD;
        int i2 = 0;
        if (!this.isConnected || this.bleCmdState || this.bluetoothGatt == null) {
            if (this.hrZoneSettingCallback == null) {
                return false;
            }
            this.hrZoneSettingCallback.OnSettingError_MIO(0);
            return false;
        } else if (MioSportMsgParserUtil.checkHRZoneSetting(settings)) {
            this.newMIOHRZoneSettings = settings;
            boolean supportMHR = this.mioDeviceInformation.IsMHRSupported_MIO(this.deviceName);
            int len = ENABLE_STREAM_MODE;
            byte dataLen = (byte) 9;
            if (supportMHR) {
                len = RESET_ADL_TODAY_DATA;
                dataLen = (byte) 10;
            } else {
                System.out.println("------------------>not Support Set MHR");
            }
            int ledLimit = settings.getLEDAlertCycle();
            if (ledLimit < 0) {
                settings.setLEDAlertCycle(0);
            }
            if (ledLimit > GET_RECORD_ADL_D_F) {
                settings.setLEDAlertCycle(GET_RECORD_ADL_D_F);
            }
            byte[] value = new byte[len];
            value[0] = dataLen;
            value[DEL_RECORD] = (byte) 2;
            if (((byte) (settings.GetHRZoneType_MIO() & DEL_RECORD)) != (byte) 1) {
                i = 0;
            }
            value[GET_RECORD_ADL_T] = (byte) i;
            byte b = value[GET_RECORD_ADL_T];
            if (settings.IsAntPlusTxEnabled_MIO()) {
                i = GET_RECORD_ADL_T;
            } else {
                i = 0;
            }
            value[GET_RECORD_ADL_T] = (byte) (i | b);
            b = value[GET_RECORD_ADL_T];
            if (settings.isHRAlertAudioEnabled_MIO()) {
                i = GET_RECORD_ADL_D_N;
            } else {
                i = 0;
            }
            value[GET_RECORD_ADL_T] = (byte) (i | b);
            b = value[GET_RECORD_ADL_T];
            if (settings.isHRAlertLEDEnabled_MIO()) {
                i = 8;
            } else {
                i = 0;
            }
            value[GET_RECORD_ADL_T] = (byte) (i | b);
            b = value[GET_RECORD_ADL_T];
            if (settings.isHRAlertVibroEnabled_MIO()) {
                i = GET_VELO_RECORD_N;
            } else {
                i = 0;
            }
            value[GET_RECORD_ADL_T] = (byte) (i | b);
            byte b2 = value[GET_RECORD_ADL_T];
            if (settings.isHijackEnabled_MIO()) {
                i2 = 32;
            }
            value[GET_RECORD_ADL_T] = (byte) (b2 | i2);
            value[GET_RECORD_ADL_T] = (byte) (value[GET_RECORD_ADL_T] | ((settings.getLEDAlertCycle() << GET_RECORD_WO_N) & Opcodes.CHECKCAST));
            value[GET_RECORD_ADL_D_F] = (byte) settings.GetHR5ZoneTargetZone_MIO();
            value[GET_RECORD_ADL_D_N] = (byte) settings.GetHR5ZoneLimit0_MIO();
            value[GET_RECORD_WO_T] = (byte) settings.GetHR5ZoneLimit1_MIO();
            value[GET_RECORD_WO_N] = (byte) settings.GetHR5ZoneLimit2_MIO();
            value[7] = (byte) settings.GetHR5ZoneLimit3_MIO();
            value[8] = (byte) settings.GetHR5ZoneLimit4_MIO();
            value[9] = (byte) settings.GetHR3ZoneLowLimit_MIO();
            value[10] = (byte) settings.GetHR3ZoneUpperLimit_MIO();
            if (supportMHR) {
                int mhr = settings.getMaxHeartRate();
                if (mhr < 80) {
                    mhr = 80;
                }
                if (mhr > 220) {
                    mhr = 220;
                }
                value[ENABLE_STREAM_MODE] = (byte) mhr;
            }
            return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_HR_SET, value);
        } else if (this.hrZoneSettingCallback == null) {
            return false;
        } else {
            this.hrZoneSettingCallback.OnSettingError_MIO(GET_RECORD_ADL_T);
            return false;
        }
    }

    private void parserRecord(byte[] raw) {
        byte msgCode = raw[GET_RECORD_ADL_D_F];
        Log.e("parserRecord", "rType=" + this.rType + "---msgCode=" + msgCode);
        if (msgCode != MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
            Log.e("parserRecord", "parserRecord7-" + raw[GET_RECORD_ADL_T]);
            if (raw[GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_GET || raw[GET_RECORD_ADL_T] == MioHelper.LINK2_MEM_RECORD_GET) {
                Log.e("parserRecord", "parserRecord8");
                if (this.mioDeviceCallBack != null) {
                    Log.e("parserRecord", "rType=" + this.rType + "---msgCode=" + msgCode);
                    if (this.rType == GET_SLEEP_RECORD_N) {
                        Log.e("rType120", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, msgCode);
                    } else if (this.rType == GET_SLEEP_RECORD_T) {
                        Log.e("rType121", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, msgCode);
                    } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                        Log.e("rType122", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, msgCode);
                    } else if (this.rType == GET_RECORD_ADL_T) {
                        Log.e("rType6", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetTodayADLRecord(null, msgCode);
                    } else if (this.rType == GET_RECORD_ADL_D_F || this.rType == GET_RECORD_ADL_D_N) {
                        Log.e("rType7", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetRecordOfDailyADL(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                    } else if (this.rType == GET_RECORD_WO_N) {
                        Log.e("rType8", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetWorkoutRecord(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                    } else if (this.rType == GET_RECORD_WO_T) {
                        Log.e("rType9", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetTotalNumbersOfWorkoutRecord((short) 0, msgCode);
                    } else if (this.rType == GET_ALPHA2_RECORD_N) {
                        Log.e("rType10", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetAlpha2Record(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                    }
                }
                this.getAllRecordFlag = false;
                this.delRecordFlag = false;
            }
        } else if (raw[GET_RECORD_ADL_T] == MioHelper.LINK_MEM_RECORD_GET || raw[GET_RECORD_ADL_T] == MioHelper.LINK2_MEM_RECORD_GET) {
            Log.e("parserRecord", "needGoOnDownLoadRecord=" + this.needGoOnDownLoadRecord + "   curSessionID=" + this.curSessionID + " sessionDataSize=" + this.sessionDataSize + " getCurSessionDataLen=" + this.getCurSessionDataLen);
            if (!this.needGoOnDownLoadRecord || this.curSessionID <= DEL_RECORD || this.sessionDataSize <= 0 || this.getCurSessionDataLen <= 0) {
                this.recordDataType = raw[GET_RECORD_ADL_D_N];
                this.recordBuf = null;
                this.numberOfRecord = (raw[GET_RECORD_WO_T] & 255) + ((raw[GET_RECORD_WO_N] & 255) << 8);
                this.recordIndex = (raw[7] & 255) + ((raw[8] & 255) << 8);
                this.recordSize = (((raw[9] & 255) + ((raw[10] & 255) << 8)) + ((raw[ENABLE_STREAM_MODE] & 255) << GET_VELO_RECORD_N)) + ((raw[RESET_ADL_TODAY_DATA] & 255) << 24);
                this.sessionTotal = (raw[AIRPLANE_MODE_ENABLE] & 255) + ((raw[GET_ALPHA2_RECORD_N] & 255) << 8);
                this.getCurSessionDataLen = 0;
                Log.e("reocrd:", "recordSize=" + this.recordSize + "   recordIndex=" + this.recordIndex + " sessionTotal=" + this.sessionTotal + " numberOfRecord=" + this.numberOfRecord);
                if (this.numberOfRecord <= 0) {
                    Log.e("parserRecord", "parserRecord1");
                    if (this.mioDeviceCallBack != null) {
                        Log.e("parserRecord", "parserRecord2");
                        if (this.rType == GET_SLEEP_RECORD_N) {
                            Log.e("rType110", "rType=" + this.rType);
                            this.rType = 0;
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!MioDeviceConnection.this.GetCurHourSleepRecord_MIO() && MioDeviceConnection.this.mioDeviceCallBack != null) {
                                        MioDeviceConnection.this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
                                    }
                                }
                            }).start();
                        } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                            Log.e("rType112", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, msgCode);
                        } else if (this.rType == GET_RECORD_ADL_T) {
                            Log.e("rType1", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetTodayADLRecord(null, msgCode);
                        } else if (this.rType == GET_RECORD_ADL_D_F || this.rType == GET_RECORD_ADL_D_N) {
                            Log.e("rType2", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetRecordOfDailyADL(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                        } else if (this.rType == GET_RECORD_WO_N) {
                            Log.e("rType3", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetWorkoutRecord(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                        } else if (this.rType == GET_RECORD_WO_T) {
                            Log.e("rType4", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetTotalNumbersOfWorkoutRecord((short) this.numberOfRecord, msgCode);
                        } else if (this.rType == GET_ALPHA2_RECORD_N) {
                            Log.e("rType5", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetAlpha2Record(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                        }
                    }
                    this.getAllRecordFlag = false;
                    this.delRecordFlag = false;
                } else {
                    Log.e("parserRecord", "parserRecord3");
                    if (this.rType == GET_SLEEP_RECORD_N) {
                        Log.e("GetSession_MIO", "GetSession_MIO10");
                        GetSession_MIO(DEL_RECORD);
                    } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                        Log.e("GetSession_MIO", "GetSession_MIO11");
                    }
                    this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                    this.mConnectionTimerHandler.postDelayed(this.mDownWorkoutRecordTimeOut, 50000);
                }
                if (this.rType == GET_RECORD_WO_T) {
                    Log.e("parserRecord", "parserRecord4");
                    this.rType = 0;
                    if (this.mioDeviceCallBack != null) {
                        Log.e("parserRecord", "parserRecord5");
                        this.mioDeviceCallBack.onGetTotalNumbersOfWorkoutRecord((short) this.numberOfRecord, msgCode);
                    }
                } else if (this.rType == GET_SLEEP_RECORD_T) {
                    Log.e("rType111", "rType=" + this.rType);
                    this.rType = 0;
                }
            } else if (this.rType == GET_SLEEP_RECORD_N) {
                new Thread(new Runnable() {
                    public void run() {
                        Log.e("GetSession_MIO", "1");
                        MioDeviceConnection.this.GetSession_MIO(MioDeviceConnection.this.curSessionID);
                    }
                }).start();
                this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                this.mConnectionTimerHandler.postDelayed(this.mDownWorkoutRecordTimeOut, 50000);
                return;
            } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                new Thread(new Runnable() {
                    public void run() {
                        Log.e("GetSession_MIO", "2");
                        MioDeviceConnection.this.GetSession_MIO(MioDeviceConnection.this.curSessionID);
                    }
                }).start();
                this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                this.mConnectionTimerHandler.postDelayed(this.mDownWorkoutRecordTimeOut, 50000);
                return;
            } else {
                return;
            }
        } else if (raw[GET_RECORD_ADL_T] == MioHelper.LINK_MEM_SESSION_GET || raw[GET_RECORD_ADL_T] == MioHelper.LINK2_MEM_NEXT_PACKET_GET) {
            Log.e("parserRecord", "parserRecord6");
            this.curSessionID = (raw[GET_RECORD_ADL_D_N] & 255) + ((raw[GET_RECORD_WO_T] & 255) << 8);
            this.sessionDataSize = (raw[GET_RECORD_WO_N] & 255) + ((raw[7] & 255) << 8);
            Log.e("parserRecord", "curSessionID=" + this.curSessionID + " sessionDataSize=" + this.sessionDataSize);
            this.getCurSessionDataLen = 0;
        }
        if (MioHelper.LINK2_MEM_RECORD_DELETE == raw[GET_RECORD_ADL_T]) {
            Log.e("parserRecord", "parserRecord9");
            if (this.mioDeviceCallBack != null) {
                Log.e("parserRecord", "parserRecord10");
                this.mioDeviceCallBack.onDeleteAlpha2Record(this.opType, msgCode);
            }
            Log.e("rType11", "rType=" + this.rType);
            this.rType = 0;
        }
    }

    private void parserVeloRecord(byte[] raw) {
        Log.e("parserVeloRecord", MioSportMsgParserUtil.printRaw("parserVeloRecord", raw));
        byte msgCode = raw[GET_RECORD_ADL_D_F];
        if (msgCode != MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
            Log.e("parserVeloRecord", "parserVeloRecord5");
            if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_VELO_RECORD_GET) {
                Log.e("parserVeloRecord", "parserVeloRecord6");
                this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                if (this.mioDeviceCallBack != null) {
                    Log.e("parserVeloRecord", "rType=" + this.rType + "---msgCode=" + msgCode);
                    if (this.rType == GET_VELO_RECORD_N) {
                        Log.e("rType13", "rType=" + this.rType);
                        this.rType = 0;
                        this.mioDeviceCallBack.onGetVeloRecord(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                    }
                }
                this.getAllRecordFlag = false;
                this.delRecordFlag = false;
                return;
            }
            byte b = raw[GET_RECORD_ADL_T];
            b = MioHelper.MSG_VELO_MEM_SESSION_GET;
        } else if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_VELO_RECORD_GET) {
            if (!this.needGoOnDownLoadRecord || this.curSessionID <= DEL_RECORD || this.sessionDataSize <= 0 || this.getCurSessionDataLen <= 0) {
                this.recordDataType = raw[GET_RECORD_ADL_T];
                this.recordBuf = null;
                this.numberOfRecord = (raw[GET_RECORD_WO_T] & 255) + ((raw[GET_RECORD_WO_N] & 255) << 8);
                this.recordIndex = (raw[7] & 255) + ((raw[8] & 255) << 8);
                this.sessionTotal = (raw[AIRPLANE_MODE_ENABLE] & 255) + ((raw[GET_ALPHA2_RECORD_N] & 255) << 8);
                this.recordSize = (((raw[9] & 255) + ((raw[10] & 255) << 8)) + ((raw[ENABLE_STREAM_MODE] & 255) << GET_VELO_RECORD_N)) + ((raw[RESET_ADL_TODAY_DATA] & 255) << 24);
                this.getCurSessionDataLen = 0;
                Log.e("parserVeloRecord", "recordSize=" + this.recordSize + "   recordIndex=" + this.recordIndex + " numberOfRecord=" + this.numberOfRecord + " sessionTotal=" + this.sessionTotal + " rType=" + this.rType + "---msgCode=" + msgCode);
                if (this.numberOfRecord <= 0) {
                    Log.e("parserVeloRecord", "parserVeloRecord1");
                    if (this.mioDeviceCallBack != null) {
                        Log.e("parserVeloRecord", "parserVeloRecord2");
                        if (this.rType == GET_VELO_RECORD_N) {
                            Log.e("rType12", "rType=" + this.rType);
                            this.rType = 0;
                            this.mioDeviceCallBack.onGetVeloRecord(null, (short) this.numberOfRecord, (short) this.recordIndex, msgCode);
                        }
                    }
                    this.getAllRecordFlag = false;
                    this.delRecordFlag = false;
                    return;
                }
                Log.e("parserVeloRecord", "parserVeloRecord3");
                this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                this.mConnectionTimerHandler.postDelayed(this.mDownWorkoutRecordTimeOut, 50000);
            }
        } else if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_VELO_MEM_SESSION_GET) {
            Log.e("parserVeloRecord", "parserVeloRecord4");
            this.curSessionID = (raw[GET_RECORD_ADL_D_N] & 255) + ((raw[GET_RECORD_WO_T] & 255) << 8);
            this.sessionDataSize = (raw[GET_RECORD_WO_N] & 255) + ((raw[7] & 255) << 8);
            this.getCurSessionDataLen = 0;
            Log.e("parserVeloRecord", "curSessionID=" + this.curSessionID + " sessionDataSize=" + this.sessionDataSize);
            this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
            this.mConnectionTimerHandler.postDelayed(this.mDownWorkoutRecordTimeOut, 50000);
        }
    }

    private void doConnectSuccess() {
        Log.e("doConnectSuccess", "doConnectSuccess---rType=" + this.rType + "--isSync=" + this.isSync);
        if (this.isSync) {
            this.rType = 0;
            requestEnableStreamModel();
        }
        if (!this.isNotificationInit) {
            this.isNotificationInit = true;
            this.retryCount = 0;
            this.mConnectionTimerHandler.removeCallbacks(this.mConnectionTimeoutThread);
            this.mConnectionTimerHandler.removeCallbacks(this.mAutoConnTimeoutThread);
            Log.e("startDataNotify", "startDataNotify--1");
            startDataNotify();
        }
    }

    private void discoverServices() {
        Log.e("discoverServices", "discoverServices--1");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.bluetoothGatt != null) {
                    Log.e("discoverServices", "discoverServices--2");
                    MioDeviceConnection.this.bluetoothGatt.discoverServices();
                    MioSportMsgParserUtil.writeLogtoFile("connected", "discoverServices----", DiffResult.OBJECTS_SAME_STRING);
                }
            }
        }).start();
    }

    private void DoReConnect() {
        if (!this.isReconnect || this.isDisconnectedByUser || this.retryCount >= GET_RECORD_ADL_D_F) {
            Disconnect_MIO();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(7000);
                        MioSportMsgParserUtil.writeLogtoFile("ble disconnected", "retry connecting---------", DiffResult.OBJECTS_SAME_STRING);
                        if (!MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.isReconnect && MioDeviceConnection.this.mContext != null) {
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                            MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                            MioDeviceConnection.this.isConnected = false;
                            MioDeviceConnection.this.notifyThread = null;
                            MioDeviceConnection.this.isHRNotificationEnabled = false;
                            MioDeviceConnection.this.isNotificationInit = false;
                            MioDeviceConnection.this.isHRZoneGet = false;
                            MioDeviceConnection.this.hasSensorData = false;
                            MioDeviceConnection.this.Connect_MIO(MioDeviceConnection.this.mContext);
                            MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mAutoConnTimeoutThread, MioDeviceConnection.this.timeout);
                            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void handleVeloReconnect() {
        if (!this.isDisconnectedByUser) {
            new Thread(new Runnable() {

                /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceConnection.16.1 */
                class C02691 implements LeScanCallback {
                    C02691() {
                    }

                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                        String address = device.getAddress();
                        String name = device.getName();
                        Log.e("onLeScan", new StringBuilder(String.valueOf(address)).append("-------").append(name).toString());
                        if (name == null) {
                            name = new MioBleAdHelper(scanRecord).getDeviceName();
                        }
                        if (name != null && name.contains("VELO")) {
                            if (MioDeviceConnection.this.deviceUID.compareToIgnoreCase(MioHelper.paserVeloSerial(scanRecord)) == 0) {
                                System.out.println("velo-retry-connecting");
                                MioSportMsgParserUtil.writeLogtoFile("velo-retry-connecting", "Address", "oldAddress=" + MioDeviceConnection.this.deviceAddress + "     newAddress= " + address);
                                System.out.println("oldAddress=" + MioDeviceConnection.this.deviceAddress + "     newAddress= " + address);
                                if (!address.equals(MioDeviceConnection.this.deviceAddress)) {
                                    MioDeviceManager.GetMioDeviceManager_MIO().changeDeviceAdress(MioDeviceConnection.this, address, MioDeviceConnection.this.deviceAddress);
                                }
                                MioDeviceConnection.this.deviceAddress = address;
                                MioDeviceConnection.this.bluetoothAdapter.stopLeScan(MioDeviceConnection.this.veloLeScanCallback);
                            }
                        }
                    }
                }

                public void run() {
                    if (MioDeviceConnection.this.mContext != null && !MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.bluetoothAdapter != null && MioDeviceConnection.this.bluetoothAdapter.isEnabled()) {
                        MioDeviceConnection.this.veloLeScanCallback = new C02691();
                        MioDeviceConnection.this.bluetoothAdapter.startLeScan(MioDeviceConnection.this.veloLeScanCallback);
                        try {
                            Thread.sleep(5000);
                            if (MioDeviceConnection.this.bluetoothAdapter != null) {
                                MioDeviceConnection.this.bluetoothAdapter.stopLeScan(MioDeviceConnection.this.veloLeScanCallback);
                            }
                            if (!MioDeviceConnection.this.isDisconnectedByUser && MioDeviceConnection.this.isReconnect) {
                                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mConnectionTimeoutThread);
                                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mAutoConnTimeoutThread);
                                MioDeviceConnection.this.isConnected = false;
                                MioDeviceConnection.this.notifyThread = null;
                                MioDeviceConnection.this.isHRNotificationEnabled = false;
                                MioDeviceConnection.this.isNotificationInit = false;
                                MioDeviceConnection.this.hasSensorData = false;
                                MioDeviceConnection.this.isHRZoneGet = false;
                                MioDeviceConnection.this.Connect_MIO(MioDeviceConnection.this.mContext);
                                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mAutoConnTimeoutThread, MioDeviceConnection.this.timeout);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    private void enableHRZoneMsgRespNotification() {
        if (this.mCharacteristicMioSportMsgResp != null && this.bluetoothGatt != null && this.bleIsConnected) {
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicMioSportMsgResp, true);
            BluetoothGattDescriptor descriptor = this.mCharacteristicMioSportMsgResp.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Log.e("writeDescriptor", "writeDescriptor1");
            if (this.bluetoothGatt.writeDescriptor(descriptor)) {
                this.retryEnableHRZoneMsgRespNotification = true;
            } else if (this.retryEnableHRZoneMsgRespNotification) {
                this.retryEnableHRZoneMsgRespNotification = false;
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                            MioDeviceConnection.this.enableHRZoneMsgRespNotification();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public boolean enableHRNotification(boolean enableHRNotification) {
        if (enableHRNotification == this.isHRNotificationEnabled || this.bluetoothGatt == null || this.mCharacteristicHeartRate == null || !this.bleIsConnected) {
            return false;
        }
        boolean setCharacteristicNotification = this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicHeartRate, enableHRNotification);
        Log.e("enableHRNotification", "enableHRNotification------------------>setCharacteristicNotification:" + enableHRNotification);
        if (!setCharacteristicNotification) {
            setCharacteristicNotification = this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicHeartRate, enableHRNotification);
            Log.e("enableHRNotification", "enableHRNotification------------------>setCharacteristicNotification2:" + enableHRNotification);
        }
        BluetoothGattDescriptor descriptor = this.mCharacteristicHeartRate.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
        if (enableHRNotification) {
            if (descriptor.getValue() == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
                return true;
            }
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else if (descriptor.getValue() == BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) {
            return true;
        } else {
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        Log.e("writeDescriptor", "writeDescriptor2");
        boolean writeDescriptor = this.bluetoothGatt.writeDescriptor(descriptor);
        Log.e("enableHRNotification", "enableHRNotification------------------>writeDescriptor:" + writeDescriptor + "---->setValue:" + descriptor.getValue());
        if (writeDescriptor) {
            this.retryEnableHRNotification = true;
            this.isHRNotificationEnabled = enableHRNotification;
        } else if (this.retryEnableHRNotification) {
            this.retryEnableHRNotification = false;
            new Thread(new AnonymousClass18(enableHRNotification)).start();
        }
        return true;
    }

    private void enableBatteryNotification() {
        if (this.bluetoothGatt != null && this.mCharacteristicBatteryLevel != null) {
            this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicBatteryLevel, true);
            BluetoothGattDescriptor descriptor = this.mCharacteristicBatteryLevel.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
            if (descriptor.getValue() != BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                Log.e("writeDescriptor", "writeDescriptor3");
                this.bluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    private void requestHRZoneParameters() {
        if (this.bluetoothGatt != null || !this.bleCmdState) {
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            byte[] cmd = new byte[GET_RECORD_ADL_T];
            cmd[DEL_RECORD] = (byte) 1;
            commonBluetoothSetting(CMD_TYPE.CMD_TYPE_HR_GET, cmd);
        }
    }

    private void requestEnableStreamModel() {
        if (this.bluetoothGatt != null || !this.bleCmdState) {
            byte[] setCommand = new byte[GET_RECORD_ADL_D_F];
            setCommand[0] = (byte) 1;
            setCommand[DEL_RECORD] = MioHelper.MSG_RUN_CMD;
            setCommand[GET_RECORD_ADL_T] = MioHelper.RUN_CMD_SME;
            commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RUN_CMD, setCommand);
        }
    }

    public void readBatteryLevel() {
        doReadBatteryLevel();
    }

    private boolean doReadBatteryLevel() {
        if (this.bluetoothGatt == null) {
            return false;
        }
        if (this.batteryStateCallback != null && this.batteryValue > 0) {
            this.batteryStateCallback.OnDeviceBatteryChanged_MIO(this.batteryValue);
            return false;
        } else if (this.mCharacteristicBatteryLevel != null && this.bluetoothGatt != null) {
            Log.e("readCharacteristic", "readCharacteristic122");
            this.bluetoothGatt.readCharacteristic(this.mCharacteristicBatteryLevel);
            return true;
        } else if (!this.deviceName.contains("ALPHA2") || this.deviceName.contains("ALPHA2_OTA")) {
            return false;
        } else {
            readAlpha2BatteryLevel_MIO();
            return true;
        }
    }

    private void startDataNotify() {
        if (this.notifyThread == null) {
            this.notifyThread = new Thread(new Runnable() {
                public void run() {
                    if (!MioDeviceConnection.this.isConnected) {
                        MioDeviceConnection.this.enableHRNotification(true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if (MioDeviceConnection.this.deviceName.contains("VELO")) {
                            if (!(!MioDeviceConnection.this.enableNotifyBikeSensorData || MioDeviceConnection.this.bluetoothGatt == null || MioDeviceConnection.this.isEnterDFUMode)) {
                                MioDeviceConnection.this.enableNotificationBikeSensorData();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MioDeviceConnection.this.enableRecordMsgRespNotification();
                            }
                        } else if (MioDeviceConnection.this.deviceName != null && ((MioDeviceConnection.this.deviceName.contains("FUSE") || MioDeviceConnection.this.deviceName.contains("ALPHA2")) && !MioDeviceConnection.this.deviceName.contains("ALPHA2_OTA"))) {
                            Log.e("enRecordMsgRespNotif", "enableRecordMsgRespNotification=" + MioDeviceConnection.this.deviceName);
                            MioDeviceConnection.this.enableRecordMsgRespNotification();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                            if (!MioDeviceConnection.this.isEnterDFUMode) {
                                MioDeviceConnection.this.enableSenserDataRespNotification();
                            }
                        }
                        if (MioDeviceConnection.this.doReadBatteryLevel()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e12) {
                                e12.printStackTrace();
                            }
                        }
                        MioDeviceConnection.this.enableBatteryNotification();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e122) {
                            e122.printStackTrace();
                        }
                        Log.e("checkDLRecordFinished", "checkDLRecordFinished1");
                        if (!MioDeviceConnection.this.isDisconnectedByUser) {
                            MioDeviceConnection.this.isConnected = true;
                            Log.e("checkDLRecordFinished", "checkDLRecordFinished2");
                            boolean ret = MioDeviceConnection.this.checkDLRecordFinished();
                            MioDeviceConnection.this.resumeDown = false;
                            if (MioDeviceConnection.this.connectionStateCallback != null) {
                                System.out.println(new StringBuilder(String.valueOf(MioDeviceConnection.this.deviceAddress)).append("-biantao99999-").append(MioDeviceConnection.this.deviceUID).toString());
                                MioSportMsgParserUtil.writeLogtoFile("ble ", "----------sdk----ble-----Connected---------", DiffResult.OBJECTS_SAME_STRING);
                                MioDeviceConnection.this.isCoonecting = false;
                                MioDeviceConnection.this.connectionStateCallback.OnDeviceConnected_MIO(MioDeviceConnection.this.deviceAddress, MioDeviceConnection.this.deviceUID);
                            }
                            Log.e("checkDLRecordFinished", "mioDeviceCallBack+ret=" + ret);
                            if (MioDeviceConnection.this.mioDeviceCallBack != null && ret) {
                                Log.e("checkDLRecordFinished", "ret=" + ret);
                                MioDeviceConnection.this.mioDeviceCallBack.onResumeDownLoadTask();
                            }
                        }
                    }
                }
            });
        }
        this.notifyThread.start();
    }

    private boolean checkDLRecordFinished() {
        if (this.recordBuf != null) {
            Log.e("checkDLRecordFinished", "recordIndex=" + this.recordIndex + " numberOfRecord=" + this.numberOfRecord + " dataLength=" + this.dataLength + " recordBuf.length=" + this.recordBuf.length);
            if (this.recordIndex < this.numberOfRecord || this.dataLength < this.recordBuf.length) {
                Log.e("checkDLRecordFinished", "rType=" + this.rType + " resumeDown=" + this.resumeDown + " resumeDownLoadTask=" + this.resumeDownLoadTask);
                if ((this.rType == GET_SLEEP_RECORD_N || this.rType == GET_SLEEP_RECORD_N || this.rType == GET_RECORD_WO_N || this.rType == GET_VELO_RECORD_N || this.rType == GET_ALPHA2_RECORD_N) && this.resumeDown && this.resumeDownLoadTask) {
                    if (this.getCurSessionDataLen > 0 && this.getCurSessionDataLen < this.sessionDataSize) {
                        this.dataLength -= this.getCurSessionDataLen;
                    }
                    if (this.sessionTotal > this.curSessionID && this.getCurSessionDataLen >= this.sessionDataSize && this.sessionDataSize > 0) {
                        this.curSessionID += DEL_RECORD;
                    }
                    this.downLoadType = this.rType;
                    Log.e("checkDLRecordFinished", "downLoadType=" + this.downLoadType);
                    this.rType = 0;
                    return true;
                }
                Log.e("rType21", "rType=" + this.rType);
                this.rType = 0;
            } else {
                Log.e("rType20", "rType=" + this.rType);
                this.rType = 0;
            }
        } else {
            Log.e("rType22", "rType=" + this.rType);
            this.rType = 0;
        }
        this.downLoadType = 0;
        return false;
    }

    public boolean resumeDownLoadRecord() {
        if (this.downLoadType != GET_RECORD_WO_N && this.downLoadType != GET_VELO_RECORD_N && this.downLoadType != GET_SLEEP_RECORD_N && this.downLoadType != GET_ALPHA2_RECORD_N) {
            return false;
        }
        this.rType = this.downLoadType;
        this.needGoOnDownLoadRecord = true;
        return SendRunCmd_MIO(RUN_CMD.CMD_StreamModeEnable, false);
    }

    private void doResumeDownLoadRecord() {
        Log.e("doResumeDownLoadRecord", "rType=" + this.rType + " recordIndex=" + this.recordIndex + " numberOfRecord=" + this.numberOfRecord);
        if (this.recordIndex > this.numberOfRecord) {
            return;
        }
        if (GET_ALPHA2_RECORD_N == this.rType) {
            doGetAlpha2Record_MIO(this.recordIndex);
        } else if (GET_VELO_RECORD_N == this.rType) {
            doGetVeloRecord_MIO(this.recordIndex);
        } else if (GET_SLEEP_RECORD_N == this.rType) {
            doGetSleepRecord_MIO(this.recordIndex);
        } else {
            doGetWorkoutRecord_MIO(this.recordIndex);
        }
    }

    private void doResumeDownLoadSession() {
        if (GET_ALPHA2_RECORD_N == this.rType) {
            if (this.curSessionID == GET_RECORD_ADL_T) {
                this.logDataPos = this.dataLength;
            }
            GetRecordPacket_MIO(this.curSessionID);
        } else if (GET_VELO_RECORD_N == this.rType) {
            GetVeloSession_MIO(this.curSessionID);
        } else {
            Log.e("GetSession_MIO", "4");
            GetSession_MIO(this.curSessionID);
        }
    }

    private void doMsgResponseResult(byte[] raw) {
        if (raw == null || raw.length <= 0 || raw[0] <= 0) {
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            Log.d("Modified", "raw[0] <= 0 not pass");
        } else if (raw[DEL_RECORD] == MioHelper.MSG_ID_RESPONSE && raw[GET_RECORD_ADL_T] == MioHelper.MSG_ID_LINK1_ZONE_SETTINGS_SET) {
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            if (raw[GET_RECORD_ADL_D_F] == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                this.hrZoneSetting = this.newMIOHRZoneSettings;
                if (this.hrZoneSettingCallback != null) {
                    this.hrZoneSettingCallback.OnSettingSuccess_MIO();
                    return;
                }
                return;
            }
            Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
            if (this.hrZoneSettingCallback != null) {
                this.hrZoneSettingCallback.OnSettingError_MIO(DEL_RECORD);
            }
        } else if (raw[DEL_RECORD] == MioHelper.MSG_ID_RESPONSE && raw[GET_RECORD_ADL_T] == MioHelper.MSG_ID_LINK1_ZONE_SETTINGS_GET) {
            MioSportMsgParserUtil.printRaw("ZONE_SETTINGS_GET", raw);
            if (raw[GET_RECORD_ADL_D_F] == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                this.isHRZoneGet = true;
                Log.d("m9d",DiffResult.OBJECTS_SAME_STRING);
                this.hrZoneSetting = parseMIOHRZoneSettings(raw);
            }
            if (this.deviceName.contains("VELO")) {
                GetVeloWorkMode_MIO();
                return;
            }
            Log.e("doMsgResponseResult", "doConnectSuccess");
            doConnectSuccess();
        }
    }

    private MioDeviceHRZoneSetting parseMIOHRZoneSettings(byte[] value) {
        int i;
        boolean z;
        boolean z2 = true;
        MioDeviceHRZoneSetting hrzone = new MioDeviceHRZoneSetting();
        if ((value[GET_RECORD_ADL_D_N] & DEL_RECORD) == DEL_RECORD) {
            i = DEL_RECORD;
        } else {
            i = 0;
        }
        hrzone.SetHRZoneType_MIO(i);
        if ((value[GET_RECORD_ADL_D_N] & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
            z = true;
        } else {
            z = false;
        }
        hrzone.SetAntPlusTx_MIO(z);
        if ((value[GET_RECORD_ADL_D_N] & GET_RECORD_ADL_D_N) == GET_RECORD_ADL_D_N) {
            z = true;
        } else {
            z = false;
        }
        hrzone.setHRAlertAudioEnable_MIO(z);
        if ((value[GET_RECORD_ADL_D_N] & 8) == 8) {
            z = true;
        } else {
            z = false;
        }
        hrzone.setHRAlertLEDEnable_MIO(z);
        if ((value[GET_RECORD_ADL_D_N] & GET_VELO_RECORD_N) == GET_VELO_RECORD_N) {
            z = true;
        } else {
            z = false;
        }
        hrzone.setHRAlertVibroEnable_MIO(z);
        if ((value[GET_RECORD_ADL_D_N] & 32) != 32) {
            z2 = false;
        }
        hrzone.setHijackEnable_MIO(z2);
        hrzone.setLEDAlertCycle((value[GET_RECORD_ADL_D_N] & Opcodes.CHECKCAST) >> GET_RECORD_WO_N);
        int tmp = value[GET_RECORD_WO_T] & 255;
        hrzone.SetHR5ZoneTargetZone_MIO(tmp == 0 ? 0 : tmp - 1);
        hrzone.SetHR5ZoneLimit0_MIO(value[GET_RECORD_WO_N] & 255);
        hrzone.SetHR5ZoneLimit1_MIO(value[7] & 255);
        hrzone.SetHR5ZoneLimit2_MIO(value[8] & 255);
        hrzone.SetHR5ZoneLimit3_MIO(value[9] & 255);
        hrzone.SetHR5ZoneLimit4_MIO(value[10] & 255);
        hrzone.SetHR3ZoneLowLimit_MIO(value[ENABLE_STREAM_MODE] & 255);
        hrzone.SetHR3ZoneUpperLimit_MIO(value[RESET_ADL_TODAY_DATA] & 255);
        if (value.length > AIRPLANE_MODE_ENABLE) {
            hrzone.setMaxHeartRate(value[AIRPLANE_MODE_ENABLE] & 255);
        }
        return hrzone;
    }

    public VeloMemoryState getVeloMemoryState() {
        if (this.veloMemoryState != null) {
            return this.veloMemoryState;
        }
        return null;
    }

    public VeloAppType getAppType() {
        if (this.veloAppType != null) {
            return this.veloAppType;
        }
        return null;
    }

    public BikeNum getActiveBikeNum() {
        if (this.activeBikeNum != null) {
            return this.activeBikeNum;
        }
        return null;
    }

    private void enableNotificationBikeSensorData() {
        if (this.characteristicSpeedAndCadence != null || this.bluetoothGatt != null) {
            this.bluetoothGatt.setCharacteristicNotification(this.characteristicSpeedAndCadence, this.enableNotifyBikeSensorData);
            BluetoothGattDescriptor descriptor = this.characteristicSpeedAndCadence.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
            if (this.enableNotifyBikeSensorData) {
                if (descriptor.getValue() != BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    return;
                }
            } else if (descriptor.getValue() != BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE) {
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            } else {
                return;
            }
            Log.e("writeDescriptor", "writeDescriptor4");
            this.bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    private void parserBikeSensorData(byte[] raw) {
        if (raw != null && raw.length > 0 && this.enableNotifyBikeSensorData) {
            this.hasSensorData = true;
            byte flag = raw[0];
            if (flag == (byte) 1 && raw.length > GET_RECORD_WO_N) {
                if (this.isCOMBO) {
                    resetSensorData();
                }
                this.mConnectionTimerHandler.removeCallbacks(this.mSpeendThread);
                this.mConnectionTimerHandler.postDelayed(this.mSpeendThread, 5000);
                handleBikeSpeedData(raw, false);
                this.isCOMBO = false;
            } else if (flag == GET_RECORD_ADL_T && raw.length > GET_RECORD_ADL_D_N) {
                if (this.isCOMBO) {
                    resetSensorData();
                }
                this.mConnectionTimerHandler.removeCallbacks(this.mCadenceThread);
                this.mConnectionTimerHandler.postDelayed(this.mCadenceThread, 5000);
                handleBikeCadenceData(raw, false);
                this.isCOMBO = false;
            } else if (flag == GET_RECORD_ADL_D_F && raw.length > 10) {
                if (!this.isCOMBO) {
                    resetSensorData();
                }
                this.isCOMBO = true;
                this.mConnectionTimerHandler.removeCallbacks(this.mSpeendThread);
                this.mConnectionTimerHandler.postDelayed(this.mSpeendThread, 5000);
                this.mConnectionTimerHandler.removeCallbacks(this.mCadenceThread);
                this.mConnectionTimerHandler.postDelayed(this.mCadenceThread, 5000);
                handleBikeSpeedData(raw, true);
                handleBikeCadenceData(raw, true);
            }
        }
    }

    private void resetSensorData() {
        this.cadenceZeroCount = 0;
        this.speedZeroCount = 0;
        if (!(this.prevRevCount == 0 && this.prevMeasTime == 0)) {
            this.prevRevCount = 0;
            this.prevMeasTime = 0;
        }
        if (this.prevCrankRevCount != 0 || this.prevCrankMeasTime != 0) {
            this.prevCrankRevCount = 0;
            this.prevCrankMeasTime = 0;
        }
    }

    private void handleBikeSpeedData(byte[] raw, boolean combo) {
        int curRevCount = ((((raw[GET_RECORD_ADL_D_N] & 255) << 24) + ((raw[GET_RECORD_ADL_D_F] & 255) << GET_VELO_RECORD_N)) + ((raw[GET_RECORD_ADL_T] & 255) << 8)) + (raw[DEL_RECORD] & 255);
        int curMeasTime = ((raw[GET_RECORD_WO_N] & 255) << 8) + (raw[GET_RECORD_WO_T] & 255);
        if (this.startRevCount == 0) {
            this.startRevCount = curRevCount;
        }
        if (this.prevRevCount != 0 && this.prevMeasTime != 0 && curMeasTime > this.prevMeasTime && curRevCount > this.prevRevCount) {
            this.speedZeroCount = 0;
            float speed = ((this.wheelCircumference * (((float) ((curRevCount - this.prevRevCount) << 10)) / ((float) (curMeasTime - this.prevMeasTime)))) * 36.0f) / 10.0f;
            if (speed <= 0.01f) {
                speed = 0.0f;
            }
            if (speed > 255.0f) {
                speed %= 255.0f;
            }
            speed = ((float) Math.round(speed * 100.0f)) / 100.0f;
            if (this.mioBikeSensorCallBack != null) {
                if (this.startRevCount > curRevCount) {
                    this.startRevCount = 0;
                }
                this.mioBikeSensorCallBack.onNotificationBikeSpeed(speed, ((float) Math.round((this.wheelCircumference * ((float) (curRevCount - this.startRevCount))) * 100.0f)) / 100.0f);
            }
        } else if (curRevCount == this.prevRevCount || curMeasTime == this.prevMeasTime) {
            this.speedZeroCount += DEL_RECORD;
            if (this.speedZeroCount >= GET_RECORD_WO_T && this.mioBikeSensorCallBack != null) {
                this.mioBikeSensorCallBack.onNotificationBikeSpeed(0.0f, ((float) Math.round((this.wheelCircumference * ((float) (curRevCount - this.startRevCount))) * 100.0f)) / 100.0f);
            }
        }
        if (this.mioBikeSensorCallBack != null) {
            this.mioBikeSensorCallBack.onNotificationSensorData((short) 1, curRevCount, curMeasTime);
        }
        this.prevRevCount = curRevCount;
        this.prevMeasTime = curMeasTime;
    }

    private void handleBikeCadenceData(byte[] raw, boolean combo) {
        int curCrankRevCount;
        int curCrankMeasTime;
        if (combo) {
            curCrankRevCount = ((raw[8] & 255) << 8) + (raw[7] & 255);
            curCrankMeasTime = ((raw[10] & 255) << 8) + (raw[9] & 255);
        } else {
            curCrankRevCount = ((raw[GET_RECORD_ADL_T] & 255) << 8) + (raw[DEL_RECORD] & 255);
            curCrankMeasTime = ((raw[GET_RECORD_ADL_D_N] & 255) << 8) + (raw[GET_RECORD_ADL_D_F] & 255);
        }
        if (this.prevCrankRevCount != 0 && this.prevCrankMeasTime != 0 && curCrankMeasTime > this.prevCrankMeasTime && curCrankRevCount > this.prevCrankRevCount) {
            this.cadenceZeroCount = 0;
            int cadence = (((curCrankRevCount - this.prevCrankRevCount) * 60) * Place.TYPE_SUBLOCALITY_LEVEL_2) / (curCrankMeasTime - this.prevCrankMeasTime);
            if (cadence <= 0) {
                cadence = 0;
            }
            if (cadence > 255) {
                cadence %= 255;
            }
            if (this.mioBikeSensorCallBack != null) {
                this.mioBikeSensorCallBack.onNotificationBikeCadence(cadence);
            }
        } else if (curCrankRevCount == this.prevCrankRevCount || curCrankMeasTime == this.prevCrankMeasTime) {
            this.cadenceZeroCount += DEL_RECORD;
            if (this.mioBikeSensorCallBack != null && this.cadenceZeroCount >= GET_RECORD_WO_T) {
                this.mioBikeSensorCallBack.onNotificationBikeCadence(0);
            }
        }
        if (this.mioBikeSensorCallBack != null) {
            this.mioBikeSensorCallBack.onNotificationSensorData((short) 2, curCrankRevCount, curCrankMeasTime);
        }
        this.prevCrankRevCount = curCrankRevCount;
        this.prevCrankMeasTime = curCrankMeasTime;
    }

    private void doBikeSensorMsgResponseResult(byte[] raw) {
        int length;
        short channelID;
        int manufacturerID;
        int deviceNumber;
        if (raw[DEL_RECORD] == MioHelper.MSG_FOUND_SONSER) {
            MioSportMsgParserUtil.printRaw("MSG_FOUND_SONSER", raw);
            length = raw.length;
            if (length >= 8) {
                channelID = (short) (raw[GET_RECORD_ADL_T] & 255);
                short code = (short) (raw[GET_RECORD_ADL_D_F] & 255);
                if (code == DEL_RECORD) {
                    length = raw[GET_RECORD_WO_T] & 255;
                    manufacturerID = (length << 8) + (raw[GET_RECORD_ADL_D_N] & 255);
                    length = raw[7] & 255;
                    deviceNumber = (length << 8) + (raw[GET_RECORD_WO_N] & 255);
                    if (this.mioBikeSensorSetting == null) {
                        this.mioBikeSensorSetting = new MioBikeSensorSetting();
                    }
                    if (channelID == DEL_RECORD) {
                        this.searchSensorFlag += DEL_RECORD;
                        this.mioBikeSensorSetting.setBikeSpeed(manufacturerID, deviceNumber);
                    } else if (channelID == GET_RECORD_ADL_T) {
                        this.searchSensorFlag += GET_RECORD_ADL_T;
                        this.mioBikeSensorSetting.setBikeCandence(manufacturerID, deviceNumber);
                    } else if (channelID == GET_RECORD_ADL_D_F) {
                        this.mioBikeSensorSetting.setBikeSC(0, deviceNumber);
                        this.searchSensorFlag = GET_RECORD_ADL_D_F;
                    } else if (channelID == GET_RECORD_ADL_D_N) {
                        this.mioBikeSensorSetting.setBikePower(manufacturerID, deviceNumber);
                    }
                    System.out.println("found channelID=" + channelID + " mnufacturerID=" + manufacturerID + " deviceNumber=" + deviceNumber);
                    if (this.mioBikeSensorCallBack != null) {
                        this.mioBikeSensorCallBack.onBikeSensorScan(deviceNumber, manufacturerID, channelID, code);
                    }
                    length = this.searchSensorFlag;
                    if (length == GET_RECORD_ADL_D_F) {
                        this.mConnectionTimerHandler.removeCallbacks(this.mSearchSensorTimeoutThread);
                        this.searchSensorEndFlag = true;
                        if (this.mioBikeSensorCallBack != null) {
                            this.mioBikeSensorCallBack.onBikeSensorScan(0, 0, channelID, (short) 0);
                            return;
                        }
                        return;
                    }
                    return;
                } else if (code == (short) 0 && this.searchSensorFlag == 0 && this.hasSensorData && this.autoRetrySearchSensor) {
                    this.mConnectionTimerHandler.removeCallbacks(this.mSearchSensorTimeoutThread);
                    StartPairBikeSensor_MIO(SensorType.SENSOR_TYPE_ALL, 15000);
                    this.autoRetrySearchSensor = false;
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        int appType;
        int bikeNumCode;
        if (raw[DEL_RECORD] == MioHelper.MSG_ID_RESPONSE) {
            MioSportMsgParserUtil.printRaw("BikeResponse", raw);
            short msgCode = (short) (raw[GET_RECORD_ADL_D_F] & 255);
            if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_SENSOR_GET) {
                short bikeNum = (short) ((raw[GET_RECORD_ADL_D_N] & 255) >> GET_RECORD_ADL_D_N);
                short activeBikeNumCode = (short) (raw[GET_RECORD_ADL_D_N] & SEND_RUN_CMD);
                System.out.println("raw=" + raw.toString() + " bikeNum=" + bikeNum + " activeBikeNumCode=" + activeBikeNumCode);
                if (activeBikeNumCode == (short) 0) {
                    this.activeBikeNum = BikeNum.BIKE_NONE;
                } else if (activeBikeNumCode == DEL_RECORD) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE1;
                } else if (activeBikeNumCode == GET_RECORD_ADL_T) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE2;
                } else if (activeBikeNumCode == GET_RECORD_ADL_D_F) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE3;
                } else if (activeBikeNumCode == GET_RECORD_ADL_D_N) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE4;
                }
                if (bikeNum > (short) 0) {
                    length = raw.length;
                    if (length >= GET_SLEEP_RECORD_CURHOUR_N) {
                        MioBikeSensorSetting mioBikeSensorSetting = new MioBikeSensorSetting();
                        if (DEL_RECORD != 0 && this.mioBikeSensorSetting == null) {
                            this.mioBikeSensorSetting = mioBikeSensorSetting;
                        }
                        if (bikeNum == DEL_RECORD) {
                            mioBikeSensorSetting.setEnableBike(BikeNum.BIKE_TYPE1, true);
                        } else if (bikeNum == GET_RECORD_ADL_T) {
                            mioBikeSensorSetting.setEnableBike(BikeNum.BIKE_TYPE2, true);
                        } else if (bikeNum == GET_RECORD_ADL_D_F) {
                            mioBikeSensorSetting.setEnableBike(BikeNum.BIKE_TYPE3, true);
                        } else {
                            mioBikeSensorSetting.setEnableBike(BikeNum.BIKE_TYPE4, true);
                        }
                        short channelFlag = (short) ((raw[GET_RECORD_WO_T] & 255) >> GET_RECORD_ADL_D_N);
                        short sensorFlag = (short) (raw[GET_RECORD_WO_T] & SEND_RUN_CMD);
                        System.out.println("sensorFlag=" + sensorFlag + " channelFlag=" + channelFlag);
                        if ((sensorFlag & DEL_RECORD) == DEL_RECORD) {
                            mioBikeSensorSetting.setEnableBikeSpeed(true);
                        }
                        if ((sensorFlag & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
                            mioBikeSensorSetting.setEnableBikeCandence(true);
                        }
                        if ((sensorFlag & GET_RECORD_ADL_D_N) == GET_RECORD_ADL_D_N) {
                            mioBikeSensorSetting.setEnableBikeSC(true);
                        }
                        if ((sensorFlag & 8) == 8) {
                            mioBikeSensorSetting.setEnableBikePower(true);
                        }
                        manufacturerID = raw[GET_RECORD_WO_N] & 255;
                        length = raw[8] & 255;
                        deviceNumber = (length << 8) + (raw[7] & 255);
                        System.out.println("manufacturerID=" + manufacturerID + " deviceNumber=" + deviceNumber);
                        mioBikeSensorSetting.setBikeSpeed(manufacturerID, deviceNumber);
                        manufacturerID = raw[9] & 255;
                        length = raw[ENABLE_STREAM_MODE] & 255;
                        deviceNumber = (length << 8) + (raw[10] & 255);
                        System.out.println("manufacturerID=" + manufacturerID + " deviceNumber=" + deviceNumber);
                        mioBikeSensorSetting.setBikeCandence(manufacturerID, deviceNumber);
                        length = raw[AIRPLANE_MODE_ENABLE] & 255;
                        manufacturerID = (length << 8) + (raw[RESET_ADL_TODAY_DATA] & 255);
                        length = raw[SEND_RUN_CMD] & 255;
                        deviceNumber = (length << 8) + (raw[GET_ALPHA2_RECORD_N] & 255);
                        System.out.println("manufacturerID=" + manufacturerID + " deviceNumber=" + deviceNumber);
                        mioBikeSensorSetting.setBikeSC(manufacturerID, deviceNumber);
                        length = raw[DEL_VELO_RECORD] & 255;
                        manufacturerID = (length << 8) + (raw[GET_VELO_RECORD_N] & 255);
                        length = raw[DEL_SLEEP_RECORD] & 255;
                        deviceNumber = (length << 8) + (raw[GET_SLEEP_RECORD_N] & 255);
                        System.out.println("manufacturerID=" + manufacturerID + " deviceNumber=" + deviceNumber);
                        mioBikeSensorSetting.setBikePower(manufacturerID, deviceNumber);
                        if (this.inGetFlag) {
                            EnableBikeSensor_MIO(this.getBikeNum, this.sensorType, this.getEnable, mioBikeSensorSetting);
                            return;
                        } else if (this.mioBikeSensorCallBack != null && DEL_RECORD != 0) {
                            this.mioBikeSensorCallBack.onBikeSensorGetting(mioBikeSensorSetting);
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                System.out.println("get response end");
                return;
            }
            if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_SENSOR_SET) {
                if (this.mioBikeSensorCallBack != null) {
                    if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                        if (this.tempActiveBikeNum != null) {
                            this.activeBikeNum = this.tempActiveBikeNum;
                        }
                        if (this.mioBikeSensorCallBack != null) {
                            this.mioBikeSensorCallBack.onVeloWorkModeGet(this.veloAppType, this.activeBikeNum, null, msgCode);
                        }
                    }
                    this.mioBikeSensorCallBack.onBikeSensorSetting(msgCode);
                    this.mConnectionTimerHandler.removeCallbacks(this.mBluetoothSettingTimeOut);
                }
                this.tempActiveBikeNum = null;
                return;
            }
            if (raw[GET_RECORD_ADL_T] != MioHelper.MSG_VELO_STATUS_GET) {
                if (raw[GET_RECORD_ADL_T] == MioHelper.MSG_VELO_STATUS_SET) {
                    if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                        this.veloAppType = this.tempAppType;
                        if (this.tempActiveBikeNum != null) {
                            this.activeBikeNum = this.tempActiveBikeNum;
                        }
                    }
                    if (this.mioBikeSensorCallBack != null) {
                        this.mioBikeSensorCallBack.onVeloWorkModeSet(msgCode);
                        return;
                    }
                    return;
                }
                return;
            } else if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                appType = raw[GET_RECORD_ADL_D_N] & 255;
                bikeNumCode = raw[GET_RECORD_WO_T] & 255;
                this.veloAppType = VeloAppType.TYPE_MIO;
                this.activeBikeNum = BikeNum.BIKE_NONE;
                if (appType == DEL_RECORD) {
                    this.veloAppType = VeloAppType.TYPE_WAHOO;
                }
                if (bikeNumCode == DEL_RECORD) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE1;
                } else if (bikeNumCode == GET_RECORD_ADL_T) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE2;
                } else if (bikeNumCode == GET_RECORD_ADL_D_F) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE3;
                } else if (bikeNumCode == GET_RECORD_ADL_D_N) {
                    this.activeBikeNum = BikeNum.BIKE_TYPE4;
                }
                try {
                    this.veloMemoryState = new VeloMemoryState();
                    if ((raw[GET_RECORD_WO_N] & DEL_RECORD) == DEL_RECORD) {
                        this.veloMemoryState.HR = (byte) 1;
                    }
                    if ((raw[GET_RECORD_WO_N] & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
                        this.veloMemoryState.HRV = (byte) 1;
                    }
                    if ((raw[GET_RECORD_WO_N] & GET_RECORD_ADL_D_N) == GET_RECORD_ADL_D_N) {
                        this.veloMemoryState.Speed = (byte) 1;
                    }
                    if ((raw[GET_RECORD_WO_N] & 8) == 8) {
                        this.veloMemoryState.Cadence = (byte) 1;
                    }
                    if ((raw[GET_RECORD_WO_N] & GET_VELO_RECORD_N) == GET_VELO_RECORD_N) {
                        this.veloMemoryState.Power = (byte) 1;
                    }
                } catch (Exception e) {
                    this.veloMemoryState = null;
                    e.printStackTrace();
                }
                if (this.mioBikeSensorCallBack != null) {
                    this.mioBikeSensorCallBack.onVeloWorkModeGet(this.veloAppType, this.activeBikeNum, this.veloMemoryState, msgCode);
                }
                Log.e("mioBikeSensorCallBack", "doConnectSuccess");
                doConnectSuccess();
                return;
            } else {
                return;
            }
        }
        if (raw[DEL_RECORD] == MioHelper.MSG_CHANNCEL_STATUS) {
            MioSportMsgParserUtil.printRaw("BikeChannelStatus", raw);
            channelID = (short) -1;
            short code = (short) -1;
            length = raw.length;
            if (length > GET_RECORD_ADL_D_F) {
                channelID = (short) (raw[GET_RECORD_ADL_T] & 255);
                code = (short) (raw[GET_RECORD_ADL_D_F] & 255);
            }
            length = raw.length;
            if (length > 7 && code == GET_RECORD_ADL_D_F && this.mioBikeSensorSetting != null) {
                length = raw[GET_RECORD_WO_T] & 255;
                manufacturerID = (length << 8) + (raw[GET_RECORD_ADL_D_N] & 255);
                length = raw[7] & 255;
                deviceNumber = (length << 8) + (raw[GET_RECORD_WO_N] & 255);
                if (channelID == DEL_RECORD) {
                    this.mioBikeSensorSetting.setBikeSpeed(manufacturerID, deviceNumber);
                } else if (channelID == GET_RECORD_ADL_T) {
                    this.mioBikeSensorSetting.setBikeCandence(manufacturerID, deviceNumber);
                } else if (channelID == GET_RECORD_ADL_D_F) {
                    this.mioBikeSensorSetting.setBikeSC(0, deviceNumber);
                } else if (channelID == GET_RECORD_ADL_D_N) {
                    this.mioBikeSensorSetting.setBikePower(manufacturerID, deviceNumber);
                }
                if (this.mioBikeSensorCallBack != null) {
                    this.mioBikeSensorCallBack.onBikeSensorScan(deviceNumber, manufacturerID, channelID, (short) 3);
                    return;
                }
                return;
            }
            return;
        }
        if (raw[DEL_RECORD] == MioHelper.MSG_INFO_ASYNC) {
            MioSportMsgParserUtil.printRaw("BikeInfoAsync", raw);
            appType = raw[GET_RECORD_ADL_T] & 255;
            bikeNumCode = raw[GET_RECORD_ADL_D_F] & 255;
            this.veloAppType = VeloAppType.TYPE_MIO;
            this.activeBikeNum = BikeNum.BIKE_NONE;
            if (appType == DEL_RECORD) {
                this.veloAppType = VeloAppType.TYPE_WAHOO;
            }
            if (bikeNumCode == DEL_RECORD) {
                this.activeBikeNum = BikeNum.BIKE_TYPE1;
            } else if (bikeNumCode == GET_RECORD_ADL_T) {
                this.activeBikeNum = BikeNum.BIKE_TYPE2;
            } else if (bikeNumCode == GET_RECORD_ADL_D_F) {
                this.activeBikeNum = BikeNum.BIKE_TYPE3;
            } else if (bikeNumCode == GET_RECORD_ADL_D_N) {
                this.activeBikeNum = BikeNum.BIKE_TYPE4;
            }
            if (this.mioBikeSensorCallBack != null) {
                this.mioBikeSensorCallBack.onVeloWorkModeGet(this.veloAppType, this.activeBikeNum, null, (short) 0);
            }
        }
    }

    public void SetBikeSensorCallBack_MIO(MioBikeSensorCallBack callBack) {
        this.mioBikeSensorCallBack = callBack;
    }

    public boolean StartPairBikeSensor_MIO(int timeOut) {
        return StartPairBikeSensor_MIO(SensorType.SENSOR_TYPE_ALL, timeOut);
    }

    public boolean StartPairBikeSensor_MIO(SensorType sensorType, int timeOut) {
        timeOut /= GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE;
        if (timeOut <= 30) {
            timeOut = 30;
        }
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null) {
            return false;
        }
        this.searchSensorFlag = 0;
        this.searchSensorEndFlag = false;
        this.mioBikeSensorSetting = null;
        this.autoRetrySearchSensor = true;
        int bikeChannelID = 0;
        if (sensorType == SensorType.SENSOR_TYPE_SPEED) {
            bikeChannelID = DEL_RECORD;
        } else if (sensorType == SensorType.SENSOR_TYPE_CADENCE) {
            bikeChannelID = GET_RECORD_ADL_T;
        } else if (sensorType == SensorType.SENSOR_TYPE_COMBO) {
            bikeChannelID = GET_RECORD_ADL_D_N;
        } else if (sensorType == SensorType.SENSOR_TYPE_POWER) {
            bikeChannelID = 8;
        } else if (sensorType == SensorType.SENSOR_TYPE_ALL) {
            bikeChannelID = SEND_RUN_CMD;
        }
        byte[] pairBikeSensorCommand = new byte[GET_RECORD_ADL_D_N];
        pairBikeSensorCommand[DEL_RECORD] = (byte) 48;
        pairBikeSensorCommand[GET_RECORD_ADL_T] = (byte) bikeChannelID;
        pairBikeSensorCommand[GET_RECORD_ADL_D_F] = (byte) timeOut;
        this.mCharacteristicMioSportMsg.setValue(pairBikeSensorCommand);
        this.bleCmdState = this.bluetoothGatt.writeCharacteristic(this.mCharacteristicMioSportMsg);
        if (!this.bleCmdState) {
            return false;
        }
        this.mConnectionTimerHandler.removeCallbacks(this.mSearchSensorTimeoutThread);
        this.mConnectionTimerHandler.postDelayed(this.mSearchSensorTimeoutThread, (long) (timeOut * GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE));
        return true;
    }

    public boolean SetBikeSensorSetting_MIO(MioBikeSensorSetting setting) {
        if (setting == null || !this.isConnected || this.bleCmdState) {
            return false;
        }
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || setting == null) {
            return false;
        }
        if (setting.getBikeCandenceDeviceNumber() == 0 && setting.getBikeCandenceManufacturerID() == 0 && setting.getBikePowerDeviceNumber() == 0 && setting.getBikePowerManufacturerID() == 0 && setting.getBikeSCDeviceNumber() == 0 && setting.getBikeSCManufacturerID() == 0 && setting.getBikeSpeedDeviceNumber() == 0 && setting.getBikeSpeedManufacturerID() == 0) {
            return false;
        }
        byte bikeNum;
        this.inGetFlag = false;
        if (setting.getCurBikeNum() == BikeNum.BIKE_TYPE1) {
            bikeNum = (byte) 17;
            this.tempActiveBikeNum = BikeNum.BIKE_TYPE1;
        } else {
            if (setting.getCurBikeNum() == BikeNum.BIKE_TYPE2) {
                bikeNum = (byte) 34;
                this.tempActiveBikeNum = BikeNum.BIKE_TYPE2;
            } else {
                if (setting.getCurBikeNum() == BikeNum.BIKE_TYPE3) {
                    bikeNum = (byte) 51;
                    this.tempActiveBikeNum = BikeNum.BIKE_TYPE3;
                } else {
                    bikeNum = (byte) 68;
                    this.tempActiveBikeNum = BikeNum.BIKE_TYPE4;
                }
            }
        }
        byte flags = (byte) 0;
        byte speedManufacturerID = (byte) 0;
        byte speedDeviceNumberL = (byte) 0;
        byte speedDeviceNumberH = (byte) 0;
        byte candenceManufacturerID = (byte) 0;
        byte candenceDeviceNumberL = (byte) 0;
        byte candenceDeviceNumberH = (byte) 0;
        byte scManufacturerIDL = (byte) 0;
        byte scManufacturerIDH = (byte) 0;
        byte scDeviceNumberL = (byte) 0;
        byte scDeviceNumberH = (byte) 0;
        byte pManufacturerIDL = (byte) 0;
        byte pManufacturerIDH = (byte) 0;
        byte pDeviceNumberL = (byte) 0;
        byte pDeviceNumberH = (byte) 0;
        if (setting.isEnableBikeSpeed() && setting.getBikeSpeedDeviceNumber() > 0) {
            flags = (byte) DEL_RECORD;
            speedManufacturerID = (byte) setting.getBikeSpeedManufacturerID();
            speedDeviceNumberL = (byte) (setting.getBikeSpeedDeviceNumber() & 255);
            speedDeviceNumberH = (byte) ((setting.getBikeSpeedDeviceNumber() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        }
        if (setting.isEnableBikeCandence() && setting.getBikeCandenceDeviceNumber() > 0) {
            flags = (byte) (flags | GET_RECORD_ADL_T);
            candenceManufacturerID = (byte) setting.getBikeCandenceManufacturerID();
            candenceDeviceNumberL = (byte) (setting.getBikeCandenceDeviceNumber() & 255);
            candenceDeviceNumberH = (byte) ((setting.getBikeCandenceDeviceNumber() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        }
        if (setting.isEnableBikeSC() && setting.getBikeSCDeviceNumber() > 0) {
            flags = (byte) (flags | GET_RECORD_ADL_D_N);
            scManufacturerIDL = (byte) (setting.getBikeSCManufacturerID() & 255);
            scManufacturerIDH = (byte) ((setting.getBikeSCManufacturerID() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
            scDeviceNumberL = (byte) (setting.getBikeSCDeviceNumber() & 255);
            scDeviceNumberH = (byte) ((setting.getBikeSCDeviceNumber() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        }
        if (setting.isEnableBikePower() && setting.getBikePowerDeviceNumber() > 0) {
            flags = (byte) (flags | 8);
            pManufacturerIDL = (byte) (setting.getBikePowerManufacturerID() & 255);
            pManufacturerIDH = (byte) ((setting.getBikePowerManufacturerID() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
            pDeviceNumberL = (byte) (setting.getBikePowerDeviceNumber() & 255);
            pDeviceNumberH = (byte) ((setting.getBikePowerDeviceNumber() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        }
        byte[] settingCommand = new byte[GET_SLEEP_RECORD_N];
        settingCommand[0] = MioHelper.EventFlagNegativeAction;
        settingCommand[DEL_RECORD] = (byte) 50;
        settingCommand[GET_RECORD_ADL_T] = bikeNum;
        settingCommand[GET_RECORD_ADL_D_F] = flags;
        settingCommand[GET_RECORD_ADL_D_N] = speedManufacturerID;
        settingCommand[GET_RECORD_WO_T] = speedDeviceNumberL;
        settingCommand[GET_RECORD_WO_N] = speedDeviceNumberH;
        settingCommand[7] = candenceManufacturerID;
        settingCommand[8] = candenceDeviceNumberL;
        settingCommand[9] = candenceDeviceNumberH;
        settingCommand[10] = scManufacturerIDL;
        settingCommand[ENABLE_STREAM_MODE] = scManufacturerIDH;
        settingCommand[RESET_ADL_TODAY_DATA] = scDeviceNumberL;
        settingCommand[AIRPLANE_MODE_ENABLE] = scDeviceNumberH;
        settingCommand[GET_ALPHA2_RECORD_N] = pManufacturerIDL;
        settingCommand[SEND_RUN_CMD] = pManufacturerIDH;
        settingCommand[GET_VELO_RECORD_N] = pDeviceNumberL;
        settingCommand[DEL_VELO_RECORD] = pDeviceNumberH;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_BIKE_SET, settingCommand);
    }

    public boolean GetBikeSensorSetting_MIO(BikeNum bikeNum) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte bikeNumCode = (byte) 0;
        if (bikeNum == BikeNum.BIKE_TYPE1) {
            bikeNumCode = (byte) 1;
        } else if (bikeNum == BikeNum.BIKE_TYPE2) {
            bikeNumCode = (byte) 2;
        } else if (bikeNum == BikeNum.BIKE_TYPE3) {
            bikeNumCode = (byte) 3;
        } else if (bikeNum == BikeNum.BIKE_TYPE4) {
            bikeNumCode = (byte) 4;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_D_F];
        getCommand[DEL_RECORD] = (byte) 49;
        getCommand[GET_RECORD_ADL_T] = bikeNumCode;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_BIKE_GET, getCommand);
    }

    public boolean ClearAllBikeSensorSetting_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] settingCommand = new byte[GET_RECORD_ADL_D_F];
        settingCommand[DEL_RECORD] = (byte) 50;
        settingCommand[GET_RECORD_ADL_T] = (byte) 0;
        this.tempActiveBikeNum = null;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_BIKE_SET, settingCommand);
    }

    public boolean ClearBikeSensorSetting_MIO(boolean autoMode, BikeNum bikeNum) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || this.activeBikeNum == null) {
            return false;
        }
        byte code = (byte) 0;
        if (bikeNum == BikeNum.BIKE_TYPE1) {
            code = (byte) 1;
        } else if (bikeNum == BikeNum.BIKE_TYPE2) {
            code = (byte) 2;
        } else if (bikeNum == BikeNum.BIKE_TYPE3) {
            code = (byte) 3;
        } else if (bikeNum == BikeNum.BIKE_TYPE4) {
            code = (byte) 4;
        }
        System.out.println("code=" + code);
        if (!(autoMode || this.activeBikeNum == bikeNum)) {
            byte aCode = (byte) 0;
            if (this.activeBikeNum == BikeNum.BIKE_TYPE1) {
                aCode = MioHelper.EventFlagNegativeAction;
            } else if (this.activeBikeNum == BikeNum.BIKE_TYPE2) {
                aCode = (byte) 32;
            } else if (this.activeBikeNum == BikeNum.BIKE_TYPE3) {
                aCode = (byte) 48;
            } else if (this.activeBikeNum == BikeNum.BIKE_TYPE4) {
                aCode = (byte) 64;
            }
            System.out.println("aCode=" + aCode);
            code = (byte) (code + aCode);
        }
        System.out.println("cmdCode=" + code);
        byte[] settingCommand = new byte[GET_SLEEP_RECORD_N];
        settingCommand[0] = MioHelper.EventFlagNegativeAction;
        settingCommand[DEL_RECORD] = (byte) 50;
        settingCommand[GET_RECORD_ADL_T] = code;
        this.tempActiveBikeNum = null;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_BIKE_SET, settingCommand);
    }

    private boolean commonBluetoothSetting(CMD_TYPE type, byte[] value) {
        MioSportMsgParserUtil.printRaw("send_cmd=" + type, value);
        if (this.mCharacteristicMioSportMsg == null) {
            return false;
        }
        try {
            this.mCharacteristicMioSportMsg.setValue(value);
            this.bleCmdState = this.bluetoothGatt.writeCharacteristic(this.mCharacteristicMioSportMsg);
            Log.e("commonBluetoothSetting", "bleCmdState=" + this.bleCmdState);
            if (!this.bleCmdState) {
                return false;
            }
            MioSportMsgParserUtil.writeLogtoFile("send_cmd", DiffResult.OBJECTS_SAME_STRING, MioSportMsgParserUtil.printRaw("send_cmd", value));
            this.cmdType = type;
            this.mConnectionTimerHandler.removeCallbacks(this.mBluetoothSettingTimeOut);
            this.mConnectionTimerHandler.postDelayed(this.mBluetoothSettingTimeOut, 5000);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean SetVeloWorkMode_MIO(VeloAppType type, BikeNum bikeNum, VeloMemoryState veloMemory) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte appType = (byte) 0;
        if (type == VeloAppType.TYPE_WAHOO) {
            appType = (byte) 1;
        }
        byte bikeNumCode = (byte) 0;
        if (bikeNum == BikeNum.BIKE_TYPE1) {
            bikeNumCode = (byte) 1;
        } else if (bikeNum == BikeNum.BIKE_TYPE2) {
            bikeNumCode = (byte) 2;
        } else if (bikeNum == BikeNum.BIKE_TYPE3) {
            bikeNumCode = (byte) 3;
        } else if (bikeNum == BikeNum.BIKE_TYPE4) {
            bikeNumCode = (byte) 4;
        }
        this.tempActiveBikeNum = bikeNum;
        this.tempAppType = type;
        byte flags = (byte) 0;
        if (veloMemory != null) {
            if (veloMemory.HR == (byte) 1) {
                flags = (byte) DEL_RECORD;
            }
            if (veloMemory.HRV == (byte) 1) {
                flags = (byte) (flags | GET_RECORD_ADL_T);
            }
            if (veloMemory.Speed == (byte) 1) {
                flags = (byte) (flags | GET_RECORD_ADL_D_N);
            }
            if (veloMemory.Cadence == (byte) 1) {
                flags = (byte) (flags | 8);
            }
            if (veloMemory.Power == (byte) 1) {
                flags = (byte) (flags | GET_VELO_RECORD_N);
            }
        }
        byte[] settingCommand = new byte[GET_RECORD_WO_T];
        settingCommand[DEL_RECORD] = MioHelper.MSG_VELO_STATUS_SET;
        settingCommand[GET_RECORD_ADL_T] = appType;
        settingCommand[GET_RECORD_ADL_D_F] = bikeNumCode;
        settingCommand[GET_RECORD_ADL_D_N] = flags;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_APPTYPE_SET, settingCommand);
    }

    public boolean GetVeloWorkMode_MIO() {
        if (!this.bleIsConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] settingCommand = new byte[GET_RECORD_ADL_T];
        settingCommand[DEL_RECORD] = MioHelper.MSG_VELO_STATUS_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_APPTYPE_GET, settingCommand);
    }

    public boolean EnableBikeSensor_MIO(BikeNum bikeNum, SensorType sensorType, boolean enable) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (this.mioBikeSensorSetting == null || this.mioBikeSensorSetting.getCurBikeNum() != bikeNum) {
            this.inGetFlag = true;
            this.getBikeNum = bikeNum;
            this.sensorType = sensorType;
            this.getEnable = enable;
            return GetBikeSensorSetting_MIO(bikeNum);
        }
        this.inGetFlag = false;
        return EnableBikeSensor_MIO(bikeNum, sensorType, enable, this.mioBikeSensorSetting);
    }

    public void EnableNotificationBikeSensor_MIO(boolean enable) {
        if (this.enableNotifyBikeSensorData != enable) {
            this.enableNotifyBikeSensorData = enable;
        }
    }

    public void SetBikeWheelCircumference_MIO(float circumference) {
        if (circumference > 0.0f) {
            this.wheelCircumference = circumference;
        }
    }

    public float GetBikeWheelCircumference_MIO() {
        return this.wheelCircumference;
    }

    private boolean EnableBikeSensor_MIO(BikeNum bikeNum, SensorType sensorType, boolean enable, MioBikeSensorSetting setting) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte flags = (byte) 0;
        if (setting.isEnableBikeSpeed()) {
            flags = (byte) DEL_RECORD;
        }
        if (setting.isEnableBikeCandence()) {
            flags = (byte) (flags | GET_RECORD_ADL_T);
        }
        if (setting.isEnableBikeSC()) {
            flags = (byte) (flags | GET_RECORD_ADL_D_N);
        }
        if (setting.isEnableBikePower()) {
            flags = (byte) (flags | 8);
        }
        if (sensorType == SensorType.SENSOR_TYPE_SPEED) {
            if (enable) {
                flags = (byte) (flags | DEL_RECORD);
            } else {
                flags = (byte) (flags & GET_ALPHA2_RECORD_N);
            }
        } else if (sensorType == SensorType.SENSOR_TYPE_CADENCE) {
            if (enable) {
                flags = (byte) (flags | GET_RECORD_ADL_T);
            } else {
                flags = (byte) (flags & AIRPLANE_MODE_ENABLE);
            }
        } else if (sensorType == SensorType.SENSOR_TYPE_COMBO) {
            if (enable) {
                flags = (byte) (flags | GET_RECORD_ADL_D_N);
            } else {
                flags = (byte) (flags & ENABLE_STREAM_MODE);
            }
        } else if (sensorType == SensorType.SENSOR_TYPE_POWER) {
            if (enable) {
                flags = (byte) (flags | 8);
            } else {
                flags = (byte) (flags & 7);
            }
        } else if (sensorType == SensorType.SENSOR_TYPE_ALL) {
            if (enable) {
                flags = (byte) 15;
            } else {
                flags = (byte) 0;
            }
        }
        byte[] settingCommand = new byte[GET_RECORD_ADL_D_N];
        settingCommand[DEL_RECORD] = (byte) 50;
        settingCommand[GET_RECORD_ADL_T] = (byte) 15;
        settingCommand[GET_RECORD_ADL_D_F] = flags;
        this.tempActiveBikeNum = null;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_BIKE_SET, settingCommand);
    }

    public void setMioDeviceCallBack(MioDeviceCallBack mioDeviceCallBack) {
        this.mioDeviceCallBack = mioDeviceCallBack;
    }

    public boolean SetUserInfo_MIO(UserInfo userInfo, String version) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || userInfo == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (this.mioDeviceInformation.IsSetUseInfoSupported_MIO(this.deviceName)) {
            if (userInfo.resetHR >= userInfo.maxHR) {
                return false;
            }
            int versionInt = 0;
            try {
                versionInt = (int) (100.0f * Float.parseFloat(version));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte flags = (byte) 0;
            if (userInfo.genderType == (byte) 1) {
                flags = (byte) DEL_RECORD;
            }
            if (userInfo.unitType == (byte) 1) {
                flags = (byte) (flags | GET_RECORD_ADL_T);
            }
            if (userInfo.HMRDisplayType == (byte) 1) {
                flags = (byte) (flags | GET_RECORD_ADL_D_N);
            }
            if (userInfo.displayOrien == (byte) 1) {
                flags = (byte) (flags | 8);
            }
            if (userInfo.woDispMode == (byte) 1) {
                flags = (byte) (flags | GET_VELO_RECORD_N);
            }
            if (userInfo.ADLCalorieGoalOpt == (byte) 1 && versionInt >= 108) {
                flags = (byte) (flags | 32);
            }
            if (userInfo.WORecording == (byte) 1) {
                flags = (byte) (flags | 64);
            }
            if (userInfo.MHRAutoAdj == (byte) 1) {
                flags = (byte) (flags | TransportMediator.FLAG_KEY_MEDIA_NEXT);
            }
            MioSportMsgParserUtil.checkUserInfo(userInfo);
            return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_USEINFO_SET, new byte[]{(byte) 8, MioHelper.MSG_USER_SETTING_SET, flags, userInfo.birthDay, userInfo.birthMonth, (byte) userInfo.birthYear, (byte) userInfo.bodyWeight, (byte) userInfo.bodyHeight, (byte) userInfo.resetHR, (byte) userInfo.maxHR});
        } else if (this.mioDeviceCallBack == null) {
            return false;
        } else {
            this.mioDeviceCallBack.onSetUserInfo((byte) -1);
            return false;
        }
    }

    public boolean GetUserInfo_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsSetUseInfoSupported_MIO(this.deviceName)) {
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.MSG_USER_SETTING_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_USEINFO_GET, getCommand);
    }

    public boolean SetDeviceName_MIO(String name) {
        if (!this.isConnected || name == null || name.length() <= 0 || this.bleCmdState || this.bluetoothGatt == null || !this.isConnected || this.mCharacteristicMioSportMsg == null) {
            return false;
        }
        if (this.mioDeviceInformation.IsSetNameSupported_MIO(this.deviceName)) {
            int len = name.length();
            if (len > RESET_ADL_TODAY_DATA) {
                len = RESET_ADL_TODAY_DATA;
            }
            byte[] setCommand = new byte[GET_ALPHA2_RECORD_N];
            setCommand[0] = (byte) 12;
            setCommand[DEL_RECORD] = MioHelper.MSG_DEVICE_NAME_SET;
            for (int i = 0; i < len; i += DEL_RECORD) {
                setCommand[i + GET_RECORD_ADL_T] = (byte) name.charAt(i);
            }
            return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NAME_SET, setCommand);
        }
        System.out.println("------------------>not Support Set DeviceName");
        if (this.mioDeviceCallBack == null) {
            return false;
        }
        this.mioDeviceCallBack.onSetDeviceName((byte) -1);
        return false;
    }

    public boolean GetDeviceName_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (this.mioDeviceInformation.IsSetNameSupported_MIO(this.deviceName)) {
            byte[] getCommand = new byte[GET_RECORD_ADL_T];
            getCommand[DEL_RECORD] = MioHelper.MSG_DEVICE_NAME_GET;
            return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NAME_GET, getCommand);
        }
        System.out.println("------------------>not Support get DeviceName");
        if (this.mioDeviceCallBack == null) {
            return false;
        }
        this.mioDeviceCallBack.onGetDeviceName(DiffResult.OBJECTS_SAME_STRING, (byte) -1);
        return false;
    }

    public boolean SetRTCTime_MIO(RTCSetting rtcSetting) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || rtcSetting.timeData == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] setCommand;
        boolean supportDate = this.mioDeviceInformation.IsRTCDateSupported_MIO(this.deviceName);
        Log.e("SetRTCTime_MIO", "------------------> Support Set Date:" + supportDate);
        if (supportDate) {
            byte flags = (byte) 0;
            if (rtcSetting.timeFormat == TIMEFORMAT.TIMEFORMAT_24H) {
                flags = (byte) DEL_RECORD;
            }
            if (rtcSetting.dateFormat == DATEFORMAT.DATEFORMAT_DDMM) {
                flags = (byte) (flags | GET_RECORD_ADL_T);
            }
            MioSportMsgParserUtil.checkTimeData(rtcSetting.timeData, rtcSetting.timeFormat);
            setCommand = new byte[]{(byte) 7, MioHelper.MSG_RTC_SET, flags, rtcSetting.timeData.second, rtcSetting.timeData.minute, rtcSetting.timeData.hour, rtcSetting.timeData.day, rtcSetting.timeData.month, (byte) rtcSetting.timeData.year};
        } else {
            MioSportMsgParserUtil.checkTimeData(rtcSetting.timeData);
            setCommand = new byte[]{(byte) 7, MioHelper.MSG_RTCTIME_SET, rtcSetting.timeData.second, rtcSetting.timeData.minute, rtcSetting.timeData.hour, (byte) 53, (byte) 70, (byte) 82, (byte) 110};
        }
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RTC_SET, setCommand);
    }

    public boolean GetRTCTime_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte cmd = MioHelper.MSG_RTC_GET;
        boolean supportDate = this.mioDeviceInformation.IsRTCDateSupported_MIO(this.deviceName);
        System.out.println("------------------>Support RTC Set Date:" + supportDate);
        if (!supportDate) {
            cmd = MioHelper.MSG_RTCTIME_GET;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = cmd;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RTC_GET, getCommand);
    }

    public boolean SendRunCmd_MIO(RUN_CMD runCmd) {
        if (runCmd == RUN_CMD.CMD_StreamModeDisable) {
            this.isSync = false;
        }
        if (this.rType != 0) {
            return false;
        }
        return SendRunCmd_MIO(runCmd, true);
    }

    private boolean SendRunCmd_MIO(RUN_CMD runCmd, boolean flag) {
        Log.e("SendRunCmd_MIO", "RUN_CMD=" + runCmd);
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (runCmd == RUN_CMD.CMD_StreamModeEnable) {
            this.isSync = true;
        }
        byte cmd = (byte) 0;
        if (runCmd == RUN_CMD.CMD_StreamModeDisable) {
            cmd = MioHelper.RUN_CMD_SMD;
        } else if (runCmd == RUN_CMD.CMD_StreamModeEnable) {
            cmd = MioHelper.RUN_CMD_SME;
        } else if (runCmd == RUN_CMD.CMD_GPSModeDisable) {
            cmd = MioHelper.RUN_CMD_GMD;
        } else if (runCmd == RUN_CMD.CMD_GPSModeEnable) {
            cmd = MioHelper.RUN_CMD_GME;
        } else if (runCmd == RUN_CMD.CMD_ResetTodayADLData) {
            cmd = MioHelper.RUN_CMD_RAD;
        } else if (runCmd == RUN_CMD.CMD_StepDataNotifyDisable) {
            cmd = MioHelper.RUN_CMD_STEP_DATA_NOTIFY_DIS;
        } else if (runCmd == RUN_CMD.CMD_StepDataNotifyEnable) {
            cmd = MioHelper.RUN_CMD_STEP_DATA_NOTIFY_ENABLE;
        } else if (runCmd == RUN_CMD.CMD_AirplaneModeEnable) {
            cmd = MioHelper.RUN_CMD_AIRMODE_ENABLE;
        } else if (runCmd == RUN_CMD.CMD_MemAllClear) {
            cmd = MioHelper.RUN_CMD_MEM_CLEAR;
        } else if (runCmd == RUN_CMD.CMD_ACTMemAllClear) {
            cmd = MioHelper.RUN_CMD_ACT_MEM_ALLCLEAR;
        } else if (runCmd == RUN_CMD.CMD_ADLMemAllClear) {
            cmd = MioHelper.RUN_CMD_ADL_MEM_ALLCLEAR;
        } else if (runCmd == RUN_CMD.CMD_UserDataBackupNow) {
            cmd = MioHelper.RUN_CMD_USER_DATA_BACKUP;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncDataNotificationDisable) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_DISABLE;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncDataNotificationEnable) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_ENABLE;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncCmd_StartTimer) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_CMD_START_TIMER;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncCmd_StopTimer) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_CMD_STOP_TIMER;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncCmd_TakeLap) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_CMD_TAKE_LAP;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncCmd_ResendLastLapData) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_CMD_RESEND_LAST_LAP_DATA;
        } else if (runCmd == RUN_CMD.CMD_ExeTimerSyncCmd_Finish) {
            cmd = MioHelper.RUN_CMD_EXE_TIMER_SYNC_CMD_TIMER_SYNC_FINISH;
        } else if (runCmd == RUN_CMD.CMD_SleepModeDeactivate) {
            cmd = MioHelper.RUN_CMD_SLEEPMODELDEACTIVATE;
        } else if (runCmd == RUN_CMD.CMD_SleepModeActivate) {
            cmd = MioHelper.RUN_CMD_SLEEPMODELACTIVATE;
        } else if (runCmd == RUN_CMD.CMD_RestHRTakeMeasurement) {
            cmd = MioHelper.RUN_CMD_RESTHRTAKEMEASUREMENT;
        } else if (runCmd == RUN_CMD.CMD_RestHRStopMeasurement) {
            cmd = MioHelper.RUN_CMD_RESTHRSTOPMEASUREMENT;
        } else if (runCmd == RUN_CMD.CMD_RestHRSendMeasurementResults) {
            cmd = MioHelper.RUN_CMD_RESTHRSENDMEASUREMENTRESULTS;
        }
        this.tempRunCmd = runCmd;
        if (flag && runCmd != RUN_CMD.CMD_StreamModeEnable) {
            this.rType = SEND_RUN_CMD;
        }
        byte[] setCommand = new byte[GET_RECORD_ADL_D_F];
        setCommand[0] = (byte) 1;
        setCommand[DEL_RECORD] = MioHelper.MSG_RUN_CMD;
        setCommand[GET_RECORD_ADL_T] = cmd;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RUN_CMD, setCommand);
    }

    public boolean SendGpsData_MIO(GPSData gpsData) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || gpsData == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.endsWith("FUSE")) {
            return false;
        }
        byte flags = (byte) 0;
        if (gpsData.gpsDataFlag == GPSDATAFLAG.GPSDATAFLAG_SERCHING) {
            flags = (byte) 1;
        } else if (gpsData.gpsDataFlag == GPSDATAFLAG.GPSDATAFLAG_2D) {
            flags = (byte) 2;
        } else if (gpsData.gpsDataFlag == GPSDATAFLAG.GPSDATAFLAG_3D) {
            flags = (byte) 3;
        }
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SEND_GPS_DATA, new byte[]{(byte) 6, MioHelper.MSG_SENSOR_DATA, MioHelper.GPS_SD_DATA, flags, (byte) (gpsData.speed & 255), (byte) ((gpsData.speed & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (gpsData.odometer & 255), (byte) ((gpsData.odometer & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8)});
    }

    public boolean SetDisplay_MIO(FuseDisplay fuseDisplay, String version) {
        Log.e("SetDisplay_MIO", "version=" + version);
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || fuseDisplay == null || fuseDisplay.adlDisplay == null || fuseDisplay.woDisplay == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (!this.mioDeviceInformation.IsDisplaySupported_MIO(this.deviceName)) {
            return false;
        }
        byte[] setCommand;
        byte adlDisp = (byte) 0;
        byte woDisp = (byte) 0;
        if (fuseDisplay.adlDisplay.dataCalorieEnable) {
            adlDisp = (byte) DEL_RECORD;
        }
        if (fuseDisplay.adlDisplay.dataStepEnable) {
            adlDisp = (byte) (adlDisp | GET_RECORD_ADL_T);
        }
        if (fuseDisplay.adlDisplay.dataDistanceEnable) {
            adlDisp = (byte) (adlDisp | GET_RECORD_ADL_D_N);
        }
        if (fuseDisplay.adlDisplay.dataGoalEnable) {
            adlDisp = (byte) (adlDisp | 8);
        }
        if (fuseDisplay.woDisplay.dataCalorieEnable) {
            woDisp = (byte) DEL_RECORD;
        }
        if (fuseDisplay.woDisplay.dataStepEnable) {
            woDisp = (byte) (woDisp | GET_RECORD_ADL_T);
        }
        if (fuseDisplay.woDisplay.dataDistanceEnable) {
            woDisp = (byte) (woDisp | GET_RECORD_ADL_D_N);
        }
        if (fuseDisplay.woDisplay.dataPaceEnable) {
            woDisp = (byte) (woDisp | 8);
        }
        if (fuseDisplay.woDisplay.dataSpeedEnable) {
            woDisp = (byte) (woDisp | GET_VELO_RECORD_N);
        }
        if (fuseDisplay.woDisplay.dataTimeEnable) {
            woDisp = (byte) (woDisp | 32);
        }
        if (this.deviceName.endsWith("FUSE")) {
            if (fuseDisplay.adlKeyLockTime != 0) {
                if (fuseDisplay.adlKeyLockTime < 10) {
                    fuseDisplay.adlKeyLockTime = 10;
                }
                if (fuseDisplay.adlKeyLockTime > 120) {
                    fuseDisplay.adlKeyLockTime = 120;
                }
            }
            if (fuseDisplay.woKeyLockTime != 0) {
                if (fuseDisplay.woKeyLockTime < 10) {
                    fuseDisplay.woKeyLockTime = 10;
                }
                if (fuseDisplay.woKeyLockTime > 120) {
                    fuseDisplay.woKeyLockTime = 120;
                }
            }
            byte[] setCommand1;
            if (version == null || version.length() == 0) {
                setCommand1 = new byte[GET_RECORD_WO_T];
                setCommand1[0] = (byte) 3;
                setCommand1[DEL_RECORD] = MioHelper.LINK_DISP_SET;
                setCommand1[GET_RECORD_ADL_T] = adlDisp;
                setCommand1[GET_RECORD_ADL_D_F] = woDisp;
                setCommand1[GET_RECORD_ADL_D_N] = (byte) fuseDisplay.adlKeyLockTime;
                setCommand = setCommand1;
            } else {
                int versionInt = 0;
                try {
                    versionInt = (int) (100.0f * Float.parseFloat(version));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (versionInt < 108) {
                    setCommand1 = new byte[GET_RECORD_WO_T];
                    setCommand1[0] = (byte) 3;
                    setCommand1[DEL_RECORD] = MioHelper.LINK_DISP_SET;
                    setCommand1[GET_RECORD_ADL_T] = adlDisp;
                    setCommand1[GET_RECORD_ADL_D_F] = woDisp;
                    setCommand1[GET_RECORD_ADL_D_N] = (byte) fuseDisplay.adlKeyLockTime;
                    setCommand = setCommand1;
                } else {
                    setCommand1 = new byte[GET_RECORD_WO_N];
                    setCommand1[0] = (byte) 4;
                    setCommand1[DEL_RECORD] = MioHelper.LINK_DISP_SET2;
                    setCommand1[GET_RECORD_ADL_T] = adlDisp;
                    setCommand1[GET_RECORD_ADL_D_F] = woDisp;
                    setCommand1[GET_RECORD_ADL_D_N] = (byte) fuseDisplay.adlKeyLockTime;
                    setCommand1[GET_RECORD_WO_T] = (byte) fuseDisplay.woKeyLockTime;
                    setCommand = setCommand1;
                }
            }
        } else {
            byte[] setCommand2 = new byte[GET_RECORD_ADL_D_N];
            setCommand2[0] = (byte) 2;
            setCommand2[DEL_RECORD] = MioHelper.LINK_DISP_SET;
            setCommand2[GET_RECORD_ADL_T] = adlDisp;
            setCommand2[GET_RECORD_ADL_D_F] = woDisp;
            setCommand = setCommand2;
        }
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DISPLAY_SET, setCommand);
    }

    public boolean GetDisplay_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsDisplaySupported_MIO(this.deviceName)) {
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.LINK_DISP_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DISPLAY_GET, getCommand);
    }

    public boolean SetDailyGoal_MIO(GoalData goalData) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || goalData == null || goalData.goalData == null || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsDialyGoalSupported_MIO(this.deviceName)) {
            return false;
        }
        byte flag;
        if (this.deviceName.endsWith("LINK2")) {
            flag = goalData.flags;
        } else {
            flag = (byte) (((byte) DEL_RECORD) | GET_RECORD_ADL_T);
            Log.e("SetDailyGoal_MIO", "SetDailyGoal_MIO=" + flag);
        }
        MioSportMsgParserUtil.checkGoalData(goalData);
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DAILYGOAL_SET, new byte[]{(byte) 6, MioHelper.LINK_DAILY_GOAL_SET, flag, (byte) (goalData.goalData.goalValue & 255), (byte) ((goalData.goalData.goalValue & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((goalData.goalData.goalValue & 16711680) >> GET_VELO_RECORD_N), (byte) ((goalData.goalData.goalValue & ViewCompat.MEASURED_STATE_MASK) >> 24), goalData.goalData.goalType});
    }

    public boolean GetDailyGoal_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsDialyGoalSupported_MIO(this.deviceName)) {
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.LINK_DAILY_GOAL_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DAILYGOAL_GET, getCommand);
    }

    public boolean GetDeviceStatus_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            Log.e("GetDeviceStatus_MIO", "GetDeviceStatus_MIO=null");
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.LINK_DEVICE_STATUS_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DEVICE_STATUS_GET, getCommand);
    }

    public boolean GetVeloDeviceStatus_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            Log.e("GetVeloDeviceStatus_MIO", "GetVeloDeviceStatus_MIO=null");
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.MSG_VELO_DEVICE_STATUS_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_VELO_DEVICE_STATUS_GET, getCommand);
    }

    public boolean AirplaneModeEnable_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        this.rType = AIRPLANE_MODE_ENABLE;
        return SendRunCmd_MIO(RUN_CMD.CMD_AirplaneModeEnable, false);
    }

    public boolean ResetTodayADLData_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            return false;
        }
        this.rType = RESET_ADL_TODAY_DATA;
        return SendRunCmd_MIO(RUN_CMD.CMD_ResetTodayADLData, false);
    }

    public boolean GetTodayADLRecord_MIO() {
        if (!this.isConnected || this.rType != 0 || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            return false;
        }
        this.rType = GET_RECORD_ADL_T;
        return GetDeviceStatus_MIO();
    }

    private void doGetTodayADLRecord_MIO() {
        if (this.mCharacteristicMioSportMsg != null && !this.bleCmdState && this.bluetoothGatt != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] delCommand;
                    if (MioDeviceConnection.this.deviceName.endsWith("ALPHA2")) {
                        delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
                        delCommand[0] = (byte) 2;
                        delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK2_MEM_RECORD_GET;
                        delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 3;
                        MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_TODAY_ADL_RECORD_GET, delCommand);
                    } else {
                        delCommand = new byte[MioDeviceConnection.GET_RECORD_WO_T];
                        delCommand[0] = (byte) 3;
                        delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_GET;
                        delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 3;
                        MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_TODAY_ADL_RECORD_GET, delCommand);
                    }
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                    MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 5000);
                }
            }).start();
        }
    }

    private void doGetVeloRecord_MIO(int index) {
        Log.e("doGetVeloRecord_MIO", "workoutRecordIndex=" + index);
        this.workoutRecordIndex = index;
        this.rType = GET_VELO_RECORD_N;
        doGetVeloRecord_MIO();
    }

    private void doGetVeloRecord_MIO() {
        Log.e("doGetVeloRecord_MIO", "doGetVeloRecord_MIO1");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte rix = (byte) 0;
                if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_VELO_RECORD_N) {
                    rix = (byte) 2;
                }
                byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_WO_T];
                delCommand[0] = (byte) 3;
                delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.MSG_VELO_RECORD_GET;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 1;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = rix;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_N] = (byte) MioDeviceConnection.this.workoutRecordIndex;
                MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_VELO_MEM_RECORD_GET, delCommand);
            }
        }).start();
    }

    private void doCMDMsgRsp(byte[] raw) {
        byte msgCode = raw[GET_RECORD_ADL_D_F];
        Log.e("doCMDMsgRsp", "rType=" + this.rType + "   msgCode+" + msgCode);
        if (this.rType == SEND_RUN_CMD && this.mioDeviceCallBack != null) {
            this.mioDeviceCallBack.onSendRunCmd_MIO(msgCode);
            Log.e("rType25", "rType=" + this.rType);
            this.rType = 0;
        } else if (this.rType == RESET_ADL_TODAY_DATA && this.mioDeviceCallBack != null) {
            Log.e("rType26", "rType=" + this.rType);
            this.rType = 0;
            this.mioDeviceCallBack.onResetTodayADLRecord(msgCode);
        } else if (AIRPLANE_MODE_ENABLE == this.rType && this.mioDeviceCallBack != null) {
            Log.e("rType27", "rType=" + this.rType);
            this.rType = 0;
            this.mioDeviceCallBack.onAirplaneModeEnable(msgCode);
        } else if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
            if (this.rType == GET_RECORD_ADL_T || this.rType == GET_RECORD_ADL_D_N || this.rType == GET_SLEEP_RECORD_N || this.rType == GET_SLEEP_RECORD_T || this.rType == GET_SLEEP_RECORD_CURHOUR_N || this.rType == GET_RECORD_ADL_D_F || this.rType == GET_RECORD_WO_N || this.rType == GET_RECORD_WO_T || this.rType == GET_VELO_RECORD_N) {
                delayTimeToGetRecord();
            } else if (this.rType == DEL_SLEEP_RECORD) {
                doDeleteSleepRecord(this.recordType, this.opType);
            } else if (this.rType == DEL_RECORD) {
                doDeleteRecord(this.recordType, this.opType);
            } else if (this.rType == DEL_VELO_RECORD) {
                doDeleteVeloRecord(this.recordType, this.opType);
            } else {
                Log.e("rType28", "rType=" + this.rType);
                this.rType = 0;
                if (this.mioDeviceCallBack != null && RUN_CMD.CMD_StreamModeEnable == this.tempRunCmd) {
                    this.mConnectionTimerHandler.removeCallbacks(this.mDownWorkoutRecordTimeOut);
                }
            }
        } else if (this.mioDeviceCallBack == null) {
        } else {
            if (this.rType == GET_SLEEP_RECORD_N) {
                Log.e("rType130", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
            } else if (this.rType == GET_SLEEP_RECORD_T) {
                Log.e("rType131", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
            } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                Log.e("rType132", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.DidGetSleepTrackList_MIO(null, (byte) 48);
            } else if (this.rType == GET_RECORD_ADL_T) {
                Log.e("rType29", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetTodayADLRecord(null, (byte) 48);
            } else if (this.rType == GET_VELO_RECORD_N) {
                Log.e("rType30", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetVeloRecord(null, (short) 0, (short) 0, (byte) 48);
            } else if (this.rType == DEL_VELO_RECORD) {
                Log.e("rType31", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onDeleteVeloRecord(this.recordType, this.opType, (byte) 48);
            } else if (this.rType == DEL_RECORD) {
                Log.e("rType32", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onDeleteRecord(this.recordType, this.opType, (byte) 48);
            } else if (this.rType == GET_RECORD_ADL_D_N) {
                Log.e("rType33", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetRecordOfDailyADL(null, (short) 0, (short) 0, (byte) 48);
            } else if (this.rType == GET_RECORD_ADL_D_F) {
                Log.e("rType34", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetRecordOfDailyADL(null, (short) 0, (short) 1, (byte) 48);
            } else if (this.rType == GET_RECORD_WO_N) {
                Log.e("rType35", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetWorkoutRecord(null, (short) 0, (short) this.workoutRecordIndex, (byte) 48);
            } else if (this.rType == GET_RECORD_WO_T) {
                Log.e("rType36", "rType=" + this.rType);
                this.rType = 0;
                this.mioDeviceCallBack.onGetTotalNumbersOfWorkoutRecord((short) 0, (byte) 48);
            }
        }
    }

    private void delayTimeToGetRecord() {
        new Thread(new Runnable() {
            public void run() {
                MioDeviceConnection.this.bleCmdState = true;
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MioDeviceConnection.this.bleCmdState = false;
                Log.e("delayTimeToGetRecord", "needGoOnDownLoadRecord=" + MioDeviceConnection.this.needGoOnDownLoadRecord);
                if (MioDeviceConnection.this.needGoOnDownLoadRecord) {
                    MioDeviceConnection.this.doResumeDownLoadRecord();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_N) {
                    MioDeviceConnection.this.doGetSleepRecord_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_T) {
                    MioDeviceConnection.this.doGetSleepRecord_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_CURHOUR_N) {
                    MioDeviceConnection.this.doGetCurHourSleepRecord_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_T) {
                    MioDeviceConnection.this.doGetTodayADLRecord_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_F) {
                    MioDeviceConnection.this.doGetRecordOfDailyADL_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_WO_N || MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_WO_T) {
                    MioDeviceConnection.this.doGetWorkoutRecord_MIO();
                } else if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_VELO_RECORD_N) {
                    MioDeviceConnection.this.doGetVeloRecord_MIO();
                } else if (MioDeviceConnection.this.mioDeviceCallBack != null) {
                    MioDeviceConnection.this.mioDeviceCallBack.onSyncRecordTimeOut(49);
                }
            }
        }).start();
    }

    private void doDeviceStatusMsgRsp(byte[] raw) {
        byte msgCode = raw[GET_RECORD_ADL_D_F];
        FuseDeviceStatus status = new FuseDeviceStatus();
        if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
            if ((raw[GET_RECORD_ADL_D_N] & DEL_RECORD) == DEL_RECORD) {
                status.streamModeStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
                status.GPSModeStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & GET_RECORD_ADL_D_N) == GET_RECORD_ADL_D_N) {
                status.StepDataNotificationStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & 8) == 8) {
                status.ExeTimerSyncStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & GET_VELO_RECORD_N) == GET_VELO_RECORD_N) {
                status.ADLModeStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & 32) == 32) {
                status.WOModeStatus = true;
            }
            if ((raw[GET_RECORD_ADL_D_N] & 64) == 64) {
                status.SleepModeStatus = true;
            }
            if ((raw[GET_RECORD_WO_T] & DEL_RECORD) == DEL_RECORD) {
                status.woRecordMemFull = true;
            }
            if ((raw[GET_RECORD_WO_T] & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
                status.woLogMemFull = true;
            }
            if ((raw[GET_RECORD_WO_T] & GET_RECORD_ADL_D_N) == GET_RECORD_ADL_D_N) {
                status.woLogMemLow = true;
            }
            if ((raw[GET_RECORD_WO_T] & 8) == 8) {
                status.fuseHasRecord = true;
            }
            if ((raw[GET_RECORD_WO_T] & GET_VELO_RECORD_N) == GET_VELO_RECORD_N) {
                status.fuseHasWORecord = true;
            }
            if ((raw[GET_RECORD_WO_T] & 32) == 32) {
                status.actDataMemFull = true;
            }
            if (this.mioDeviceCallBack != null) {
                this.mioDeviceCallBack.onGetDeviceStatus(msgCode, status);
            }
        }
        if (this.deviceName.endsWith("ALPHA2") && (this.rType == GET_RECORD_ADL_D_N || this.rType == GET_RECORD_ADL_D_F || this.rType == GET_RECORD_ADL_T || this.rType == DEL_RECORD)) {
            status.streamModeStatus = true;
        }
        if (status.streamModeStatus) {
            if (this.rType == GET_SLEEP_RECORD_N || this.rType == GET_SLEEP_RECORD_T) {
                this.isSleepFullMemory = status.actDataMemFull;
                doGetSleepRecord_MIO();
            } else if (this.rType == DEL_SLEEP_RECORD) {
                doDeleteSleepRecord(this.recordType, this.opType);
            } else if (this.rType == GET_SLEEP_RECORD_CURHOUR_N) {
                doGetCurHourSleepRecord_MIO();
            } else if (this.rType == GET_RECORD_ADL_T) {
                doGetTodayADLRecord_MIO();
            } else if (this.rType == GET_RECORD_ADL_D_N || this.rType == GET_RECORD_ADL_D_F) {
                doGetRecordOfDailyADL_MIO();
            } else if (this.rType == GET_RECORD_WO_N || this.rType == GET_RECORD_WO_T) {
                doGetWorkoutRecord_MIO();
            } else if (this.rType == DEL_RECORD) {
                doDeleteRecord(this.recordType, this.opType);
            }
        } else if ((this.rType == GET_RECORD_ADL_T || this.rType == DEL_RECORD || this.rType == GET_RECORD_ADL_D_F || this.rType == GET_RECORD_ADL_D_N || this.rType == GET_RECORD_WO_T || this.rType == GET_RECORD_WO_N || this.rType == GET_SLEEP_RECORD_N || this.rType == GET_SLEEP_RECORD_T || this.rType == DEL_SLEEP_RECORD || this.rType == GET_SLEEP_RECORD_CURHOUR_N) && !SendRunCmd_MIO(RUN_CMD.CMD_StreamModeEnable, false)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!MioDeviceConnection.this.SendRunCmd_MIO(RUN_CMD.CMD_StreamModeEnable, false) && MioDeviceConnection.this.mioDeviceCallBack != null) {
                        MioDeviceConnection.this.mioDeviceCallBack.onSendRunCmd_MIO((byte) 33);
                    }
                }
            }).start();
        }
    }

    private void doVeloDeviceStatusMsgRsp(byte[] raw) {
        byte msgCode = raw[GET_RECORD_ADL_D_F];
        VeloDeviceStatus status = new VeloDeviceStatus();
        if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
            if ((raw[GET_RECORD_ADL_D_N] & DEL_RECORD) == DEL_RECORD) {
                status.streamModeStatus = true;
            }
            if ((raw[GET_RECORD_WO_T] & DEL_RECORD) == DEL_RECORD) {
                status.SummaryOverwrite = true;
            }
            if ((raw[GET_RECORD_WO_T] & GET_RECORD_ADL_T) == GET_RECORD_ADL_T) {
                status.RawDataOverwrite = true;
            }
            if ((raw[GET_RECORD_WO_T] & 8) == 8) {
                status.VeloHasRecord = true;
            }
            if (this.mioDeviceCallBack != null) {
                this.mioDeviceCallBack.onGetVeloDeviceStatus(msgCode, status);
            }
        }
        if (status.streamModeStatus) {
            if (this.rType == GET_VELO_RECORD_N) {
                doGetVeloRecord_MIO();
            } else if (this.rType == DEL_VELO_RECORD) {
                doDeleteVeloRecord(this.recordType, this.opType);
            }
        } else if ((this.rType == GET_VELO_RECORD_N || this.rType == DEL_VELO_RECORD) && !SendRunCmd_MIO(RUN_CMD.CMD_StreamModeEnable, false)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!MioDeviceConnection.this.SendRunCmd_MIO(RUN_CMD.CMD_StreamModeEnable, false) && MioDeviceConnection.this.mioDeviceCallBack != null) {
                        MioDeviceConnection.this.mioDeviceCallBack.onSendRunCmd_MIO((byte) 33);
                    }
                }
            }).start();
        }
    }

    public boolean GetFirstRecordOfDailyADL_MIO() {
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || this.bleCmdState || !this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            return false;
        }
        this.rType = GET_RECORD_ADL_D_F;
        this.getAllRecordFlag = false;
        this.delRecordFlag = false;
        return GetDeviceStatus_MIO();
    }

    public boolean GetRecordOfDailyADL_MIO(boolean delRecordsAfterGet) {
        if (!this.isConnected) {
            Log.e("IsRecordSupported_MIO", "isConnected");
            return false;
        } else if (this.rType != 0) {
            Log.e("IsRecordSupported", "rType=" + this.rType);
            return true;
        } else if (this.bluetoothGatt == null) {
            Log.e("IsRecordSupported_MIO", "bluetoothGatt");
            return false;
        } else if (this.bleCmdState) {
            Log.e("IsRecordSupported", "bleCmdState=" + this.bleCmdState);
            return true;
        } else if (this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            this.rType = GET_RECORD_ADL_D_F;
            this.getAllRecordFlag = true;
            this.delRecordFlag = delRecordsAfterGet;
            return GetDeviceStatus_MIO();
        } else {
            Log.e("IsRecordSupported_MIO", "IsRecordSupported_MIO=" + this.deviceName);
            return false;
        }
    }

    private void doGetNextRecordOfDailyADL_MIO() {
        this.rType = GET_RECORD_ADL_D_N;
        doGetRecordOfDailyADL_MIO();
    }

    private void doGetRecordOfDailyADL_MIO() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte rix = (byte) 0;
                if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_ADL_D_N) {
                    rix = (byte) 1;
                }
                byte[] delCommand;
                if (MioDeviceConnection.this.deviceName.endsWith("ALPHA2")) {
                    delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
                    delCommand[0] = (byte) 2;
                    delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK2_MEM_RECORD_GET;
                    delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 2;
                    delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = rix;
                    MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_TODAY_ADL_RECORD_GET, delCommand);
                } else {
                    delCommand = new byte[MioDeviceConnection.GET_RECORD_ADL_D_N];
                    delCommand[0] = (byte) 3;
                    delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_GET;
                    delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 2;
                    delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = rix;
                    MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_TODAY_ADL_RECORD_GET, delCommand);
                }
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 100000);
            }
        }.start();
    }

    private void GetSession_MIO(int sessionID) {
        Log.e("GetSession_MIO", "sessionID=" + sessionID);
        new AnonymousClass26(sessionID).start();
    }

    private void GetVeloSession_MIO(int sessionID) {
        Log.e("GetVeloSession_MIO", "sessionID=" + sessionID);
        byte[] delCommand = new byte[GET_RECORD_ADL_D_N];
        delCommand[0] = (byte) 2;
        delCommand[DEL_RECORD] = MioHelper.MSG_VELO_MEM_SESSION_GET;
        delCommand[GET_RECORD_ADL_T] = (byte) (sessionID & 255);
        delCommand[GET_RECORD_ADL_D_F] = (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & sessionID) >> 8);
        commonBluetoothSetting(CMD_TYPE.CMD_TYPE_VELO_MEM_SESSION_GET, delCommand);
    }

    public boolean GetNextRecordOfDailyADL_MIO() {
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.rType = GET_RECORD_ADL_D_N;
        return GetDeviceStatus_MIO();
    }

    public boolean GetWorkoutRecord_MIO(boolean delRecordsAfterGet) {
        if ((this.needGoOnDownLoadRecord && this.rType == GET_RECORD_WO_N) || this.rType != 0 || this.bleCmdState) {
            return true;
        }
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.workoutRecordIndex = DEL_RECORD;
        this.rType = GET_RECORD_WO_N;
        this.getAllRecordFlag = true;
        this.delRecordFlag = delRecordsAfterGet;
        return GetDeviceStatus_MIO();
    }

    public boolean GetTotalNumbersOfWorkoutRecord_MIO() {
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || !this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            return false;
        }
        this.workoutRecordIndex = 0;
        this.rType = GET_RECORD_WO_T;
        this.getAllRecordFlag = false;
        this.delRecordFlag = false;
        return GetDeviceStatus_MIO();
    }

    public boolean GetWorkoutRecord_MIO(short index) {
        if (!this.isConnected || this.rType != 0) {
            return false;
        }
        this.workoutRecordIndex = index;
        this.rType = GET_RECORD_WO_N;
        this.getAllRecordFlag = false;
        this.delRecordFlag = false;
        return GetDeviceStatus_MIO();
    }

    private void doGetWorkoutRecord_MIO(int index) {
        this.workoutRecordIndex = index;
        this.rType = GET_RECORD_WO_N;
        doGetWorkoutRecord_MIO();
    }

    private void doGetWorkoutRecord_MIO() {
        new Thread(new Runnable() {
            public void run() {
                byte rix = (byte) 0;
                if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_RECORD_WO_N) {
                    rix = (byte) 2;
                }
                byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_WO_T];
                delCommand[0] = (byte) 3;
                delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_GET;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 1;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = rix;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_N] = (byte) MioDeviceConnection.this.workoutRecordIndex;
                MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_RECORD_GET, delCommand);
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 120000);
            }
        }).start();
    }

    public boolean DeleteRecord_MIO(RType type, DelOPType opType) {
        if (!this.isConnected || type == RType.TYPE_ADL_TODAY || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.rType = DEL_RECORD;
        this.recordType = type;
        this.opType = opType;
        return GetDeviceStatus_MIO();
    }

    public boolean DeleteVeloRecord_MIO(RType type, DelOPType opType) {
        if (!this.isConnected || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.rType = DEL_VELO_RECORD;
        this.recordType = type;
        this.opType = opType;
        return GetVeloDeviceStatus_MIO();
    }

    private boolean doDeleteRecord(RType type, DelOPType opType) {
        if (!this.isConnected || type == RType.TYPE_ADL_TODAY || this.bluetoothGatt == null) {
            return false;
        }
        new AnonymousClass28(type, opType).start();
        return true;
    }

    private boolean doDeleteVeloRecord(RType type, DelOPType opType) {
        Log.e("doDeleteVeloRecord", "doDeleteVeloRecord1");
        if (!this.isConnected || this.bluetoothGatt == null) {
            return false;
        }
        new Thread(new AnonymousClass29(opType)).start();
        return true;
    }

    private void enableSenserDataRespNotification() {
        if (this.bluetoothGatt != null && this.mCharacteristicMioSenserData != null) {
            boolean set = this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicMioSenserData, true);
            Log.e("enSenDataRespNotif", "--------mCharacteristicMioSenserData=" + set);
            if (!set) {
                this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicMioSenserData, true);
                Log.e("enSenDataRespNotif", "--------mCharacteristicMioSenserData=" + set);
            }
            BluetoothGattDescriptor descriptor = this.mCharacteristicMioSenserData.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
            if (descriptor == null) {
                this.mCharacteristicMioSenserData.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
                List<BluetoothGattDescriptor> list = this.mCharacteristicMioSenserData.getDescriptors();
                if (list == null) {
                    Log.e("enSenDataRespNotif", "BluetoothGattDescriptorSize=0");
                } else {
                    Log.e("enSenDataRespNotif", "BluetoothGattDescriptorSize=" + list.size());
                }
                for (int i = 0; i < list.size(); i += DEL_RECORD) {
                    Log.e("enSenDataRespNotif", "descriptor_UID=" + ((BluetoothGattDescriptor) list.get(i)).getUuid());
                }
            }
            if (descriptor != null) {
                byte[] value = descriptor.getValue();
                if (value == null || value != BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Log.e("writeDescriptor", "writeDescriptor5");
                    if (this.bluetoothGatt.writeDescriptor(descriptor)) {
                        Log.e("enSenDataRespNotif", "--------enableSenserDataRespNotification  descriptor false");
                        return;
                    }
                    return;
                }
                return;
            }
            Log.e("enaSenDataRespNotif", "--------enableSenserDataRespNotification  descriptor null");
        }
    }

    private void enableRecordMsgRespNotification() {
        if (this.bluetoothGatt != null && this.mCharacteristicMioRecordMsg != null) {
            try {
                boolean set = this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicMioRecordMsg, true);
                System.out.println("--------setCharacteristicNotification=" + set);
                if (!set) {
                    this.bluetoothGatt.setCharacteristicNotification(this.mCharacteristicMioRecordMsg, true);
                    System.out.println("--------setCharacteristicNotification=" + set);
                }
                BluetoothGattDescriptor descriptor = this.mCharacteristicMioRecordMsg.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
                if (descriptor == null) {
                    this.mCharacteristicMioRecordMsg.getDescriptor(MioHelper.UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR);
                    List<BluetoothGattDescriptor> list = this.mCharacteristicMioRecordMsg.getDescriptors();
                    if (list == null) {
                        System.out.println("BluetoothGattDescriptorSize=0");
                    } else {
                        System.out.println("BluetoothGattDescriptorSize=" + list.size());
                    }
                    for (int i = 0; i < list.size(); i += DEL_RECORD) {
                        System.out.println("descriptor_UID=" + ((BluetoothGattDescriptor) list.get(i)).getUuid());
                    }
                }
                if (descriptor != null) {
                    byte[] value = descriptor.getValue();
                    if (value == null || value != BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        Log.e("writeDescriptor", "writeDescriptor5");
                        if (this.bluetoothGatt.writeDescriptor(descriptor)) {
                            System.out.println("--------enableRecordMsgRespNotification  descriptor false");
                            return;
                        }
                        return;
                    }
                    return;
                }
                System.out.println("--------enableRecordMsgRespNotification  descriptor null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readAlpha2BatteryLevel_MIO() {
        if (this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        byte cmd = MioHelper.LINK_CUST_CMD;
        byte[] getCommand = new byte[GET_RECORD_ADL_D_F];
        getCommand[0] = (byte) 1;
        getCommand[DEL_RECORD] = cmd;
        getCommand[GET_RECORD_ADL_T] = (byte) 1;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_LINK_CUST_CMD, getCommand);
    }

    public boolean GetExerciseSetting_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_ADL_T];
        command[DEL_RECORD] = MioHelper.LINK_EXER_SETTINGS_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public boolean SetExerciseSetting_MIO(ExerciseSetting exeSetting) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        byte flag = (byte) 0;
        if (!exeSetting.recoveryTimeIntoTotalTime) {
            flag = (byte) DEL_RECORD;
        }
        if (exeSetting.repeatFlag) {
            flag = (byte) (flag | GET_RECORD_ADL_T);
        }
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, new byte[]{(byte) 5, MioHelper.LINK_EXER_SETTINGS_SET, flag, exeSetting.timeHour, exeSetting.timeMinute, exeSetting.recoveryTimeMinute, exeSetting.recoveryTimeSecond});
    }

    public boolean GetUserScreen_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_ADL_T];
        command[DEL_RECORD] = MioHelper.LINK_USER_SCREEN_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public boolean SetUserScreen_MIO(UserScreenData userScreen) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("ALPHA2") || userScreen == null) {
            return false;
        }
        byte[] command = new byte[RESET_ADL_TODAY_DATA];
        command[0] = (byte) 10;
        command[DEL_RECORD] = MioHelper.LINK_USER_SCREEN_SET;
        command[GET_RECORD_ADL_T] = MioSportMsgParserUtil.getCodeUS1(userScreen.us1_1);
        command[GET_RECORD_ADL_D_F] = MioSportMsgParserUtil.getCodeUS1(userScreen.us1_2);
        command[GET_RECORD_ADL_D_N] = MioSportMsgParserUtil.getCodeUS1(userScreen.us1_3);
        command[GET_RECORD_WO_T] = MioSportMsgParserUtil.getCodeUS1(userScreen.us1_4);
        command[GET_RECORD_WO_N] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_1);
        command[7] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_2);
        command[8] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_3);
        command[9] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_4);
        command[10] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_5);
        command[ENABLE_STREAM_MODE] = MioSportMsgParserUtil.getCodeUS2(userScreen.us2_6);
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public boolean GetAlpha2Record_MIO(boolean delRecordsAfterGet) {
        if (this.needGoOnDownLoadRecord && this.rType == GET_ALPHA2_RECORD_N) {
            return true;
        }
        if (!this.isConnected || this.rType != 0 || this.mCharacteristicMioSportMsg == null || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        if (!this.deviceName.contains("ALPHA2")) {
            return false;
        }
        this.getAllRecordFlag = true;
        this.delRecordFlag = delRecordsAfterGet;
        return doGetAlpha2Record_MIO(DEL_RECORD);
    }

    private boolean doGetAlpha2Record_MIO(int index) {
        this.workoutRecordIndex = index;
        this.rType = GET_ALPHA2_RECORD_N;
        byte ix = (byte) 0;
        if (index > DEL_RECORD) {
            ix = (byte) 1;
        }
        byte[] command = new byte[GET_RECORD_ADL_D_N];
        command[0] = (byte) 2;
        command[DEL_RECORD] = MioHelper.LINK2_MEM_RECORD_GET;
        command[GET_RECORD_ADL_T] = (byte) 1;
        command[GET_RECORD_ADL_D_F] = ix;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    private boolean GetRecordPacket_MIO(int index) {
        byte[] command = new byte[GET_RECORD_ADL_D_N];
        command[0] = (byte) 2;
        command[DEL_RECORD] = MioHelper.LINK2_MEM_NEXT_PACKET_GET;
        command[GET_RECORD_ADL_T] = (byte) (index & 255);
        command[GET_RECORD_ADL_D_F] = (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & index) >> 8);
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public boolean DeleteAlpha2Record_MIO(DelOPType type) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bluetoothGatt == null || this.bleCmdState || !this.deviceName.contains("ALPHA2")) {
            return false;
        }
        int i;
        this.opType = type;
        if (type == DelOPType.DELETE_ALL_RECORD) {
            i = DEL_RECORD;
        } else {
            i = 0;
        }
        byte dr = (byte) i;
        byte[] command = new byte[GET_RECORD_ADL_D_N];
        command[0] = (byte) 2;
        command[DEL_RECORD] = MioHelper.LINK2_MEM_RECORD_DELETE;
        command[GET_RECORD_ADL_T] = (byte) 1;
        command[GET_RECORD_ADL_D_F] = dr;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public boolean ResetDevice_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !this.deviceName.contains("FUSE")) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_WO_N];
        command[0] = (byte) 4;
        command[DEL_RECORD] = MioHelper.LINK_RESET_CMD;
        command[GET_RECORD_ADL_T] = (byte) 53;
        command[GET_RECORD_ADL_D_F] = (byte) 70;
        command[GET_RECORD_ADL_D_N] = (byte) 82;
        command[GET_RECORD_WO_T] = (byte) 110;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_NONE, command);
    }

    public MIODevicesType getDeviceNameType(String devicename) {
        MIODevicesType type = MIODevicesType.MIO_DEVICE_UNKNOW;
        if (TextUtils.isEmpty(devicename)) {
            return type;
        }
        if (devicename.endsWith("LINK") || devicename.toUpperCase().endsWith("OTBEAT MIO LINK")) {
            type = MIODevicesType.MIO_DEVICE_LINK;
        } else if (devicename.endsWith("VELO")) {
            type = MIODevicesType.MIO_DEVICE_VELO;
        } else if (devicename.endsWith("FUSE")) {
            type = MIODevicesType.MIO_DEVICE_FUSE;
        } else if (devicename.endsWith("ALPHA2")) {
            type = MIODevicesType.MIO_DEVICE_ALPHA2;
        } else if (devicename.endsWith("ALPHA2_OTA")) {
            type = MIODevicesType.MIO_DEVICE_ALPHA2_OTA;
        } else if (devicename.startsWith("MIOUP1.1")) {
            type = MIODevicesType.MIO_DEVICE_LINK_OTA;
        } else if (devicename.startsWith("MIOUP")) {
            type = MIODevicesType.MIO_DEVICE_UNKNOW;
        }
        return type;
    }

    public boolean SetStrideCali_MIO(StridCaliData caliData) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte flag = (byte) 0;
        if (caliData.strideCaliMode && !caliData.strideCaliDataClr) {
            flag = (byte) 1;
        }
        if (!caliData.strideCaliMode && caliData.strideCaliDataClr) {
            flag = Byte.MIN_VALUE;
        }
        byte[] command = new byte[GET_RECORD_WO_T];
        command[0] = (byte) 3;
        command[DEL_RECORD] = MioHelper.LINK_STRIDE_CALI_SET;
        command[GET_RECORD_ADL_T] = flag;
        command[GET_RECORD_ADL_D_F] = (byte) caliData.caliWalkFactor;
        command[GET_RECORD_ADL_D_N] = (byte) caliData.caliRunFactor;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_STRIDE_CALI_SET, command);
    }

    public boolean GetStrideCali_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_ADL_T];
        command[DEL_RECORD] = MioHelper.LINK_STRIDE_CALI_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_STRIDE_CALI_GET, command);
    }

    public boolean DeviceUpdate(String deviceName) {
        this.updateDeviceType = getDeviceNameType(deviceName);
        if (CheckDeviceNameAndType(deviceName)) {
            if (this.deviceDFUCallBack == null) {
                return false;
            }
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_ALPHA2) {
                if (this.commonDFUManager != null) {
                    this.commonDFUManager.stopUpdate();
                    this.commonDFUManager = null;
                }
                if (this.alpha2DFUManager == null) {
                    this.alpha2DFUManager = new Alpha2DFUManager();
                }
            } else if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_UNKNOW) {
                return false;
            } else {
                if (this.alpha2DFUManager != null) {
                    this.alpha2DFUManager.stopUpdate();
                    this.alpha2DFUManager = null;
                }
                if (this.commonDFUManager == null) {
                    this.commonDFUManager = new CommonDFUManager();
                }
            }
            return EnterDFUModeCommond();
        } else if (this.deviceDFUCallBack == null) {
            return false;
        } else {
            this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_NAME_AND_TYPE_NOT_MAP, DEL_RECORD);
            return false;
        }
    }

    public boolean StartDFU(String deviceName, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            if (this.deviceDFUCallBack != null) {
                this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_FILE_NOT_EXITS, GET_ALPHA2_RECORD_N);
            }
            Log.e("StartDFU", "StartDFU--44");
            return false;
        }
        Log.e("StartDFU", deviceName);
        Log.e("StartDFU", filePath);
        this.updateFilePath = filePath;
        if (deviceName != null && deviceName.length() > 0) {
            Log.e("StartDFU", "StartDFU--21");
            if (this.commonDFUManager == null && this.alpha2DFUManager == null) {
                Log.e("StartDFU", "StartDFU--222");
                resetBlueGratt();
            }
            if (this.commonDFUManager != null) {
                Log.e("StartDFU", "StartDFU--22");
                this.commonDFUManager.resetStatus();
                this.commonDFUManager.setInitParm(this.bluetoothAdapter, this.bluetoothGatt, this.deviceDFUCallBack, filePath, deviceName);
                this.commonDFUManager.setGattCharacteristic(this.mCharacteristicDFUPacket, this.mCharacteristicDFUControlPoint);
                this.isDeviceUpdate = true;
                Log.e("StartDFU", "StartDFU--2");
                return this.commonDFUManager.startUpdate(filePath);
            } else if (this.alpha2DFUManager != null) {
                Log.e("StartDFU", "StartDFU--32");
                this.alpha2DFUManager.resetStatus();
                this.alpha2DFUManager.setInitParm(this.bluetoothAdapter, this.bluetoothGatt, this.deviceDFUCallBack, filePath, deviceName);
                this.alpha2DFUManager.setGattCharacteristic(this.mCharacteristicDFUPacket, this.mCharacteristicDFUControlPoint, this.mCharacteristicAlpha2DFUSendPackage);
                this.isDeviceUpdate = true;
                Log.e("StartDFU", "StartDFU--3");
                return this.alpha2DFUManager.startUpdate(filePath);
            }
        }
        Log.e("StartDFU", "StartDFU--1");
        return false;
    }

    private boolean EnterDFUModeCommond() {
        Log.e("EnterDFUModeCommond", "EnterDFUModeCommond--1");
        Log.v("DeviceConnector", " enterDFUMode(context)");
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        if (this.bluetoothGatt == null || this.mCharacteristicMioSportMsg == null || this.bluetoothAdapter == null || !this.bluetoothAdapter.isEnabled()) {
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            if (this.deviceDFUCallBack != null) {
                Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
                this.deviceDFUCallBack.onError(DFUCallbacks.MIO_DFU_ERROR_MSG_INTERNAL_ERROR, 9);
            }
            Log.e("EnterDFUModeCommond", "EnterDFUModeCommond--2");
            return false;
        }
        Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
        if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_LINK || this.updateDeviceType == MIODevicesType.MIO_DEVICE_VELO) {
            Log.e("LINK_or_velo:", "biantao--" + MIOCommon.MSG_CMD_ENABLE_DFU_MODE_FUSE);
            this.mCharacteristicMioSportMsg.setValue(MIOCommon.MSG_CMD_ENABLE_DFU_MODE_LINK_OR_VELO);
        } else if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_FUSE) {
            Log.e("FUSE:", "biantao" + MIOCommon.MSG_CMD_ENABLE_DFU_MODE_FUSE);
            this.mCharacteristicMioSportMsg.setValue(MIOCommon.MSG_CMD_ENABLE_DFU_MODE_FUSE);
        } else if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_ALPHA2) {
            Log.e("ALPHA2:", "biantao" + MIOCommon.MSG_CMD_ENABLE_DFU_MODE_FUSE);
            this.mCharacteristicMioSportMsg.setValue(MIOCommon.MSG_CMD_ENABLE_DFU_MODE_ALPHA2);
        }
        this.bleCmdState = this.bluetoothGatt.writeCharacteristic(this.mCharacteristicMioSportMsg);
        if (this.bleCmdState) {
            this.autoRetry = true;
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_LINK || this.updateDeviceType == MIODevicesType.MIO_DEVICE_VELO) {
                this.cmdType = CMD_TYPE.CMD_TYPE_LINK_ENTER_DFUMODE;
            } else if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_FUSE) {
                this.cmdType = CMD_TYPE.CMD_TYPE_LINK_ENTER_DFUMODE;
            } else if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_ALPHA2) {
                this.cmdType = CMD_TYPE.CMD_TYPE_ALPHA2_ENTER_DFUMODE;
            }
            this.mConnectionTimerHandler.removeCallbacks(this.mBluetoothSettingTimeOut);
            this.mConnectionTimerHandler.postDelayed(this.mBluetoothSettingTimeOut, 10000);
            Log.e("EnterDFUModeCommond", "EnterDFUModeCommond--3");
            return true;
        }
        Log.e("EnterDFUModeCommond", "EnterDFUModeCommond--4");
        return false;
    }

    public void StopUpdate() {
        if (this.commonDFUManager != null) {
            this.commonDFUManager.stopUpdate();
            this.commonDFUManager.close();
            this.commonDFUManager = null;
        }
        if (this.alpha2DFUManager != null) {
            this.alpha2DFUManager.stopUpdate();
            this.alpha2DFUManager.close();
            this.alpha2DFUManager = null;
        }
        this.isDeviceUpdate = false;
    }

    public boolean IsDFUMode(String deviceName) {
        String devicename = deviceName.toUpperCase();
        if (devicename.startsWith("MIOUP") || devicename.endsWith("ALPHA2_OTA")) {
            return true;
        }
        return false;
    }

    private boolean CheckDeviceNameAndType(String deviceName) {
        String mDeviceName = deviceName;
        boolean ret;
        if (mDeviceName.endsWith("LINK") || mDeviceName.toUpperCase().endsWith("OTBEAT MIO LINK")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_LINK) {
                return true;
            }
            return false;
        } else if (mDeviceName.endsWith("VELO")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_VELO) {
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        } else if (mDeviceName.endsWith("FUSE")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_FUSE) {
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        } else if (mDeviceName.endsWith("ALPHA2")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_ALPHA2) {
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        } else if (mDeviceName.endsWith("ALPHA2_OTA")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_ALPHA2) {
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        } else if (mDeviceName.startsWith("MIOUP1.1")) {
            if (this.updateDeviceType == MIODevicesType.MIO_DEVICE_LINK) {
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        } else if (mDeviceName.startsWith("MIOUP")) {
            return true;
        } else {
            if (mDeviceName.endsWith("TEST")) {
                return true;
            }
            return false;
        }
    }

    public void setIsEnterDFUMode(boolean flag) {
        this.isEnterDFUMode = flag;
    }

    public void resetBlueGratt() {
        try {
            if (this.bluetoothGatt != null) {
                this.bluetoothGatt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bluetoothAdapter.disable();
        sleep(2000);
        this.bluetoothAdapter.enable();
        sleep(5000);
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isAutoReconnection() {
        if (!this.autoRetry || this.retryCount >= GET_RECORD_ADL_D_N) {
            return false;
        }
        return true;
    }

    public boolean SendCMD(CMD_TYPE cmd) {
        if (!this.isDebug || cmd != CMD_TYPE.CMD_TYPE_FACTORY_DEFAULT) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_WO_N];
        command[0] = (byte) 4;
        command[DEL_RECORD] = MioHelper.FUSE_FACTORY_DEFAULT;
        command[GET_RECORD_ADL_T] = (byte) 53;
        command[GET_RECORD_ADL_D_F] = (byte) 70;
        command[GET_RECORD_ADL_D_N] = (byte) 82;
        command[GET_RECORD_WO_T] = (byte) 110;
        return commonBluetoothSetting(cmd, command);
    }

    public boolean SetMisc1_MIO(MIOMisc1Data misc1Data) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || misc1Data == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        if (misc1Data != null && misc1Data.RHRMeasCtrl_SLP_NUM >= (byte) 5 && misc1Data.RHRMeasCtrl_SLP_NUM <= (byte) 60 && misc1Data.RHRMeasCtrl_ADL_WO_NUM >= (byte) 5 && misc1Data.RHRMeasCtrl_ADL_WO_NUM <= (byte) 60) {
            byte flag = (byte) 0;
            if (misc1Data.SwingArmDetection == (byte) 1) {
                flag = (byte) DEL_RECORD;
            }
            if (misc1Data.SwingArmSelect_ADL == (byte) 1) {
                flag = (byte) (flag | GET_RECORD_ADL_T);
            }
            if (misc1Data.SwingArmSelect_WO == (byte) 1) {
                flag = (byte) (flag | GET_RECORD_ADL_D_N);
            }
            if (misc1Data.RHRMeasCtrl_SLP == (byte) 1) {
                flag = (byte) (flag | 8);
            }
            if (misc1Data.RHRMeasCtrl_ADL_WO == (byte) 1) {
                flag = (byte) (flag | GET_VELO_RECORD_N);
            }
            if (misc1Data.MobileNotification == (byte) 1) {
                flag = (byte) (flag | 32);
            }
            byte[] setCommand = new byte[GET_SLEEP_RECORD_N];
            setCommand[0] = MioHelper.EventFlagNegativeAction;
            setCommand[DEL_RECORD] = MioHelper.LINK_MISC1_SET;
            setCommand[GET_RECORD_ADL_T] = flag;
            setCommand[GET_RECORD_ADL_D_N] = (byte) misc1Data.SAItemType_ADL;
            setCommand[GET_RECORD_WO_T] = (byte) misc1Data.SAItemType_WO;
            setCommand[GET_RECORD_WO_N] = misc1Data.RHRMeasCtrl_SLP_NUM;
            setCommand[7] = misc1Data.RHRMeasCtrl_ADL_WO_NUM;
            return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SWING_ARM_SET, setCommand);
        } else if (this.mioDeviceCallBack == null) {
            return false;
        } else {
            this.mioDeviceCallBack.DidSetMisc1_MIO((byte) 4);
            return false;
        }
    }

    public boolean GetMisc1_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] getCommand = new byte[GET_RECORD_ADL_T];
        getCommand[DEL_RECORD] = MioHelper.LINK_MISC1_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SWING_ARM_GET, getCommand);
    }

    public boolean GetVeloRecord_MIO(boolean delRecordsAfterGet) {
        if ((this.needGoOnDownLoadRecord && this.rType == GET_VELO_RECORD_N) || this.rType != 0 || this.bleCmdState) {
            return true;
        }
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.workoutRecordIndex = DEL_RECORD;
        this.rType = GET_VELO_RECORD_N;
        this.getAllRecordFlag = true;
        this.delRecordFlag = delRecordsAfterGet;
        return GetVeloDeviceStatus_MIO();
    }

    public boolean SendMobileEvent(int EventID, byte EventFlags, int CategoryID, int CategoryCount, int NotificationUID) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || !MioDeviceManager.GetMioDeviceManager_MIO().isNotification) {
            return false;
        }
        byte[] getCommand = new byte[ENABLE_STREAM_MODE];
        getCommand[0] = (byte) 9;
        getCommand[DEL_RECORD] = MioHelper.MOBILE_NOTIFICATION;
        getCommand[GET_RECORD_ADL_T] = (byte) 1;
        getCommand[GET_RECORD_ADL_D_N] = JSONScanner.EOI;
        getCommand[GET_RECORD_WO_N] = (byte) CategoryCount;
        getCommand[GET_RECORD_WO_T] = (byte) CategoryID;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_LINK_MOBILE_NOTIFICATION, getCommand);
    }

    public boolean SendMobileMsg(byte cmd, String msg) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null || msg == null || !MioDeviceManager.GetMioDeviceManager_MIO().isNotification) {
            return false;
        }
        msg = msg.toUpperCase();
        Log.e("SendMobileMsg", msg);
        int byteNum = GET_VELO_RECORD_N;
        CMD_TYPE cmdType = CMD_TYPE.CMD_TYPE_LINK_MOBILE_MSG_ALERT;
        if (cmd == MioHelper.MOBILE_MSG_ALERT) {
            cmdType = CMD_TYPE.CMD_TYPE_LINK_MOBILE_MSG_ALERT;
            byteNum = GET_SLEEP_RECORD_N;
        } else if (cmd == MioHelper.MOBILE_EMAIL_ALERT) {
            cmdType = CMD_TYPE.CMD_TYPE_LINK_MOBILE_EMAIL_ALERT;
        } else if (cmd != MioHelper.MOBILE_PHONE_ALERT) {
            return false;
        } else {
            cmdType = CMD_TYPE.CMD_TYPE_LINK_MOBILE_PHONE_ALERT;
        }
        byte[] getCommand = new byte[(byteNum + GET_RECORD_ADL_T)];
        getCommand[0] = (byte) byteNum;
        getCommand[DEL_RECORD] = cmd;
        int i = 0;
        while (i < msg.getBytes().length && i + GET_RECORD_ADL_T < getCommand.length) {
            getCommand[i + GET_RECORD_ADL_T] = msg.getBytes()[i];
            i += DEL_RECORD;
        }
        return commonBluetoothSetting(cmdType, getCommand);
    }

    public boolean isSupportMoblieEvent() {
        if (this.deviceName != null && this.deviceName.contains("FUSE")) {
            return true;
        }
        return false;
    }

    public boolean GetAllRecordOfActivity_MIO() {
        if ((this.needGoOnDownLoadRecord && this.rType == GET_SLEEP_RECORD_N) || this.rType != 0 || this.bleCmdState) {
            return true;
        }
        if (!this.isConnected || this.rType != 0 || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.isSleepFullMemory = false;
        this.workoutRecordIndex = DEL_RECORD;
        this.rType = GET_SLEEP_RECORD_N;
        this.getAllRecordFlag = true;
        this.delRecordFlag = false;
        return GetDeviceStatus_MIO();
    }

    private void doGetSleepRecord_MIO(int index) {
        this.workoutRecordIndex = index;
        this.rType = GET_SLEEP_RECORD_N;
        doGetSleepRecord_MIO();
    }

    private void doGetSleepRecord_MIO() {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte rix = (byte) 0;
                if (MioDeviceConnection.this.rType == MioDeviceConnection.GET_SLEEP_RECORD_N) {
                    rix = (byte) 2;
                }
                byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_WO_T];
                delCommand[0] = (byte) 3;
                delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_GET;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 5;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_F] = rix;
                delCommand[MioDeviceConnection.GET_RECORD_ADL_D_N] = (byte) MioDeviceConnection.this.workoutRecordIndex;
                MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SLEEP_RECORD_GET, delCommand);
                MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 120000);
            }
        }.start();
    }

    public boolean DeleteAllActivityRecord_MIO(RType type, DelOPType opType) {
        if (!this.isConnected || type == RType.TYPE_ADL_TODAY || this.bluetoothGatt == null || this.bleCmdState) {
            return false;
        }
        this.rType = DEL_SLEEP_RECORD;
        this.recordType = type;
        this.opType = opType;
        return GetDeviceStatus_MIO();
    }

    private boolean GetCurHourSleepRecord_MIO() {
        if (!this.isConnected || this.rType != 0 || this.bleCmdState || this.bluetoothGatt == null || !this.mioDeviceInformation.IsRecordSupported_MIO(this.deviceName)) {
            return false;
        }
        this.getAllRecordFlag = true;
        this.rType = GET_SLEEP_RECORD_CURHOUR_N;
        Log.e("GetCurHourSleepRec", "GetCurHourSleepRecord_MIO");
        return GetDeviceStatus_MIO();
    }

    private boolean doDeleteSleepRecord(RType type, DelOPType opType) {
        if (!this.isConnected || type == RType.TYPE_ADL_TODAY || this.bluetoothGatt == null) {
            return false;
        }
        new AnonymousClass31(type, opType).start();
        return true;
    }

    private void doGetCurHourSleepRecord_MIO() {
        if (this.mCharacteristicMioSportMsg != null && !this.bleCmdState && this.bluetoothGatt != null) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] delCommand = new byte[MioDeviceConnection.GET_RECORD_WO_T];
                    delCommand[0] = (byte) 3;
                    delCommand[MioDeviceConnection.DEL_RECORD] = MioHelper.LINK_MEM_RECORD_GET;
                    delCommand[MioDeviceConnection.GET_RECORD_ADL_T] = (byte) 6;
                    MioDeviceConnection.this.commonBluetoothSetting(CMD_TYPE.CMD_TYPE_SLEEP_RECORD_CURHOUR, delCommand);
                    MioDeviceConnection.this.mConnectionTimerHandler.removeCallbacks(MioDeviceConnection.this.mDownWorkoutRecordTimeOut);
                    MioDeviceConnection.this.mConnectionTimerHandler.postDelayed(MioDeviceConnection.this.mDownWorkoutRecordTimeOut, 5000);
                }
            }.start();
        }
    }

    public boolean SleepModeActivate_MIO() {
        return SendRunCmd_MIO(RUN_CMD.CMD_SleepModeActivate);
    }

    public boolean SleepModeDeactivate_MIO() {
        return SendRunCmd_MIO(RUN_CMD.CMD_SleepModeDeactivate);
    }

    public boolean RestHRTakeMeasurement_MIO() {
        return SendRunCmd_MIO(RUN_CMD.CMD_RestHRTakeMeasurement);
    }

    public boolean RestHRStopMeasurement_MIO() {
        return SendRunCmd_MIO(RUN_CMD.CMD_RestHRStopMeasurement);
    }

    public boolean RestHRSendMeasurementResults_MIO() {
        return SendRunCmd_MIO(RUN_CMD.CMD_RestHRSendMeasurementResults);
    }

    public boolean EndSYNC_MIO() {
        this.isEndSync = true;
        return GetRTCTime_MIO();
    }

    public boolean GetDeviceOption_MIO() {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_ADL_T];
        command[DEL_RECORD] = MioHelper.LINK_CUST_DEVICE_OPTION_GET;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DEVICE_OPTION_GET, command);
    }

    public boolean SetDeviceOption_MIO(byte optCode) {
        if (!this.isConnected || this.mCharacteristicMioSportMsg == null || this.bleCmdState || this.bluetoothGatt == null) {
            return false;
        }
        byte[] command = new byte[GET_RECORD_ADL_D_F];
        command[0] = (byte) 1;
        command[DEL_RECORD] = MioHelper.LINK_CUST_DEVICE_OPTION_SET;
        command[GET_RECORD_ADL_T] = optCode;
        return commonBluetoothSetting(CMD_TYPE.CMD_TYPE_DEVICE_OPTION_SET, command);
    }
}
