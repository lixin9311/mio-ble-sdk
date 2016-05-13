package com.mioglobal.android.ble.sdk;

public interface DFUCallbacks {
    public static final int MIO_DFU_ERROR_CODE_BLUETOOTH_CLOSED = 6;
    public static final int MIO_DFU_ERROR_CODE_DEVICES_TYPE_IS_UNKNOW = 4;
    public static final int MIO_DFU_ERROR_CODE_DFUMANAGER_CAllBACK_IS_NONE = 3;
    public static final int MIO_DFU_ERROR_CODE_FILEPATH_IS_NONE = 2;
    public static final int MIO_DFU_ERROR_CODE_FILE_CLOSE = 17;
    public static final int MIO_DFU_ERROR_CODE_FILE_NOT_EXITS = 14;
    public static final int MIO_DFU_ERROR_CODE_FILE_OPEN = 15;
    public static final int MIO_DFU_ERROR_CODE_FILE_READ = 18;
    public static final int MIO_DFU_ERROR_CODE_INTERNAL_ERROR = 9;
    public static final int MIO_DFU_ERROR_CODE_INVALID_MAC_ADDRESS = 16;
    public static final int MIO_DFU_ERROR_CODE_LOW_POWER = 10;
    public static final int MIO_DFU_ERROR_CODE_MAC_ADDRESS_IS_NONE = 5;
    public static final int MIO_DFU_ERROR_CODE_NAME_AND_TYPE_NOT_MAP = 1;
    public static final int MIO_DFU_ERROR_CODE_SUCCESS = 0;
    public static final int MIO_DFU_ERROR_CODE_UPDATE_FILE_TRANSFER_FAILED = 11;
    public static final int MIO_DFU_ERROR_CODE_UPDATE_FILE_VALIDATION_FAILED = 12;
    public static final int MIO_DFU_ERROR_CODE_WRITE_DESCRIPTOR = 13;
    public static final String MIO_DFU_ERROR_MSG_BLUETOOTH_CLOSED = "Bluetooth Closed";
    public static final String MIO_DFU_ERROR_MSG_FILE_CLOSE = "Error on closing file";
    public static final String MIO_DFU_ERROR_MSG_FILE_NOT_EXITS = "File not exits";
    public static final String MIO_DFU_ERROR_MSG_FILE_OPEN = "Error on openning file";
    public static final String MIO_DFU_ERROR_MSG_FILE_READ = "Error on reading file";
    public static final String MIO_DFU_ERROR_MSG_INTERNAL_ERROR = "Internal error,please retry!";
    public static final String MIO_DFU_ERROR_MSG_INVALID_MAC_ADDRESS = "Invalid MAC address";
    public static final String MIO_DFU_ERROR_MSG_LOW_POWER = "Devices Low Power";
    public static final String MIO_DFU_ERROR_MSG_MAC_ADDRESS_IS_NONE = "Devices MAC Address is null";
    public static final String MIO_DFU_ERROR_MSG_NAME_AND_TYPE_NOT_MAP = "Devices Name and Type is Not Map!";
    public static final String MIO_DFU_ERROR_MSG_UPDATE_FILE_TRANSFER_FAILED = "Data transfer failed";
    public static final String MIO_DFU_ERROR_MSG_UPDATE_FILE_VALIDATION_FAILED = "File validation failed";
    public static final String MIO_DFU_ERROR_MSG_WRITE_DESCRIPTOR = "Error on writing descriptor";

    void onEnterDFUModeSuccess();

    void onError(String str, int i);

    void onFileTranfering(int i);

    void onFileTransferCompleted();

    void onFileTransferStarted();

    void onFileTransferValidation();

    void onUpdateSuccess();
}
