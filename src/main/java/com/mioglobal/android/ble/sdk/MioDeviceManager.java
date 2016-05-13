package com.mioglobal.android.ble.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
//import com.breeze.android.util.log.LogHelper;
import com.google.android.gms.location.GeofenceStatusCodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.builder.DiffResult;

public class MioDeviceManager {
    public static final int REQUEST_ENABLE_BT = 256;
    private static Map<String, MioDeviceConnection> connectionList = null;
    private static MioDeviceManager instance = null;
    private static final String version = "2.5.16";
    private int alpha2TranState;
    private BluetoothAdapter bluetoothAdapter;
    public MioDeviceConnection curConnection;
    private MioDeviceScanCallback deviceScanCallback;
    private int deviceType;
    private ArrayList<MioDeviceBriefInfo> devicesArray;
    public boolean isNotification;
    private boolean isScaning;
    private LeScanCallback leScanCallback;
    private Handler mScanHandler;
    private Runnable mScanTimeOut;
    private Map<String, String> notificationApps;
    private Context scanContext;
    private int scanTimeout;
    private Set<String> scannedDevicesList;
    private boolean stopScan;

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceManager.1 */
    class C02821 implements Runnable {
        C02821() {
        }

        public void run() {
            MioDeviceManager.this.StopDeviceScan_MIO();
            if (MioDeviceManager.this.deviceScanCallback != null) {
                MioDeviceManager.this.deviceScanCallback.OnScanCompleted_MIO();
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceManager.2 */
    class C02832 implements LeScanCallback {
        C02832() {
        }

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String address = device.getAddress();
            String name = device.getName();
            if (name == null) {
                name = new MioBleAdHelper(scanRecord).getDeviceName();
            }
            if (name == null) {
                return;
            }
            if (name.contains("LINK") || name.endsWith("FUSE") || name.toUpperCase().endsWith("OTBEAT MIO LINK") || name.endsWith("VELO") || name.contains("ALPHA") || name.contains("MIO GLOBAL") || name.startsWith("MIOUP") || name.endsWith("ALPHA2_OTA")) {
                String uid = DiffResult.OBJECTS_SAME_STRING;
                if (name.contains("VELO")) {
                    Log.e("createLeScanCallback", "-------" + name);
                    uid = MioDeviceManager.this.paserVeloSerial(scanRecord, name, address);
                }
                if (!MioDeviceManager.this.scannedDevicesList.contains(address)) {
                    MioSportMsgParserUtil.writeLogtoFile("LeScan", DiffResult.OBJECTS_SAME_STRING, "name=" + name + "-------address=" + address);
                    if (MioDeviceManager.this.deviceScanCallback != null) {
                        String dfuType = null;
                        if (name.toUpperCase().startsWith("MIOUP")) {
                            if (name.toUpperCase().equals("MIOUP1.1")) {
                                dfuType = "LINK";
                            } else {
                                String scanR = new String(scanRecord);
                                if (scanR != null && scanR.length() > 0) {
                                    if (scanR.indexOf("MIO GLOBAL LINK") > 0) {
                                        dfuType = "LINK";
                                    } else if (scanR.indexOf("MIO GLOBAL VELO") > 0) {
                                        dfuType = "VELO";
                                    } else if (scanR.indexOf("MIO GLOBAL FUSE") > 0) {
                                        dfuType = "FUSE";
                                    }
                                }
                            }
                        } else if (name.toUpperCase().endsWith("ALPHA2_OTA")) {
                            dfuType = "ALPHA2";
                        }
                        MioDeviceManager.this.deviceScanCallback.OnDeviceFound_MIO(name, address, uid, MioHelper.companyID, dfuType, rssi);
                    }
                }
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioDeviceManager.3 */
    class C02843 implements Runnable {
        C02843() {
        }

        public void run() {
            MioDeviceManager.this.isScaning = true;
            if (!MioDeviceManager.this.bluetoothAdapter.isEnabled() || MioDeviceManager.this.bluetoothAdapter.getScanMode() == 20) {
                MioDeviceManager.this.resetBLE(false);
            }
            if (!(MioDeviceManager.this.bluetoothAdapter == null || MioDeviceManager.this.leScanCallback == null)) {
                MioDeviceManager.this.bluetoothAdapter.stopLeScan(MioDeviceManager.this.leScanCallback);
                MioDeviceManager.this.sleep(1000);
            }
            MioDeviceManager.this.devicesArray.clear();
            MioDeviceManager.this.scannedDevicesList.clear();
            MioSportMsgParserUtil.writeLogtoFile("LeScan", DiffResult.OBJECTS_SAME_STRING, "---------startLeScan-----------");
            MioDeviceManager.this.leScanCallback = MioDeviceManager.this.createLeScanCallback();
            boolean openSucc = MioDeviceManager.this.bluetoothAdapter.startLeScan(MioDeviceManager.this.leScanCallback);
            int retryCount = 0;
            while (!openSucc && retryCount < 2) {
                MioDeviceManager.this.sleep(2000);
                openSucc = MioDeviceManager.this.bluetoothAdapter.startLeScan(MioDeviceManager.this.leScanCallback);
                retryCount++;
            }
            MioSportMsgParserUtil.writeLogtoFile("LeScan", DiffResult.OBJECTS_SAME_STRING, "startLeScan=" + openSucc);
            MioDeviceManager.this.mScanHandler.removeCallbacks(MioDeviceManager.this.mScanTimeOut);
            MioDeviceManager.this.mScanHandler.postDelayed(MioDeviceManager.this.mScanTimeOut, (long) (MioDeviceManager.this.scanTimeout * GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE));
        }
    }

    public interface MioDeviceScanCallback {
        void OnBluetoothClosed_MIO();

        void OnDeviceFound_MIO(String str, String str2, String str3, String str4, String str5, int i);

        void OnScanCompleted_MIO();
    }

    static {
        instance = null;
        connectionList = new HashMap();
    }

    public ArrayList<MioDeviceBriefInfo> getDevicesArray() {
        return this.devicesArray;
    }

    private String paserVeloSerial(byte[] scanRecond, String name, String address) {
        String uid = MioHelper.paserVeloSerial(scanRecond);
        Log.e("MioDeviceManager", "-------" + uid);
        if (uid.length() > 0) {
            MioDeviceBriefInfo item = new MioDeviceBriefInfo();
            item.deviceUID = uid;
            item.deviceName = name;
            item.deviceAddress = address;
            item.companyID = MioHelper.companyID;
            if (!isExsit(item)) {
                this.devicesArray.add(item);
            }
        }
        return uid;
    }

    public static String getSDKVersion() {
        return version;
    }

    private boolean isExsit(MioDeviceBriefInfo item) {
        for (int i = 0; i < this.devicesArray.size(); i++) {
            if (((MioDeviceBriefInfo) this.devicesArray.get(i)).deviceAddress.contains(item.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    private MioDeviceManager() {
        this.deviceScanCallback = null;
        this.leScanCallback = null;
        this.deviceType = 1;
        this.scanContext = null;
        this.bluetoothAdapter = null;
        this.isScaning = false;
        this.stopScan = false;
        this.scanTimeout = 60;
        this.scannedDevicesList = new HashSet();
        this.notificationApps = new HashMap();
        this.devicesArray = new ArrayList();
        this.alpha2TranState = 0;
        this.curConnection = null;
        this.isNotification = true;
        this.mScanHandler = new Handler();
        this.mScanTimeOut = new C02821();
        instance = this;
    }

    public static synchronized MioDeviceManager GetMioDeviceManager_MIO() {
        MioDeviceManager mioDeviceManager;
        synchronized (MioDeviceManager.class) {
            mioDeviceManager = instance == null ? new MioDeviceManager() : instance;
        }
        return mioDeviceManager;
    }

    public void SetMioDeviceScanCallback_MIO(MioDeviceScanCallback callback) {
        this.deviceScanCallback = callback;
    }

    private LeScanCallback createLeScanCallback() {
        return new C02832();
    }

    public void StartDeviceScan_MIO(Context context, int type, int timeout) {
        this.deviceType = type;
        this.scanContext = context.getApplicationContext();
        this.bluetoothAdapter = MioHelper.getBluetoothAdapter(this.scanContext);
        this.scanTimeout = timeout;
        if (this.bluetoothAdapter == null || !this.bluetoothAdapter.isEnabled()) {
            this.isScaning = false;
            if (this.deviceScanCallback != null) {
                this.deviceScanCallback.OnBluetoothClosed_MIO();
            }
        } else if (!this.isScaning) {
            doStartDeviceScan();
        }
    }

    public void resetBLE(boolean reset) {
        if (reset) {
            MioSportMsgParserUtil.writeLogtoFile("LeScan", DiffResult.OBJECTS_SAME_STRING, "---------BLE RESET-----------");
            this.bluetoothAdapter.disable();
            sleep(1000);
            this.bluetoothAdapter.enable();
            sleep(3000);
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doStartDeviceScan() {
        new Thread(new C02843()).start();
    }

    public void StopDeviceScan_MIO() {
        doStopDeviceScan();
    }

    private void doStopDeviceScan() {
        if (this.isScaning) {
            this.isScaning = false;
            this.mScanHandler.removeCallbacks(this.mScanTimeOut);
            Log.e("m13e",DiffResult.OBJECTS_SAME_STRING);
            try {
                if (!(this.bluetoothAdapter == null || this.leScanCallback == null)) {
                    MioSportMsgParserUtil.writeLogtoFile("LeScan", DiffResult.OBJECTS_SAME_STRING, "---------stopLeScan-----------");
                    this.bluetoothAdapter.stopLeScan(this.leScanCallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.scannedDevicesList != null && this.scannedDevicesList.size() > 0) {
                this.scannedDevicesList.clear();
            }
        }
    }

    public MioDeviceConnection GetMioDeviceConnection_MIO(String name, String address) {
        if (connectionList.containsKey(address)) {
            MioDeviceConnection temp = (MioDeviceConnection) connectionList.get(address);
            temp.setName(name);
            this.curConnection = temp;
            return temp;
        }
        MioDeviceConnection connection = new MioDeviceConnection(name, address);
        for (int i = 0; i < this.devicesArray.size(); i++) {
            MioDeviceBriefInfo item = (MioDeviceBriefInfo) this.devicesArray.get(i);
            if (item.deviceAddress.contains(address)) {
                if (item.deviceUID.length() > 0) {
                    connection.setDeviceUID(item.deviceUID);
                }
                connection.setCompanyID(item.companyID);
                connectionList.put(address, connection);
                this.curConnection = connection;
                return connection;
            }
        }
        connectionList.put(address, connection);
        this.curConnection = connection;
        return connection;
    }

    public void changeDeviceAdress(MioDeviceConnection connection, String newAddress, String oldAddress) {
        if (connectionList.containsValue(connection)) {
            connectionList.remove(oldAddress);
            connectionList.put(newAddress, connection);
        }
    }

    public boolean RemoveMioDeviceConnection_MIO1(MioDeviceConnection connection) {
        if (!connectionList.containsValue(connection)) {
            return false;
        }
        connection.Disconnect_MIO();
        connectionList.remove(connection);
        System.gc();
        return true;
    }

    public void DisconnectOtherDevice(MioDeviceConnection connection) {
        for (Entry<String, MioDeviceConnection> entry : connectionList.entrySet()) {
            MioDeviceConnection temp = (MioDeviceConnection) entry.getValue();
            if (connection != temp) {
                temp.ReConnectDisable();
                temp.SetBikeSensorCallBack_MIO(null);
                temp.setMioDeviceCallBack(null);
                temp.SetBikeSensorCallBack_MIO(null);
                temp.SetMioDeviceConnectionCallback_MIO(null);
                temp.SetMioDeviceHRMCallback_MIO(null);
                temp.SetMioDeviceRSSICallback_MIO(null);
                temp.SetMioDeviceBatteryCallback_MIO(null);
                temp.Disconnect_MIO();
            }
        }
    }

    public void enableBluetooth(Activity activity) {
        activity.startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
    }

    public boolean isBluetoothEnabled(Context context) {
        this.bluetoothAdapter = MioHelper.getBluetoothAdapter(context);
        if (this.bluetoothAdapter == null) {
            return false;
        }
        return this.bluetoothAdapter.isEnabled();
    }

    public void resetAlpha2TranState() {
        this.alpha2TranState = 0;
    }

    public boolean isSlowTran() {
        Log.e("isSlowTran", "alpha2TranState=" + this.alpha2TranState);
        if (this.alpha2TranState > 1) {
            return true;
        }
        return false;
    }

    public void setAlpha2TranSlowState() {
        if (this.alpha2TranState < 5) {
            this.alpha2TranState++;
        }
        Log.e("alpha2TranState", "alpha2TranState=" + this.alpha2TranState);
    }

    public void addNotificationApp(String apkName, int type) {
        this.notificationApps.put(apkName, Integer.toString(type));
    }

    public void removeNotificationApp(String apkName) {
        this.notificationApps.remove(apkName);
    }

    public void clearNotificationApp() {
        this.notificationApps.clear();
    }

    public void addNotificationApp(String apkName, String appName) {
        this.notificationApps.put(apkName, appName);
    }

    public int isNeedNotification(String apkName) {
        int i = -1;
        if (this.notificationApps.containsKey(apkName)) {
            try {
                i = Integer.parseInt((String) this.notificationApps.get(apkName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    public String getNeedNotificationName(String apkName) {
        if (this.notificationApps.containsKey(apkName)) {
            return (String) this.notificationApps.get(apkName);
        }
        return null;
    }
}
