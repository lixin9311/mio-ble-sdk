package com.mioglobal.android.ble.sdk.DFU;

//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class MIOCommon {
    public static byte[] MSG_CMD_ENABLE_DFU_MODE_ALPHA2;
    public static byte[] MSG_CMD_ENABLE_DFU_MODE_FUSE;
    public static byte[] MSG_CMD_ENABLE_DFU_MODE_LINK_OR_VELO;
    public static byte[] MSG_CMD_GET_LINK_ZONE_SETTINGS;
    public static byte MSG_ID_ALPHA2_DFU_CMD;
    public static byte MSG_ID_LINK1_ZONE_SETTINGS_GET;
    public static byte MSG_ID_LINK1_ZONE_SETTINGS_SET;
    public static byte MSG_ID_LINK_DFU_CMD;
    public static byte MSG_ID_RESPONSE;
    public static byte MSG_RESPONSE_CODE_INVALID_MSG;
    public static byte MSG_RESPONSE_CODE_NO_ERROR;
    public static byte MSG_RESPONSE_CODE_UNIT_IN_WRONG_STATE;
    protected static final char[] hexArray;

    static {
        MSG_ID_LINK1_ZONE_SETTINGS_GET = (byte) 1;
        MSG_ID_LINK1_ZONE_SETTINGS_SET = (byte) 2;
        MSG_ID_RESPONSE = Byte.MIN_VALUE;
        MSG_ID_LINK_DFU_CMD = (byte) -2;
        MSG_ID_ALPHA2_DFU_CMD = (byte) 68;
        MSG_RESPONSE_CODE_NO_ERROR = (byte) 0;
        MSG_RESPONSE_CODE_UNIT_IN_WRONG_STATE = (byte) 32;
        MSG_RESPONSE_CODE_INVALID_MSG = (byte) 48;
        byte[] bArr = new byte[2];
        bArr[1] = (byte) 1;
        MSG_CMD_GET_LINK_ZONE_SETTINGS = bArr;
        MSG_CMD_ENABLE_DFU_MODE_LINK_OR_VELO = new byte[]{(byte) 5, (byte) -2, (byte) -96, (byte) -7, (byte) -82, (byte) 52, (byte) -119};
        MSG_CMD_ENABLE_DFU_MODE_FUSE = new byte[]{(byte) 5, (byte) -2, (byte) -96, (byte) -115, (byte) -82, (byte) 52, (byte) -119};
        bArr = new byte[2];
        bArr[1] = (byte) 68;
        MSG_CMD_ENABLE_DFU_MODE_ALPHA2 = bArr;
        hexArray = "0123456789ABCDEF".toCharArray();
    }

    public static byte[] hexByteArrayToByteArray(byte[] hexByteArray) {
        return hexStringToByteArray(new String(hexByteArray));
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[(j * 2) + 1] = hexArray[v & 15];
        }
        return new String(hexChars);
    }
}
