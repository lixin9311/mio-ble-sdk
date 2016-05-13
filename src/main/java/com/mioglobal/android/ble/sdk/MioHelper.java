package com.mioglobal.android.ble.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalData;
import com.mioglobal.android.ble.sdk.MioUserSetting.RTCSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.TimeData;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserInfo;
import net.lingala.zip4j.crypto.PBKDF2.PBKDF2Engine;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.UUID;
import org.apache.commons.lang3.builder.DiffResult;

public class MioHelper {
    public static final UUID ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID;
    public static final UUID ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID;
    public static final UUID ALPHA2_DFU_SENDPACKET_CHARACTERISTIC_UUID;
    public static final UUID CHARACTERISTIC_CHAR_CSC_MEASUREMENT;
    public static final int CategoryIDBusinessAndFinance = 9;
    public static final int CategoryIDEmail = 6;
    public static final int CategoryIDEntertainment = 11;
    public static final int CategoryIDHealthAndFitness = 8;
    public static final int CategoryIDIncomingCall = 1;
    public static final int CategoryIDLocation = 10;
    public static final int CategoryIDMissedCall = 2;
    public static final int CategoryIDNews = 7;
    public static final int CategoryIDOther = 0;
    public static final int CategoryIDSchedule = 5;
    public static final int CategoryIDSocial = 4;
    public static final int CategoryIDVoicemail = 3;
    public static final int CommandIDGetAppAttributes = 1;
    public static final int CommandIDGetNotificationAttributes = 0;
    public static final int CommandIDPerformNotificationAction = 2;
    public static final UUID DFU_CONTROLPOINT_CHARACTERISTIC_UUID;
    public static final UUID DFU_PACKET_CHARACTERISTIC_UUID;
    public static final UUID DFU_STATUS_REPORT_CHARACTERISTIC_UUID;
    public static final int EIGTH_BITMASK = 128;
    public static final byte EventFlagImportant = (byte) 2;
    public static final byte EventFlagNegativeAction = (byte) 16;
    public static final byte EventFlagPositiveAction = (byte) 8;
    public static final byte EventFlagPreExisting = (byte) 4;
    public static final byte EventFlagSilent = (byte) 1;
    public static final int EventIDNotificationAdded = 0;
    public static final int EventIDNotificationModified = 1;
    public static final int EventIDNotificationRemoved = 2;
    public static final int FIFTH_BITMASK = 16;
    public static final int FIRST_BITMASK = 1;
    public static final int FORMAT_FLOAT = 52;
    public static final int FORMAT_SFLOAT = 50;
    public static final int FORMAT_SINT16 = 34;
    public static final int FORMAT_SINT32 = 36;
    public static final int FORMAT_SINT8 = 33;
    public static final int FORMAT_UINT16 = 18;
    public static final int FORMAT_UINT32 = 20;
    public static final int FORMAT_UINT8 = 17;
    public static final int FOURTH_BITMASK = 8;
    public static byte FUSE_FACTORY_DEFAULT = (byte) 0;
    public static byte GPS_SD_DATA = (byte) 0;
    public static byte LINK2_MEM_NEXT_PACKET_GET = (byte) 0;
    public static byte LINK2_MEM_RECORD_DELETE = (byte) 0;
    public static byte LINK2_MEM_RECORD_GET = (byte) 0;
    public static byte LINK_CUST_CMD = (byte) 0;
    public static byte LINK_CUST_DEVICE_OPTION_GET = (byte) 0;
    public static byte LINK_CUST_DEVICE_OPTION_SET = (byte) 0;
    public static byte LINK_DAILY_GOAL_GET = (byte) 0;
    public static byte LINK_DAILY_GOAL_SET = (byte) 0;
    public static byte LINK_DEVICE_STATUS_GET = (byte) 0;
    public static byte LINK_DISP_GET = (byte) 0;
    public static byte LINK_DISP_SET = (byte) 0;
    public static byte LINK_DISP_SET2 = (byte) 0;
    public static byte LINK_EXER_SETTINGS_GET = (byte) 0;
    public static byte LINK_EXER_SETTINGS_SET = (byte) 0;
    public static byte LINK_MEM_RECORD_DEL = (byte) 0;
    public static byte LINK_MEM_RECORD_GET = (byte) 0;
    public static byte LINK_MEM_SESSION_GET = (byte) 0;
    public static byte LINK_MISC1_GET = (byte) 0;
    public static byte LINK_MISC1_SET = (byte) 0;
    public static byte LINK_RESET_CMD = (byte) 0;
    public static byte LINK_STRIDE_CALI_GET = (byte) 0;
    public static byte LINK_STRIDE_CALI_SET = (byte) 0;
    public static byte LINK_USER_SCREEN_GET = (byte) 0;
    public static byte LINK_USER_SCREEN_SET = (byte) 0;
    public static byte MOBILE_EMAIL_ALERT = (byte) 0;
    public static byte MOBILE_MSG_ALERT = (byte) 0;
    public static byte MOBILE_NOTIFICATION = (byte) 0;
    public static byte MOBILE_PHONE_ALERT = (byte) 0;
    public static byte MSG_CHANNCEL_STATUS = (byte) 0;
    public static byte[] MSG_CMD_ENABLE_DFU_MODE = null;
    public static byte[] MSG_CMD_GET_LINK_ZONE_SETTINGS = null;
    public static byte MSG_DEVICE_NAME_GET = (byte) 0;
    public static byte MSG_DEVICE_NAME_SET = (byte) 0;
    public static byte MSG_FOUND_SONSER = (byte) 0;
    public static byte MSG_ID_LINK1_ZONE_SETTINGS_GET = (byte) 0;
    public static byte MSG_ID_LINK1_ZONE_SETTINGS_SET = (byte) 0;
    public static byte MSG_ID_LINK_DFU_CMD = (byte) 0;
    public static byte MSG_ID_RESPONSE = (byte) 0;
    public static byte MSG_INFO_ASYNC = (byte) 0;
    public static byte MSG_RESPONSE_CODE_INVALID_MSG = (byte) 0;
    public static byte MSG_RESPONSE_CODE_NO_ERROR = (byte) 0;
    public static byte MSG_RESPONSE_CODE_UNIT_IN_WRONG_STATE = (byte) 0;
    public static byte MSG_RTCTIME_GET = (byte) 0;
    public static byte MSG_RTCTIME_SET = (byte) 0;
    public static byte MSG_RTC_GET = (byte) 0;
    public static byte MSG_RTC_SET = (byte) 0;
    public static byte MSG_RUN_CMD = (byte) 0;
    public static byte MSG_SENSOR_DATA = (byte) 0;
    public static byte MSG_SENSOR_GET = (byte) 0;
    public static byte MSG_SENSOR_SET = (byte) 0;
    public static byte MSG_USER_SETTING_GET = (byte) 0;
    public static byte MSG_USER_SETTING_SET = (byte) 0;
    public static byte MSG_VELO_DEVICE_STATUS_GET = (byte) 0;
    public static byte MSG_VELO_MEM_RECORD_DEL = (byte) 0;
    public static byte MSG_VELO_MEM_SESSION_GET = (byte) 0;
    public static byte MSG_VELO_RECORD_GET = (byte) 0;
    public static byte MSG_VELO_STATUS_GET = (byte) 0;
    public static byte MSG_VELO_STATUS_SET = (byte) 0;
    public static byte RUN_CMD_ACT_MEM_ALLCLEAR = (byte) 0;
    public static byte RUN_CMD_ADL_MEM_ALLCLEAR = (byte) 0;
    public static byte RUN_CMD_AIRMODE_ENABLE = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_CMD_RESEND_LAST_LAP_DATA = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_CMD_START_TIMER = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_CMD_STOP_TIMER = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_CMD_TAKE_LAP = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_CMD_TIMER_SYNC_FINISH = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_DISABLE = (byte) 0;
    public static byte RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_ENABLE = (byte) 0;
    public static byte RUN_CMD_GMD = (byte) 0;
    public static byte RUN_CMD_GME = (byte) 0;
    public static byte RUN_CMD_MEM_CLEAR = (byte) 0;
    public static byte RUN_CMD_RAD = (byte) 0;
    public static byte RUN_CMD_RESTHRSENDMEASUREMENTRESULTS = (byte) 0;
    public static byte RUN_CMD_RESTHRSTOPMEASUREMENT = (byte) 0;
    public static byte RUN_CMD_RESTHRTAKEMEASUREMENT = (byte) 0;
    public static byte RUN_CMD_SLEEPMODELACTIVATE = (byte) 0;
    public static byte RUN_CMD_SLEEPMODELDEACTIVATE = (byte) 0;
    public static byte RUN_CMD_SMD = (byte) 0;
    public static byte RUN_CMD_SME = (byte) 0;
    public static byte RUN_CMD_STEP_DATA_NOTIFY_DIS = (byte) 0;
    public static byte RUN_CMD_STEP_DATA_NOTIFY_ENABLE = (byte) 0;
    public static byte RUN_CMD_USER_DATA_BACKUP = (byte) 0;
    public static final int SECOND_BITMASK = 2;
    public static final UUID SERVICES_SPEED_AND_CADENCE_UUID;
    public static final int SEVENTH_BITMASK = 64;
    public static final int SIXTH_BITMASK = 32;
    public static byte STEP_DATA = (byte) 0;
    public static final int THIRD_BITMASK = 4;
    public static byte TIMER_SYNC_DATA = (byte) 0;
    public static final UUID UUID_CHARACTERISTIC_BATTERY_LEVEL;
    public static final UUID UUID_CHARACTERISTIC_BATTERY_LEVEL_STATE;
    public static final UUID UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR;
    public static final UUID UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_HEART_RATE;
    public static final UUID UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING;
    public static final UUID UUID_CHARACTERISTIC_MIO_RECORD;
    public static final UUID UUID_CHARACTERISTIC_MIO_SENSER;
    public static final UUID UUID_CHARACTERISTIC_MIO_SPORT_MSG;
    public static final UUID UUID_CHARACTERISTIC_MIO_SPORT_MSG_RESP;
    public static final UUID UUID_CHARACTERISTIC_MODEL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING;
    public static final UUID UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING;
    public static final UUID UUID_CHARACTERISTIC_SYSTEM_ID_STRING;
    public static final String UUID_MIO_FUSE_DFU_SERVICE = "381C08EC-57EE-0611-0601-02362E315055";
    public static final String UUID_MIO_LINK_DFU_SERVICE = "381C08EC-57EE-0611-0601-02312E315055";
    public static final String UUID_MIO_VELO_DFU_SERVICE = "381C08EC-57EE-0611-0601-02332E315055";
    public static final UUID UUID_SERVICE_ALPHA2_DFU;
    public static final UUID UUID_SERVICE_BATTERY;
    public static final UUID UUID_SERVICE_DEVICE_INFORMATION;
    public static final UUID UUID_SERVICE_DFU;
    public static final UUID UUID_SERVICE_HEART_RATE;
    public static final UUID UUID_SERVICE_MIO_SPORTS;
    public static String companyID = null;
    protected static final char[] hexArray;
    public static final long leastSigBits = -9223371485494954757L;

