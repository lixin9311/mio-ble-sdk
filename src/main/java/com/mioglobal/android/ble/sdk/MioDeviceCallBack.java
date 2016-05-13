package com.mioglobal.android.ble.sdk;

import com.mioglobal.android.ble.sdk.MioDeviceInterface.CMD_TYPE;
import com.mioglobal.android.ble.sdk.MioUserSetting.ADLDailyData;
import com.mioglobal.android.ble.sdk.MioUserSetting.ADLTodayData;
import com.mioglobal.android.ble.sdk.MioUserSetting.DelOPType;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseRecord;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseTimerSyncData;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDeviceStatus;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.MIOMisc1Data;
import com.mioglobal.android.ble.sdk.MioUserSetting.OneHourSleepTracking;
import com.mioglobal.android.ble.sdk.MioUserSetting.RTCSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.RType;
import com.mioglobal.android.ble.sdk.MioUserSetting.StridCaliData;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserInfo;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreenData;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloDeviceStatus;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloRecordData;
import com.mioglobal.android.ble.sdk.MioUserSetting.WorkoutRecord;
import java.util.List;

public interface MioDeviceCallBack {
    void DidDeleteAllActivityRecord_MIO(RType rType, DelOPType delOPType, byte b);

    void DidEndSYNC(byte b);

    void DidGetDeviceOption_MIO(byte b, byte b2);

    void DidGetMisc1_MIO(MIOMisc1Data mIOMisc1Data, byte b);

    void DidGetSleepTrackList_MIO(List<OneHourSleepTracking> list, byte b);

    void DidSendCMDTimeOut_MIO();

    void DidSetDeviceOption_MIO(byte b);

    void DidSetMisc1_MIO(byte b);

    void OnSyscDeviceSensorData_MIO(int i, ExerciseTimerSyncData exerciseTimerSyncData);

    void onAirplaneModeEnable(byte b);

    void onDeleteAlpha2Record(DelOPType delOPType, byte b);

    void onDeleteRecord(RType rType, DelOPType delOPType, byte b);

    void onDeleteVeloRecord(RType rType, DelOPType delOPType, byte b);

    void onGetAlpha2Record(ExerciseRecord exerciseRecord, short s, short s2, byte b);

    void onGetDailyGoal(GoalSetting goalSetting, byte b);

    void onGetDeviceName(String str, byte b);

    void onGetDeviceStatus(byte b, FuseDeviceStatus fuseDeviceStatus);

    void onGetDisplay(FuseDisplay fuseDisplay, byte b);

    void onGetExerciseSetting(ExerciseSetting exerciseSetting, byte b);

    void onGetRTCTime(RTCSetting rTCSetting, byte b);

    void onGetRecordOfDailyADL(ADLDailyData aDLDailyData, short s, short s2, byte b);

    void onGetStrideCali(byte b, StridCaliData stridCaliData);

    void onGetTodayADLRecord(ADLTodayData aDLTodayData, byte b);

    void onGetTotalNumbersOfWorkoutRecord(short s, byte b);

    void onGetUserInfo(UserInfo userInfo, byte b);

    void onGetUserScreen(UserScreenData userScreenData, byte b);

    void onGetVeloDeviceStatus(byte b, VeloDeviceStatus veloDeviceStatus);

    void onGetVeloRecord(VeloRecordData veloRecordData, short s, short s2, byte b);

    void onGetWorkoutRecord(WorkoutRecord workoutRecord, short s, short s2, byte b);

    void onResetTodayADLRecord(byte b);

    void onResumeDownLoadTask();

    void onSendCMD(CMD_TYPE cmd_type, byte b);

    void onSendGpsData(byte b);

    void onSendRunCmd_MIO(byte b);

    void onSetDailyGoal(byte b);

    void onSetDeviceName(byte b);

    void onSetDisplay(byte b);

    void onSetExerciseSetting(byte b);

    void onSetRTCTime(byte b);

    void onSetStrideCali(byte b);

    void onSetUserInfo(byte b);

    void onSetUserScreen(byte b);

    void onSyncRecordTimeOut(int i);

    void onSyscTimerSenserData_MIO(int i, ExerciseTimerSyncData exerciseTimerSyncData);
}
