package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.MioUserSetting.DelOPType;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.GPSData;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalData;
import com.mioglobal.android.ble.sdk.MioUserSetting.MIOMisc1Data;
import com.mioglobal.android.ble.sdk.MioUserSetting.RTCSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.RType;
import com.mioglobal.android.ble.sdk.MioUserSetting.StridCaliData;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserInfo;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreenData;

public interface MioDeviceInterface {

    public enum CMD_TYPE {
        CMD_TYPE_NONE,
        CMD_TYPE_HR_SET,
        CMD_TYPE_HR_GET,
        CMD_TYPE_BIKE_SET,
        CMD_TYPE_BIKE_GET,
        CMD_TYPE_APPTYPE_GET,
        CMD_TYPE_APPTYPE_SET,
        CMD_TYPE_USEINFO_GET,
        CMD_TYPE_USEINFO_SET,
        CMD_TYPE_NAME_GET,
        CMD_TYPE_NAME_SET,
        CMD_TYPE_RTC_GET,
        CMD_TYPE_RTC_SET,
        CMD_TYPE_RUN_CMD,
        CMD_TYPE_SEND_GPS_DATA,
        CMD_TYPE_DISPLAY_GET,
        CMD_TYPE_DISPLAY_SET,
        CMD_TYPE_DAILYGOAL_GET,
        CMD_TYPE_DAILYGOAL_SET,
        CMD_TYPE_DEVICE_STATUS_GET,
        CMD_TYPE_TODAY_ADL_RECORD_GET,
        CMD_TYPE_RECORD_GET,
        CMD_TYPE_RECORD_DELETE,
        CMD_TYPE_SESSION_GET,
        CMD_TYPE_LINK_CUST_CMD,
        CMD_TYPE_LINK_ENTER_DFUMODE,
        CMD_TYPE_ALPHA2_ENTER_DFUMODE,
        CMD_TYPE_LINK_UPDATE,
        CMD_TYPE_ALPHA2_UPDATE,
        CMD_TYPE_STRIDE_CALI_GET,
        CMD_TYPE_STRIDE_CALI_SET,
        CMD_TYPE_FACTORY_DEFAULT,
        CMD_TYPE_SWING_ARM_GET,
        CMD_TYPE_SWING_ARM_SET,
        CMD_TYPE_VELO_DEVICE_STATUS_GET,
        CMD_TYPE_VELO_MEM_RECORD_GET,
        CMD_TYPE_VELO_MEM_SESSION_GET,
        CMD_TYPE_VELO_MEM_RECORD_DEL,
        CMD_TYPE_LINK_MOBILE_NOTIFICATION,
        CMD_TYPE_LINK_MOBILE_MSG_ALERT,
        CMD_TYPE_LINK_MOBILE_EMAIL_ALERT,
        CMD_TYPE_LINK_MOBILE_PHONE_ALERT,
        CMD_TYPE_SLEEP_RECORD_GET,
        CMD_TYPE_SLEEP_RECORD_DELETE,
        CMD_TYPE_SLEEP_RECORD_CURHOUR,
        CMD_TYPE_DEVICE_OPTION_GET,
        CMD_TYPE_DEVICE_OPTION_SET
    }

    public enum RUN_CMD {
        CMD_StreamModeDisable,
        CMD_StreamModeEnable,
        CMD_GPSModeDisable,
        CMD_GPSModeEnable,
        CMD_ResetTodayADLData,
        CMD_StepDataNotifyDisable,
        CMD_StepDataNotifyEnable,
        CMD_AirplaneModeEnable,
        CMD_MemAllClear,
        CMD_UserDataBackupNow,
        CMD_ExeTimerSyncDataNotificationDisable,
        CMD_ExeTimerSyncDataNotificationEnable,
        CMD_ExeTimerSyncCmd_StartTimer,
        CMD_ExeTimerSyncCmd_StopTimer,
        CMD_ExeTimerSyncCmd_TakeLap,
        CMD_ExeTimerSyncCmd_ResendLastLapData,
        CMD_ExeTimerSyncCmd_Finish,
        CMD_SleepModeDeactivate,
        CMD_SleepModeActivate,
        CMD_RestHRTakeMeasurement,
        CMD_RestHRStopMeasurement,
        CMD_RestHRSendMeasurementResults,
        CMD_ACTMemAllClear,
        CMD_ADLMemAllClear
    }

    boolean AirplaneModeEnable_MIO();

    boolean DeleteAllActivityRecord_MIO(RType rType, DelOPType delOPType);

    boolean DeleteAlpha2Record_MIO(DelOPType delOPType);

    boolean DeleteRecord_MIO(RType rType, DelOPType delOPType);

    boolean DeleteVeloRecord_MIO(RType rType, DelOPType delOPType);

    boolean GetAllRecordOfActivity_MIO();

    boolean GetAlpha2Record_MIO(boolean z);

    boolean GetDailyGoal_MIO();

    boolean GetDeviceName_MIO();

    boolean GetDeviceOption_MIO();

    boolean GetDisplay_MIO();

    boolean GetExerciseSetting_MIO();

    boolean GetFirstRecordOfDailyADL_MIO();

    boolean GetMisc1_MIO();

    boolean GetNextRecordOfDailyADL_MIO();

    boolean GetRTCTime_MIO();

    boolean GetRecordOfDailyADL_MIO(boolean z);

    boolean GetStrideCali_MIO();

    boolean GetTodayADLRecord_MIO();

    boolean GetTotalNumbersOfWorkoutRecord_MIO();

    boolean GetUserInfo_MIO();

    boolean GetUserScreen_MIO();

    boolean GetVeloRecord_MIO(boolean z);

    boolean GetWorkoutRecord_MIO(short s);

    boolean GetWorkoutRecord_MIO(boolean z);

    boolean ResetDevice_MIO();

    boolean ResetTodayADLData_MIO();

    boolean SendCMD(CMD_TYPE cmd_type);

    boolean SendGpsData_MIO(GPSData gPSData);

    boolean SendRunCmd_MIO(RUN_CMD run_cmd);

    boolean SetDailyGoal_MIO(GoalData goalData);

    boolean SetDeviceName_MIO(String str);

    boolean SetDeviceOption_MIO(byte b);

    boolean SetDisplay_MIO(FuseDisplay fuseDisplay, String str);

    boolean SetExerciseSetting_MIO(ExerciseSetting exerciseSetting);

    boolean SetMisc1_MIO(MIOMisc1Data mIOMisc1Data);

    boolean SetRTCTime_MIO(RTCSetting rTCSetting);

    boolean SetStrideCali_MIO(StridCaliData stridCaliData);

    boolean SetUserInfo_MIO(UserInfo userInfo, String str);

    boolean SetUserScreen_MIO(UserScreenData userScreenData);

    boolean SleepModeActivate_MIO();

    boolean SleepModeDeactivate_MIO();
}