    static {
        UUID_SERVICE_DEVICE_INFORMATION = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
        UUID_SERVICE_HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        UUID_SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
        UUID_SERVICE_MIO_SPORTS = UUID.fromString("6C721838-5BF1-4F64-9170-381C08EC57EE");
        UUID_SERVICE_DFU = UUID.fromString("6C721530-5BF1-4F64-9170-381C08EC57EE");
        UUID_SERVICE_ALPHA2_DFU = UUID.fromString("6C721550-5BF1-4F64-9170-381C08EC57EE");
        UUID_CHARACTERISTIC_CLIENT_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_BATTERY_LEVEL_STATE = UUID.fromString("0000C-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_BATTERY_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_SYSTEM_ID_STRING = UUID.fromString("00002A23-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_MODEL_NUMBER_STRING = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_SERIAL_NUMBER_STRING = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_FIRMWARE_REVISION_STRING = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_HARDWARE_REVISION_STRING = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_SOFTWARE_REVISION_STRING = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_MANUFACTURER_NAME_STRING = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_HEART_RATE = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
        UUID_CHARACTERISTIC_MIO_SPORT_MSG = UUID.fromString("6C722A80-5BF1-4F64-9170-381C08EC57EE");
        UUID_CHARACTERISTIC_MIO_SPORT_MSG_RESP = UUID.fromString("6C722A82-5BF1-4F64-9170-381C08EC57EE");
        UUID_CHARACTERISTIC_MIO_SENSER = UUID.fromString("6C722A83-5BF1-4F64-9170-381C08EC57EE");
        UUID_CHARACTERISTIC_MIO_RECORD = UUID.fromString("6C722A84-5BF1-4F64-9170-381C08EC57EE");
        SERVICES_SPEED_AND_CADENCE_UUID = UUID.fromString("00001816-0000-1000-8000-00805f9b34fb");
        CHARACTERISTIC_CHAR_CSC_MEASUREMENT = UUID.fromString("00002A5B-0000-1000-8000-00805f9b34fb");
        ALPHA2_DFU_CONTROLPOINT_CHARACTERISTIC_UUID = UUID.fromString("6C721551-5BF1-4F64-9170-381C08EC57EE");
        ALPHA2_DFU_SENDPACKET_CHARACTERISTIC_UUID = UUID.fromString("6C721552-5BF1-4F64-9170-381C08EC57EE");
        ALPHA2_DFU_PACKET_CHARACTERISTIC_UUID = UUID.fromString("6C721553-5BF1-4F64-9170-381C08EC57EE");
        DFU_CONTROLPOINT_CHARACTERISTIC_UUID = UUID.fromString("6C721531-5BF1-4F64-9170-381C08EC57EE");
        DFU_PACKET_CHARACTERISTIC_UUID = UUID.fromString("6C721532-5BF1-4F64-9170-381C08EC57EE");
        DFU_STATUS_REPORT_CHARACTERISTIC_UUID = UUID.fromString("00001533-1212-efde-1523-785feabcd123");
        MSG_ID_LINK1_ZONE_SETTINGS_GET = EventFlagSilent;
        MSG_ID_LINK1_ZONE_SETTINGS_SET = EventFlagImportant;
        MSG_ID_RESPONSE = Byte.MIN_VALUE;
        MSG_ID_LINK_DFU_CMD = (byte) -2;
        MSG_RESPONSE_CODE_NO_ERROR = (byte) 0;
        MSG_RESPONSE_CODE_UNIT_IN_WRONG_STATE = (byte) 32;
        MSG_RESPONSE_CODE_INVALID_MSG = (byte) 48;
        MSG_FOUND_SONSER = (byte) -127;
        MSG_CHANNCEL_STATUS = (byte) -126;
        MSG_INFO_ASYNC = (byte) -125;
        MSG_SENSOR_GET = (byte) 49;
        MSG_SENSOR_SET = (byte) 50;
        MSG_VELO_STATUS_GET = (byte) 51;
        MSG_VELO_STATUS_SET = (byte) 52;
        MSG_VELO_DEVICE_STATUS_GET = (byte) 53;
        MSG_VELO_RECORD_GET = (byte) 54;
        MSG_VELO_MEM_SESSION_GET = (byte) 55;
        MSG_VELO_MEM_RECORD_DEL = (byte) 56;
        MSG_USER_SETTING_GET = (byte) 3;
        MSG_USER_SETTING_SET = EventFlagPreExisting;
        MSG_DEVICE_NAME_GET = (byte) 5;
        MSG_DEVICE_NAME_SET = (byte) 6;
        MSG_RTC_GET = EventFlagNegativeAction;
        MSG_RTC_SET = (byte) 17;
        MSG_RTCTIME_GET = (byte) -48;
        MSG_RTCTIME_SET = (byte) -47;
        MSG_RUN_CMD = (byte) 18;
        LINK_STRIDE_CALI_GET = (byte) 19;
        LINK_STRIDE_CALI_SET = (byte) 20;
        LINK_EXER_SETTINGS_GET = (byte) 64;
        LINK_EXER_SETTINGS_SET = (byte) 65;
        LINK_USER_SCREEN_GET = (byte) 66;
        LINK_USER_SCREEN_SET = (byte) 67;
        LINK2_MEM_RECORD_GET = (byte) 80;
        LINK2_MEM_NEXT_PACKET_GET = (byte) 81;
        LINK2_MEM_RECORD_DELETE = (byte) 82;
        LINK_CUST_DEVICE_OPTION_GET = (byte) 70;
        LINK_CUST_DEVICE_OPTION_SET = (byte) 71;
        MSG_SENSOR_DATA = (byte) -3;
        GPS_SD_DATA = EventFlagNegativeAction;
        TIMER_SYNC_DATA = (byte) -15;
        STEP_DATA = (byte) -16;
        MOBILE_NOTIFICATION = (byte) -4;
        MOBILE_MSG_ALERT = (byte) -5;
        MOBILE_EMAIL_ALERT = (byte) -6;
        MOBILE_PHONE_ALERT = (byte) -7;
        RUN_CMD_SLEEPMODELDEACTIVATE = (byte) 64;
        RUN_CMD_SLEEPMODELACTIVATE = (byte) 65;
        RUN_CMD_RESTHRTAKEMEASUREMENT = (byte) 66;
        RUN_CMD_RESTHRSTOPMEASUREMENT = (byte) 67;
        RUN_CMD_RESTHRSENDMEASUREMENTRESULTS = (byte) 68;
        RUN_CMD_SMD = EventFlagNegativeAction;
        RUN_CMD_SME = (byte) 17;
        RUN_CMD_GMD = (byte) 18;
        RUN_CMD_GME = (byte) 19;
        RUN_CMD_STEP_DATA_NOTIFY_DIS = (byte) 20;
        RUN_CMD_STEP_DATA_NOTIFY_ENABLE = (byte) 21;
        RUN_CMD_MEM_CLEAR = (byte) 23;
        RUN_CMD_ADL_MEM_ALLCLEAR = (byte) 24;
        RUN_CMD_ACT_MEM_ALLCLEAR = (byte) 25;
        RUN_CMD_RAD = (byte) 32;
        RUN_CMD_AIRMODE_ENABLE = (byte) 33;
        RUN_CMD_USER_DATA_BACKUP = (byte) 34;
        RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_DISABLE = (byte) 48;
        RUN_CMD_EXE_TIMER_SYNC_DATA_NOTIFICATION_ENABLE = (byte) 49;
        RUN_CMD_EXE_TIMER_SYNC_CMD_START_TIMER = (byte) 50;
        RUN_CMD_EXE_TIMER_SYNC_CMD_STOP_TIMER = (byte) 51;
        RUN_CMD_EXE_TIMER_SYNC_CMD_TAKE_LAP = (byte) 52;
        RUN_CMD_EXE_TIMER_SYNC_CMD_RESEND_LAST_LAP_DATA = (byte) 53;
        RUN_CMD_EXE_TIMER_SYNC_CMD_TIMER_SYNC_FINISH = (byte) 54;
        LINK_DISP_GET = (byte) 32;
        LINK_DISP_SET = (byte) 33;
        LINK_DAILY_GOAL_GET = (byte) 34;
        LINK_DAILY_GOAL_SET = (byte) 35;
        LINK_DEVICE_STATUS_GET = (byte) 36;
        LINK_MEM_RECORD_GET = (byte) 37;
        LINK_MEM_SESSION_GET = (byte) 38;
        LINK_MEM_RECORD_DEL = (byte) 39;
        LINK_DISP_SET2 = (byte) 40;
        LINK_MISC1_GET = (byte) 41;
        LINK_MISC1_SET = (byte) 42;
        LINK_RESET_CMD = (byte) -27;
        LINK_CUST_CMD = (byte) 69;
        byte[] bArr = new byte[SECOND_BITMASK];
        bArr[FIRST_BITMASK] = EventFlagSilent;
        MSG_CMD_GET_LINK_ZONE_SETTINGS = bArr;
        MSG_CMD_ENABLE_DFU_MODE = new byte[]{(byte) 5, (byte) -2, (byte) -96, (byte) -7, (byte) -82, (byte) 52, (byte) -119};
        FUSE_FACTORY_DEFAULT = (byte) -19;
        hexArray = "0123456789ABCDEF".toCharArray();
        companyID = DiffResult.OBJECTS_SAME_STRING;
    }

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        if (context == null) {
            return null;
        }
        return ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    public static UUID toUuid(String uuidString) {
        return UUID.fromString(uuidString);
    }

    public static UUID toUuid(long assignedNumber) {
        return new UUID((assignedNumber << SIXTH_BITMASK) | PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, leastSigBits);
    }

    public static String toUuid128(long assignedNumber) {
        return toUuid(assignedNumber).toString();
    }

    public static String toUuid16(int assignedNumber) {
        return Integer.toHexString(assignedNumber);
    }

    public static Integer getIntValue(byte[] value, int format, int position) {
        if (value == null || (format & 15) + position > value.length) {
            return null;
        }
        switch (format) {
            case FORMAT_UINT8 /*17*/:
                return Integer.valueOf(value[position] & 255);
            case FORMAT_UINT16 /*18*/:
                return Integer.valueOf(add(value[position], value[position + FIRST_BITMASK]));
            case FORMAT_UINT32 /*20*/:
                return Integer.valueOf(add(value[position], value[position + FIRST_BITMASK], value[position + SECOND_BITMASK], value[position + CategoryIDVoicemail]));
            case FORMAT_SINT8 /*33*/:
                return Integer.valueOf(signed(value[position] & 255, FOURTH_BITMASK));
            case FORMAT_SINT16 /*34*/:
                return Integer.valueOf(signed(add(value[position], value[position + FIRST_BITMASK]), FIFTH_BITMASK));
            case FORMAT_SINT32 /*36*/:
                return Integer.valueOf(signed(add(value[position], value[position + FIRST_BITMASK], value[position + SECOND_BITMASK], value[position + CategoryIDVoicemail]), SIXTH_BITMASK));
            default:
                return null;
        }
    }

    public static Float getFloatValue(byte[] value, int format, int position) {
        if (value == null || (format & 15) + position > value.length) {
            return null;
        }
        switch (format) {
            case FORMAT_SFLOAT /*50*/:
                int i = value[position + FIRST_BITMASK];
                return Float.valueOf((float) (((double) signed((value[position] & 255) + (((i & 255) & 15) << FOURTH_BITMASK), 12)) * Math.pow(10.0d, (double) signed((i & 255) >> THIRD_BITMASK, THIRD_BITMASK))));
            case FORMAT_FLOAT /*52*/:
                int exponent = value[position + CategoryIDVoicemail];
                int mantissa = value[position + SECOND_BITMASK];
                return Float.valueOf((float) (((double) signed(((value[position] & 255) + ((value[position + FIRST_BITMASK] & 255) << FOURTH_BITMASK)) + ((mantissa & 255) << FIFTH_BITMASK), 24)) * Math.pow(10.0d, (double) exponent)));
            default:
                return null;
        }
    }

    public static String getStringValue(byte[] value, int position) {
        if (value == null || position > value.length) {
            return null;
        }
        byte[] arrayOfByte = new byte[(value.length - position)];
        for (int i = EventIDNotificationAdded; i != value.length - position; i += FIRST_BITMASK) {
            arrayOfByte[i] = value[position + i];
        }
        return new String(arrayOfByte);
    }

    private static int add(byte byte1, byte byte2) {
        return (byte1 & 255) + ((byte2 & 255) << FOURTH_BITMASK);
    }

    private static int add(byte byte1, byte byte2, byte byte3, byte byte4) {
        return (((byte1 & 255) + ((byte2 & 255) << FOURTH_BITMASK)) + ((byte3 & 255) << FIFTH_BITMASK)) + ((byte4 & 255) << 24);
    }

    private static int signed(int value, int length) {
        if (((FIRST_BITMASK << (length - 1)) & value) != 0) {
            return ((FIRST_BITMASK << (length - 1)) - (((FIRST_BITMASK << (length - 1)) - 1) & value)) * -1;
        }
        return value;
    }

    public static byte[] hexByteArrayToByteArray(byte[] hexByteArray) {
        return hexStringToByteArray(new String(hexByteArray));
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[(len / SECOND_BITMASK)];
        for (int i = EventIDNotificationAdded; i < len; i += SECOND_BITMASK) {
            data[i / SECOND_BITMASK] = (byte) ((Character.digit(hexString.charAt(i), FIFTH_BITMASK) << THIRD_BITMASK) + Character.digit(hexString.charAt(i + FIRST_BITMASK), FIFTH_BITMASK));
        }
        return data;
    }

    public static String toHexCode(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length * SECOND_BITMASK);
        int length = data.length;
        for (int i = EventIDNotificationAdded; i < length; i += FIRST_BITMASK) {
            Object[] objArr = new Object[FIRST_BITMASK];
            objArr[EventIDNotificationAdded] = Byte.valueOf(data[i]);
            stringBuilder.append(String.format("%02X", objArr));
        }
        return stringBuilder.toString();
    }

    public static boolean isHeartRateInUINT16(byte value) {
        if ((value & FIRST_BITMASK) != 0) {
            return true;
        }
        return false;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[(bytes.length * SECOND_BITMASK)];
        for (int j = EventIDNotificationAdded; j < bytes.length; j += FIRST_BITMASK) {
            int v = bytes[j] & 255;
            hexChars[j * SECOND_BITMASK] = hexArray[v >>> THIRD_BITMASK];
            hexChars[(j * SECOND_BITMASK) + FIRST_BITMASK] = hexArray[v & 15];
        }
        return new String(hexChars);
    }

    public static boolean IsHRSupported(byte[] scanRecond) {
        return true;
    }

    public static String paserVeloSerial(byte[] scanRecond) {
        int i;
        String uid = DiffResult.OBJECTS_SAME_STRING;
        int index = EventIDNotificationAdded;
        companyID = DiffResult.OBJECTS_SAME_STRING;
        for (i = CategoryIDEmail; i < scanRecond.length; i += FIRST_BITMASK) {
            if (scanRecond[i] == 57) {
                index = i;
                break;
            }
        }
        if (index > CategoryIDEmail) {
            byte[] veloSerial = new byte[CategoryIDSchedule];
            veloSerial[EventIDNotificationAdded] = scanRecond[index - 4];
            veloSerial[FIRST_BITMASK] = scanRecond[index - 3];
            veloSerial[SECOND_BITMASK] = scanRecond[index - 2];
            veloSerial[CategoryIDVoicemail] = scanRecond[index - 1];
            veloSerial[THIRD_BITMASK] = scanRecond[index];
            for (i = EventIDNotificationAdded; i < veloSerial.length; i += FIRST_BITMASK) {
                int code = veloSerial[i] & 255;
                if (code < CategoryIDLocation) {
                    uid = new StringBuilder(String.valueOf(uid)).append("0").toString();
                }
                uid = new StringBuilder(String.valueOf(uid)).append(Integer.toHexString(code)).toString();
            }
            System.out.println(" deviceUID ====> " + uid);
            int num = scanRecond[index - 5] & 255;
            if (num < CategoryIDLocation) {
                companyID += "0";
            }
            companyID += Integer.toHexString(num);
            num = scanRecond[index - 6] & 255;
            if (num < CategoryIDLocation) {
                companyID += "0";
            }
            companyID += Integer.toHexString(num);
            System.out.println("companyID=" + companyID);
        }
        return uid;
    }

    public static boolean checkUserInfoValid(UserInfo userInfo) {
        if (userInfo == null || userInfo.birthDay > 31 || userInfo.birthMonth < EventFlagSilent || userInfo.birthMonth < EventFlagSilent || userInfo.birthMonth > 12) {
            return false;
        }
        if (userInfo.birthYear > (short) 1900) {
            userInfo.birthYear = (short) (userInfo.birthYear - 1900);
        }
        if (userInfo.birthYear < (short) 0 || userInfo.birthYear > (short) 199 || userInfo.bodyWeight < (short) 20 || userInfo.bodyWeight > (short) 200 || userInfo.bodyHeight < (short) 69 || userInfo.bodyHeight > (short) 231 || userInfo.resetHR < (short) 30 || userInfo.resetHR > (short) 140 || userInfo.maxHR < (short) 80 || userInfo.maxHR > (short) 220 || userInfo.resetHR >= userInfo.maxHR) {
            return false;
        }
        return true;
    }

    public static boolean checkRTCTimeValid(RTCSetting rtcSetting) {
        if (rtcSetting == null || rtcSetting.timeData == null) {
            return false;
        }
        if (rtcSetting.timeData.second > (byte) 59 || rtcSetting.timeData.second < 0) {
            return false;
        }
        if (rtcSetting.timeData.minute > (byte) 59 || rtcSetting.timeData.minute < 0) {
            return false;
        }
        if (rtcSetting.timeData.hour > 23 || rtcSetting.timeData.hour < 0) {
            return false;
        }
        if (rtcSetting.timeData.day > 31 || rtcSetting.timeData.day < EventFlagSilent) {
            return false;
        }
        if (rtcSetting.timeData.month > 12 || rtcSetting.timeData.month < EventFlagSilent) {
            return false;
        }
        if (rtcSetting.timeData.year > (short) 1900) {
            TimeData timeData = rtcSetting.timeData;
            timeData.year = (short) (timeData.year - 1900);
        }
        if (rtcSetting.timeData.year > (short) 199 || rtcSetting.timeData.year < (short) 114) {
            return false;
        }
        return true;
    }

    public static boolean checkGoalDataValid(GoalData goalData) {
        if (goalData == null || goalData.goalData == null || goalData.flags < EventFlagSilent || goalData.flags > EventFlagImportant || goalData.goalData.goalType < 0 || goalData.goalData.goalType > (byte) 3) {
            return false;
        }
        if (goalData.goalData.goalType == EventFlagSilent) {
            if (goalData.goalData.goalValue < 100 || goalData.goalData.goalValue > 99999) {
                return false;
            }
        } else if (goalData.goalData.goalType == EventFlagImportant) {
            if (goalData.goalData.goalValue < 100) {
                return false;
            }
            if (goalData.goalData.goalValue > 99999) {
                return false;
            }
        } else if (goalData.goalData.goalType == (byte) 3) {
            if (goalData.goalData.goalValue < 100) {
                return false;
            }
            if (goalData.goalData.goalValue > 9999) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkDisplayValid(FuseDisplay fuseDisplay, String deviceName) {
        if (fuseDisplay == null || fuseDisplay.adlDisplay == null || fuseDisplay.woDisplay == null) {
            return false;
        }
        if (!deviceName.endsWith("FUSE") || fuseDisplay.adlKeyLockTime == 0 || (fuseDisplay.adlKeyLockTime >= CategoryIDLocation && fuseDisplay.adlKeyLockTime <= 120)) {
            return true;
        }
        return false;
    }

    public static boolean IsMioBuletoothDevice(String deviceName) {
        if (deviceName.contains("LINK") || deviceName.contains("ALPHA") || deviceName.toUpperCase().endsWith("OTBEAT MIO LINK") || deviceName.contains("VELO") || deviceName.contains("FUSE")) {
            return true;
        }
        return false;
    }
}
