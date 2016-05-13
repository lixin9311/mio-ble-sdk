package com.mioglobal.android.ble.sdk;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.InputDeviceCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.couchbase.lite.replicator.PullerInternal;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.mioglobal.android.ble.sdk.MioUserSetting.ADLDailyData;
import com.mioglobal.android.ble.sdk.MioUserSetting.ADLDisPlay;
import com.mioglobal.android.ble.sdk.MioUserSetting.ADLTodayData;
import com.mioglobal.android.ble.sdk.MioUserSetting.DATEFORMAT;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseOrLapSummary;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseRecord;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseTimeData;
import com.mioglobal.android.ble.sdk.MioUserSetting.ExerciseTimerSyncData;
import com.mioglobal.android.ble.sdk.MioUserSetting.FuseDisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalData;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.GoalStruct;
import com.mioglobal.android.ble.sdk.MioUserSetting.MIOMisc1Data;
import com.mioglobal.android.ble.sdk.MioUserSetting.OneHourSleepTracking;
import com.mioglobal.android.ble.sdk.MioUserSetting.OneMinuteSleepTracking;
import com.mioglobal.android.ble.sdk.MioUserSetting.PaceTime;
import com.mioglobal.android.ble.sdk.MioUserSetting.RTCSetting;
import com.mioglobal.android.ble.sdk.MioUserSetting.RecordLogData;
import com.mioglobal.android.ble.sdk.MioUserSetting.StridCaliData;
import com.mioglobal.android.ble.sdk.MioUserSetting.TIMEFORMAT;
import com.mioglobal.android.ble.sdk.MioUserSetting.TimeData;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserInfo;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreen1Type;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreen2Type;
import com.mioglobal.android.ble.sdk.MioUserSetting.UserScreenData;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloRecordData;
import com.mioglobal.android.ble.sdk.MioUserSetting.VeloRowData;
import com.mioglobal.android.ble.sdk.MioUserSetting.WODisplay;
import com.mioglobal.android.ble.sdk.MioUserSetting.WorkoutRecord;
import com.mioglobal.android.ble.sdk.MioUserSetting.WorkoutRecordSummary;
import com.mobsandgeeks.saripaar.DateFormats;
//import com.oeday.libs.base.DBModel;
//import com.oeday.miogo.GPSTrackerActivity;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.time.DateUtils;

public class MioSportMsgParserUtil {
    private static final int RUN_MAX_RUNNING_PACE = 5999;
    private static final int RUN_MIN_RUNNING_PACE_KM = 5;
    public static final String SDCARD_PATH;
    private static final int exerciseOrLapSummarySize = 48;
    private static boolean isDebug = false;
    @SuppressLint({"SimpleDateFormat"})
    private static SimpleDateFormat logfile = null;
    private static SimpleDateFormat myLogSdf = null;
    private static final int workoutSummarySize = 34;

    /* renamed from: com.mioglobal.android.ble.sdk.MioSportMsgParserUtil.1 */
    static class C02851 implements Runnable {
        private final /* synthetic */ MioDeviceCallBack val$callBack;
        private final /* synthetic */ byte[] val$data;
        private final /* synthetic */ int val$numberOfRecord;
        private final /* synthetic */ int val$recordIndex;

        C02851(byte[] bArr, MioDeviceCallBack mioDeviceCallBack, int i, int i2) {
            this.val$data = bArr;
            this.val$callBack = mioDeviceCallBack;
            this.val$numberOfRecord = i;
            this.val$recordIndex = i2;
        }

        public void run() {
            WorkoutRecord workoutRecord = MioSportMsgParserUtil.parserWorkoutRecordData(this.val$data);
            if (this.val$callBack != null) {
                this.val$callBack.onGetWorkoutRecord(workoutRecord, (short) this.val$numberOfRecord, (short) this.val$recordIndex, (byte) 0);
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioSportMsgParserUtil.2 */
    static class C02862 implements Runnable {
        private final /* synthetic */ MioDeviceCallBack val$callBack;
        private final /* synthetic */ byte[] val$data;
        private final /* synthetic */ int val$numberOfRecord;
        private final /* synthetic */ int val$recordIndex;

        C02862(byte[] bArr, MioDeviceCallBack mioDeviceCallBack, int i, int i2) {
            this.val$data = bArr;
            this.val$callBack = mioDeviceCallBack;
            this.val$numberOfRecord = i;
            this.val$recordIndex = i2;
        }

        public void run() {
            VeloRecordData veloRecord = MioSportMsgParserUtil.parserVeloRecordData(this.val$data);
            if (this.val$callBack != null) {
                this.val$callBack.onGetVeloRecord(veloRecord, (short) this.val$numberOfRecord, (short) this.val$recordIndex, (byte) 0);
            }
        }
    }

    /* renamed from: com.mioglobal.android.ble.sdk.MioSportMsgParserUtil.3 */
    static class C02873 implements Runnable {
        private final /* synthetic */ MioDeviceCallBack val$callBack;
        private final /* synthetic */ byte[] val$data;
        private final /* synthetic */ int val$logDataPos;
        private final /* synthetic */ int val$numberOfRecord;
        private final /* synthetic */ int val$recordIndex;

        C02873(byte[] bArr, int i, MioDeviceCallBack mioDeviceCallBack, int i2, int i3) {
            this.val$data = bArr;
            this.val$logDataPos = i;
            this.val$callBack = mioDeviceCallBack;
            this.val$numberOfRecord = i2;
            this.val$recordIndex = i3;
        }

        public void run() {
            ExerciseRecord exerciseRecord = MioSportMsgParserUtil.parserExerciseRecordData(this.val$data, this.val$logDataPos);
            if (this.val$callBack != null) {
                this.val$callBack.onGetAlpha2Record(exerciseRecord, (short) this.val$numberOfRecord, (short) this.val$recordIndex, (byte) 0);
            }
        }
    }

    static {
        isDebug = false;
        logfile = new SimpleDateFormat(DateFormats.YMD);
        myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SDCARD_PATH = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append(InternalZipConstants.ZIP_FILE_SEPARATOR).toString();
    }

    public static String printRaw(String tag, byte[] raw) {
        String str = DiffResult.OBJECTS_SAME_STRING;
        int num = 0;
        for (byte b : raw) {
            String codeStr = Integer.toHexString(b & 255);
            if (codeStr.length() < 2) {
                codeStr = "0" + codeStr;
            }
            str = new StringBuilder(String.valueOf(str)).append(codeStr).toString();
            if (str.length() > 0) {
                str = new StringBuilder(String.valueOf(str)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                num++;
            }
            if (num == 8) {
                str = new StringBuilder(String.valueOf(str)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
            } else if (num == 16) {
                str = new StringBuilder(String.valueOf(str)).append(IOUtils.LINE_SEPARATOR_WINDOWS).toString();
                num = 0;
            }
        }
        Log.e(tag, "  ====>  " + str);
        return str;
    }

    public static String[] printRaws(String tag, byte[] raw) {
        String[] strs = new String[raw.length];
        String tempStr = DiffResult.OBJECTS_SAME_STRING;
        for (int i = 0; i < raw.length; i++) {
            String codeStr = Integer.toHexString(raw[i] & 255);
            if (codeStr.length() < 2) {
                codeStr = "0" + codeStr;
            }
            strs[i] = codeStr;
        }
        return strs;
    }

    public static ADLTodayData parserADLTodayData(byte[] data) {
        ADLTodayData tData = new ADLTodayData();
        if (data != null && data.length >= 18) {
            tData.rmr = (data[0] & 255) + ((data[1] & 255) << 8);
            tData.step = (((data[2] & 255) + ((data[3] & 255) << 8)) + ((data[4] & 255) << 16)) + ((data[RUN_MIN_RUNNING_PACE_KM] & 255) << 24);
            tData.dist = (((data[6] & 255) + ((data[7] & 255) << 8)) + ((data[8] & 255) << 16)) + ((data[9] & 255) << 24);
            tData.calorie = (short) ((data[10] & 255) + ((data[11] & 255) << 8));
            tData.actTimeOfWalk = (data[12] & 255) + ((data[13] & 255) << 8);
            tData.actTimeOfRun = (data[14] & 255) + ((data[15] & 255) << 8);
            if ((data[16] & 1) == 1) {
                tData.actTimeOfWalk += InternalZipConstants.MIN_SPLIT_LENGTH;
            }
            if ((data[16] & 2) == 2) {
                tData.actTimeOfRun += InternalZipConstants.MIN_SPLIT_LENGTH;
            }
        }
        return tData;
    }

    public static void parserWorkoutRecordData(byte[] data, MioDeviceCallBack callBack, int numberOfRecord, int recordIndex) {
        new Thread(new C02851(data, callBack, numberOfRecord, recordIndex)).start();
    }

    public static List<OneMinuteSleepTracking> parserSleepRecordData(byte[] data) {
        List<OneMinuteSleepTracking> records = new ArrayList();
        String[] log = printRaws(DiffResult.OBJECTS_SAME_STRING, data);
        if (data != null) {
            int tempIndex = 0;
            short lastYear = (short) 0;
            byte lastMonth = (byte) 0;
            byte lastDay = (byte) 0;
            int lastHr = 0;
            while (tempIndex < data.length) {
                int dataLength = data[tempIndex] & 255;
                int i = 2;
                while (i < dataLength && tempIndex + i < data.length) {
                    boolean z;
                    OneMinuteSleepTracking record = new OneMinuteSleepTracking();
                    records.add(record);
                    if ((data[tempIndex + i] & 1) == 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.sleepMode = z;
                    if ((data[tempIndex + i] & 2) == 2) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.dateStampPresent = z;
                    if ((data[tempIndex + i] & 4) == 4) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.timeStampPresent = z;
                    if ((data[tempIndex + i] & 8) == 8) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.stepDataPresent = z;
                    if ((data[tempIndex + i] & 16) == 16) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.hrDataPresent = z;
                    if ((data[tempIndex + i] & 32) == 32) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.calorieDataPresent = z;
                    if ((data[tempIndex + i] & 64) == 64) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.blankMarker = z;
                    if ((data[tempIndex + i] & TransportMediator.FLAG_KEY_MEDIA_NEXT) == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.extFlagsPresent = z;
                    if (record.extFlagsPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                    }
                    try {
                        if (record.dateStampPresent) {
                            record.recordTime = new TimeData();
                            if ((tempIndex + i) + 1 >= data.length) {
                                break;
                            }
                            record.recordTime.day = data[(tempIndex + i) + 1];
                            if ((tempIndex + i) + 2 >= data.length) {
                                break;
                            }
                            record.recordTime.month = data[(tempIndex + i) + 2];
                            if ((tempIndex + i) + 3 >= data.length) {
                                break;
                            }
                            record.recordTime.year = (short) (data[(tempIndex + i) + 3] + 1900);
                            lastYear = record.recordTime.year;
                            lastMonth = record.recordTime.month;
                            lastDay = record.recordTime.day;
                            i += 3;
                        }
                        if (record.timeStampPresent) {
                            if (record.recordTime == null) {
                                record.recordTime = new TimeData();
                                record.recordTime.day = lastDay;
                                record.recordTime.month = lastMonth;
                                record.recordTime.year = lastYear;
                            }
                            if ((tempIndex + i) + 1 >= data.length) {
                                break;
                            }
                            record.recordTime.minute = data[(tempIndex + i) + 1];
                            if (record.recordTime.minute > 0) {
                                TimeData timeData = record.recordTime;
                                timeData.minute = (byte) (timeData.minute - 1);
                            }
                            if ((tempIndex + i) + 2 >= data.length) {
                                break;
                            }
                            record.recordTime.hour = data[(tempIndex + i) + 2];
                            i += 2;
                            Log.e("time", record.recordTime.toString());
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                    if (tempIndex + i >= data.length) {
                        break;
                    }
                    record.ActivityIndex = data[tempIndex + i] & 255;
                    if (record.stepDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.StepData = data[tempIndex + i] & 255;
                    }
                    if (record.hrDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.HRData = data[tempIndex + i] & 255;
                        if (record.HRData > 0) {
                            lastHr = record.HRData;
                        } else {
                            record.HRData = lastHr;
                        }
                    } else {
                        record.HRData = lastHr;
                    }
                    if (record.calorieDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.CalorieData = data[tempIndex + i] & 255;
                    }
                    Log.e("record", record.toString());
                    i++;
                }
                tempIndex += dataLength;
            }
        }
        if (records.size() > 0) {
            ((OneMinuteSleepTracking) records.get(0)).log = log;
        }
        return records;
    }

    private static List<OneHourSleepTracking> parserSleepRecordDataEx(byte[] data) {
        List<OneHourSleepTracking> records = parserSleepRecordDatas(data);
        if (records == null) {
            return new ArrayList();
        }
        return records;
    }

    public static void parserCurHourSleepRecordData(byte[] data, MioDeviceCallBack callBack, List<OneMinuteSleepTracking> sleepList, boolean isFullMemory) {
        int i;
        int m;
        Date tDate;
        int tempHr;
        String[] log1 = null;
        String[] log2 = null;
        List<OneHourSleepTracking> hourSleepList = new ArrayList();
        List<OneMinuteSleepTracking> curSleepList = parserSleepRecordData(data);
        if (sleepList != null && sleepList.size() > 0) {
            log1 = ((OneMinuteSleepTracking) sleepList.get(0)).log;
        }
        if (curSleepList != null && curSleepList.size() > 0) {
            log2 = ((OneMinuteSleepTracking) curSleepList.get(0)).log;
        }
        if (!(isFullMemory || curSleepList == null || curSleepList.size() <= 0)) {
            for (i = 0; i < curSleepList.size(); i++) {
                sleepList.add((OneMinuteSleepTracking) curSleepList.get(i));
            }
            curSleepList.clear();
        }
        short lastYear = (short) 0;
        byte lastMonth = (byte) 0;
        byte lastDay = (byte) 0;
        byte lastHour = (byte) 0;
        byte lastMinute = (byte) 0;
        int mmNum = 0;
        for (i = sleepList.size() - 1; i >= 0; i--) {
            OneMinuteSleepTracking om = (OneMinuteSleepTracking) sleepList.get(i);
            mmNum++;
            if (om.recordTime != null) {
                Log.e("lastTime1", new StringBuilder(String.valueOf(om.recordTime.toString())).append("--").append(mmNum).toString());
                lastYear = om.recordTime.year;
                lastMonth = om.recordTime.month;
                lastDay = om.recordTime.day;
                lastHour = om.recordTime.hour;
                lastMinute = om.recordTime.minute;
                break;
            }
        }
        OneHourSleepTracking tempData = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(new StringBuilder(String.valueOf(lastYear)).append("-").append(lastMonth).append("-").append(lastDay).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(lastHour).append(":01:00").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        long curTime = date.getTime();
        int tNum = mmNum;
        for (i = sleepList.size() - 1; i >= 0; i--) {
            TimeData tempTime;
            OneMinuteSleepTracking toneData;
            if (tNum <= 0) {
                curTime -= DateUtils.MILLIS_PER_HOUR;
                tempData = null;
                tNum = 60;
            }
            OneMinuteSleepTracking oneData = (OneMinuteSleepTracking) sleepList.get(i);
            if (oneData.blankMarker) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                tempTime = null;
                m = i - 1;
                while (m >= 0 && m >= 0) {
                    toneData = (OneMinuteSleepTracking) sleepList.get(m);
                    if (toneData.recordTime != null) {
                        tempTime = toneData.recordTime;
                        TimeData timeData = tempData.recordTime;
                        timeData.minute = (byte) (59 - tempData.allRecords.size());
                        break;
                    }
                    m--;
                }
                if (tempTime != null) {
                    tDate = null;
                    try {
                        tDate = format.parse(new StringBuilder(String.valueOf(tempTime.year)).append("-").append(tempTime.month).append("-").append(tempTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(tempTime.hour).append(":01:00").toString());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    curTime = tDate.getTime() + DateUtils.MILLIS_PER_HOUR;
                    tNum = 0;
                }
            }
            if (tempData == null) {
                tempData = new OneHourSleepTracking();
                hourSleepList.add(0, tempData);
                tempData.recordTime = new TimeData();
                Date dt = new Date(curTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt);
                TimeData timeData = tempData.recordTime;
                timeData.year = (short) calendar.get(Calendar.YEAR);
                byte b = (byte) (calendar.get(Calendar.MONTH) + 1);
                tempData.recordTime.month = b;
                timeData = tempData.recordTime;
                timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
                timeData = tempData.recordTime;
                timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                if (oneData.blankMarker && tNum == 0) {
                    tempData.recordTime.minute = (byte) 59;
                }
            }
            oneData.recordTime = null;
            tempData.allRecords.add(0, oneData);
            tNum--;
        }
        if (tempData != null) {
            TimeData timeData = tempData.recordTime;
            timeData.minute = (byte) (60 - tempData.allRecords.size());
            if (tempData.recordTime.minute < 0) {
                tempData.recordTime.minute = (byte) 0;
            }
        }
        if (hourSleepList.size() > 0) {
            ((OneHourSleepTracking) hourSleepList.get(0)).log = log1;
        }
        for (m = 0; m < hourSleepList.size(); m++) {
            OneMinuteSleepTracking temp;
            OneHourSleepTracking oneData2 = (OneHourSleepTracking) hourSleepList.get(m);
            if (oneData2.allRecords.size() > 0) {
                tempHr = 0;
                i = 0;
                while (true) {
                    if (i >= oneData2.allRecords.size()) {
                        break;
                    }
                    temp = (OneMinuteSleepTracking) oneData2.allRecords.get(i);
                    temp.HRLogData = temp.HRData;
                    if (temp.HRData > 0) {
                        tempHr = temp.HRData;
                    } else {
                        temp.HRData = tempHr;
                    }
                    i++;
                }
            }
        }
        if (!isFullMemory && hourSleepList.size() > 0) {
            OneHourSleepTracking oneData2 = (OneHourSleepTracking) hourSleepList.get(hourSleepList.size() - 1);
            oneData2.recordTime.minute = lastMinute;
            if (oneData2.recordTime.minute + oneData2.allRecords.size() > 60) {
                oneData2.recordTime.minute = (byte) 0;
            }
        }
        if (curSleepList != null && curSleepList.size() > 0) {
            List<OneHourSleepTracking> curHourSleepList = new ArrayList();
            lastYear = (short) 0;
            lastMonth = (byte) 0;
            lastDay = (byte) 0;
            lastHour = (byte) 0;
            byte lastMinite = (byte) 0;
            mmNum = 0;
            for (i = curSleepList.size() - 1; i >= 0; i--) {
                OneMinuteSleepTracking om = (OneMinuteSleepTracking) curSleepList.get(i);
                mmNum++;
                if (om.recordTime != null) {
                    Log.e("lastTime2", new StringBuilder(String.valueOf(om.recordTime.toString())).append("-").append(mmNum).toString());
                    lastYear = om.recordTime.year;
                    lastMonth = om.recordTime.month;
                    lastDay = om.recordTime.day;
                    lastHour = om.recordTime.hour;
                    lastMinite = om.recordTime.minute;
                    break;
                }
            }
            tempData = null;
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = null;
            try {
                date = format.parse(new StringBuilder(String.valueOf(lastYear)).append("-").append(lastMonth).append("-").append(lastDay).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(lastHour).append(":01:00").toString());
            } catch (Exception e22) {
                e22.printStackTrace();
            }
            curTime = date.getTime();
            tNum = mmNum;
            for (i = curSleepList.size() - 1; i >= 0; i--) {
                if (tNum <= 0) {
                    curTime -= DateUtils.MILLIS_PER_HOUR;
                    tempData = null;
                    tNum = 60;
                }
                OneMinuteSleepTracking oneData = (OneMinuteSleepTracking) curSleepList.get(i);
                if (oneData.blankMarker) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    TimeData tempTime = null;
                    m = i - 1;
                    while (m >= 0 && m >= 0) {
                        OneMinuteSleepTracking toneData = (OneMinuteSleepTracking) curSleepList.get(m);
                        if (toneData.recordTime != null) {
                            tempTime = toneData.recordTime;
                            tempTime.minute = (byte) (59 - tempData.allRecords.size());
                            break;
                        }
                        m--;
                    }
                    if (tempTime != null) {
                        tDate = null;
                        try {
                            tDate = format.parse(new StringBuilder(String.valueOf(tempTime.year)).append("-").append(tempTime.month).append("-").append(tempTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(tempTime.hour).append(":01:00").toString());
                        } catch (Exception e222) {
                            e222.printStackTrace();
                        }
                        curTime = tDate.getTime() + DateUtils.MILLIS_PER_HOUR;
                        tNum = 0;
                    }
                }
                if (tempData == null) {
                    tempData = new OneHourSleepTracking();
                    curHourSleepList.add(0, tempData);
                    tempData.recordTime = new TimeData();
                    Date dt = new Date(curTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    TimeData timeData = tempData.recordTime;
                    timeData.year = (short) calendar.get(Calendar.YEAR);
                    byte b = (byte) (calendar.get(Calendar.MONTH) + 1);
                    tempData.recordTime.month = b;
                    timeData = tempData.recordTime;
                    timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
                    timeData = tempData.recordTime;
                    timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    if (oneData.blankMarker && tNum == 0) {
                        tempData.recordTime.minute = (byte) 59;
                    }
                }
                oneData.recordTime = null;
                tempData.allRecords.add(0, oneData);
                tNum--;
            }
            if (tempData != null) {
                TimeData timeData = tempData.recordTime;
                timeData.minute = (byte) (60 - tempData.allRecords.size());
                if (tempData.recordTime.minute < 0) {
                    tempData.recordTime.minute = (byte) 0;
                }
            }
            for (m = 0; m < curHourSleepList.size(); m++) {
                OneHourSleepTracking oneData2 = (OneHourSleepTracking) curHourSleepList.get(m);
                if (oneData2.allRecords.size() > 0) {
                    tempHr = 0;
                    i = 0;
                    while (true) {
                        if (i >= oneData2.allRecords.size()) {
                            break;
                        }
                        OneMinuteSleepTracking temp = (OneMinuteSleepTracking) oneData2.allRecords.get(i);
                        temp.HRLogData = temp.HRData;
                        if (temp.HRData > 0) {
                            tempHr = temp.HRData;
                        } else {
                            temp.HRData = tempHr;
                        }
                        i++;
                    }
                }
            }
            for (i = 0; i < curHourSleepList.size(); i++) {
                hourSleepList.add((OneHourSleepTracking) curHourSleepList.get(i));
            }
            if (curHourSleepList.size() > 0) {
                OneHourSleepTracking oneData2 = (OneHourSleepTracking) curHourSleepList.get(curHourSleepList.size() - 1);
                oneData2.recordTime.minute = lastMinite;
                if (oneData2.recordTime.minute + oneData2.allRecords.size() > 60) {
                    oneData2.recordTime.minute = (byte) 0;
                }
            }
        }
        List<OneHourSleepTracking> tempList = new ArrayList();
        OneHourSleepTracking tempHour = new OneHourSleepTracking();
        tempList.add(tempHour);
        for (i = 0; i < hourSleepList.size(); i++) {
            OneHourSleepTracking temp2 = (OneHourSleepTracking) hourSleepList.get(i);
            int j = 0;
            while (true) {
                if (j >= temp2.allRecords.size()) {
                    break;
                }
                OneMinuteSleepTracking mm = (OneMinuteSleepTracking) temp2.allRecords.get(j);
                if (mm.blankMarker) {
                    tempHour = new OneHourSleepTracking();
                    tempList.add(tempHour);
                    tempHour.allRecords.add(mm);
                } else {
                    tempHour.allRecords.add(mm);
                }
                j++;
            }
        }
        for (i = 0; i < tempList.size(); i++) {
            OneHourSleepTracking temp2 = (OneHourSleepTracking) tempList.get(i);
            if (temp2.allRecords.size() > 0) {
                int[] activitys = new int[temp2.allRecords.size()];
                int j = 0;
                while (true) {
                    if (j >= temp2.allRecords.size()) {
                        break;
                    }
                    activitys[j] = ((OneMinuteSleepTracking) temp2.allRecords.get(j)).ActivityIndex;
                    j++;
                }
                int[] ret = SleepCal.SleepTracking_MIO(activitys, activitys.length, 0);
                j = 0;
                while (true) {
                    if (j >= temp2.allRecords.size()) {
                        break;
                    }
                    OneMinuteSleepTracking mm = (OneMinuteSleepTracking) temp2.allRecords.get(j);
                    mm.sleepStatus = ret[j];
                    j++;
                }
            }
        }
        if (hourSleepList.size() > 0) {
            ((OneHourSleepTracking) hourSleepList.get(hourSleepList.size() - 1)).log = log2;
        }
        if (hourSleepList.size() > 1) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = null;
            OneHourSleepTracking mm2 = (OneHourSleepTracking) hourSleepList.get(hourSleepList.size() - 1);
            if (!((OneMinuteSleepTracking) mm2.allRecords.get(0)).blankMarker) {
                try {
                    date = format.parse(new StringBuilder(String.valueOf(mm2.recordTime.year)).append("-").append(mm2.recordTime.month).append("-").append(mm2.recordTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(mm2.recordTime.hour).append(":00:00").toString());
                } catch (Exception e2222) {
                    e2222.printStackTrace();
                }
                curTime = date.getTime();
                long time1 = curTime - System.currentTimeMillis();
                if (time1 > -1702967296 || time1 < 1702967296) {
                    Date dt = new Date(System.currentTimeMillis());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    TimeData timeData = mm2.recordTime;
                    timeData.year = (short) calendar.get(Calendar.YEAR);
                    byte b = (byte) (calendar.get(Calendar.MONTH) + 1);
                    mm2.recordTime.month = b;
                    timeData = mm2.recordTime;
                    timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
                    timeData = mm2.recordTime;
                    timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    try {
                        curTime = format.parse(new StringBuilder(String.valueOf(mm2.recordTime.year)).append("-").append(mm2.recordTime.month).append("-").append(b = mm2.recordTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(mm2.recordTime.hour).append(":00:00").toString()).getTime();
                    } catch (Exception e22222) {
                        e22222.printStackTrace();
                    }
                }
                for (i = hourSleepList.size() - 2; i >= 0; i--) {
                    OneHourSleepTracking mmm = (OneHourSleepTracking) hourSleepList.get(i);
                    if (((OneMinuteSleepTracking) mmm.allRecords.get(0)).blankMarker) {
                        break;
                    }
                    curTime -= DateUtils.MILLIS_PER_HOUR;
                    Date dt = new Date(curTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    TimeData timeData = mmm.recordTime;
                    timeData.year = (short) calendar.get(Calendar.YEAR);
                    byte b = (byte) (calendar.get(Calendar.MONTH) + 1);
                    mmm.recordTime.month = b;
                    timeData = mmm.recordTime;
                    timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
                    timeData = mmm.recordTime;
                    timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                }
            }
        }
        if (callBack != null) {
            callBack.DidGetSleepTrackList_MIO(hourSleepList, (byte) 0);
        }
        sleepList.clear();
    }

    private static void parserCurHourSleepRecordDataEx(byte[] data, MioDeviceCallBack callBack, List<OneHourSleepTracking> sleepList) {
        int i;
        OneHourSleepTracking record = parserCurHourSleepRecordData(data);
        if (record != null) {
            if (record.allRecords.size() > 0) {
                sleepList.add(record);
            }
        }
        List<OneHourSleepTracking> tempList = new ArrayList();
        OneHourSleepTracking tempHour = new OneHourSleepTracking();
        tempList.add(tempHour);
        for (i = 0; i < sleepList.size(); i++) {
            OneHourSleepTracking temp = (OneHourSleepTracking) sleepList.get(i);
            int j = 0;
            while (true) {
                if (j >= temp.allRecords.size()) {
                    break;
                }
                OneMinuteSleepTracking mm = (OneMinuteSleepTracking) temp.allRecords.get(j);
                if (mm.blankMarker) {
                    tempHour = new OneHourSleepTracking();
                    tempList.add(tempHour);
                    tempHour.allRecords.add(mm);
                } else {
                    tempHour.allRecords.add(mm);
                }
                j++;
            }
        }
        for (i = 0; i < tempList.size(); i++) {
            OneHourSleepTracking temp = (OneHourSleepTracking) tempList.get(i);
            if (temp.allRecords.size() > 0) {
                int[] activitys = new int[temp.allRecords.size()];
                int j = 0;
                while (true) {
                    if (j >= temp.allRecords.size()) {
                        break;
                    }
                    activitys[j] = ((OneMinuteSleepTracking) temp.allRecords.get(j)).ActivityIndex;
                    j++;
                }
                int[] ret = SleepCal.SleepTracking_MIO(activitys, activitys.length, 0);
                j = 0;
                while (true) {
                    if (j >= temp.allRecords.size()) {
                        break;
                    }
                    OneMinuteSleepTracking mm = (OneMinuteSleepTracking) temp.allRecords.get(j);
                    mm.sleepStatus = ret[j];
                    j++;
                }
            }
        }
        if (sleepList.size() > 1) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            OneHourSleepTracking mm2 = (OneHourSleepTracking) sleepList.get(sleepList.size() - 1);
            if (!((OneMinuteSleepTracking) mm2.allRecords.get(0)).blankMarker) {
                Date dt;
                Calendar calendar;
                TimeData timeData;
                byte b;
                try {
                    date = format.parse(new StringBuilder(String.valueOf(mm2.recordTime.year)).append("-").append(mm2.recordTime.month).append("-").append(mm2.recordTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(mm2.recordTime.hour).append(":00:00").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long curTime = date.getTime();
                long time1 = curTime - System.currentTimeMillis();
                if (time1 > -1702967296 || time1 < 1702967296) {
                    dt = new Date(System.currentTimeMillis());
                    calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    timeData = mm2.recordTime;
                    timeData.year = (short) calendar.get(Calendar.YEAR);
                    b = (byte) (calendar.get(Calendar.MONTH) + 1);
                    mm2.recordTime.month = b;
                    timeData = mm2.recordTime;
                    timeData.day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                    timeData = mm2.recordTime;
                    timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                    try {
                        curTime = format.parse(new StringBuilder(String.valueOf(mm2.recordTime.year)).append("-").append(mm2.recordTime.month).append("-").append(mm2.recordTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(mm2.recordTime.hour).append(":00:00").toString()).getTime();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                for (i = sleepList.size() - 2; i >= 0; i--) {
                    OneHourSleepTracking mmm = (OneHourSleepTracking) sleepList.get(i);
                    if (((OneMinuteSleepTracking) mmm.allRecords.get(0)).blankMarker) {
                        break;
                    }
                    curTime -= DateUtils.MILLIS_PER_HOUR;
                    dt = new Date(curTime);
                    calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    timeData = mmm.recordTime;
                    timeData.year = (short) calendar.get(Calendar.YEAR);
                    b = (byte) (calendar.get(Calendar.MONTH) + 1);
                    mmm.recordTime.month = b;
                    timeData = mmm.recordTime;
                    timeData.day = (byte) calendar.get(Calendar.DAY_OF_MONTH);
                    timeData = mmm.recordTime;
                    timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
                }
            }
        }
        if (callBack != null) {
            callBack.DidGetSleepTrackList_MIO(sleepList, (byte) 0);
        }
    }

    public static void parserVeloRecordData(byte[] data, MioDeviceCallBack callBack, int numberOfRecord, int recordIndex) {
        new Thread(new C02862(data, callBack, numberOfRecord, recordIndex)).start();
    }

    public static VeloRecordData parserVeloRecordData(byte[] data) {
        VeloRecordData veloRecord = null;
        if (data != null) {
            veloRecord = new VeloRecordData();
            veloRecord.Startpage = data[0] & 255;
            veloRecord.Startbyte = data[1] & 255;
            veloRecord.Ctrlinfo = data[2] & 255;
            veloRecord.Workoutsecs = (data[3] & 255) + ((data[4] & 255) << 8);
            veloRecord.Workoutsize = (data[RUN_MIN_RUNNING_PACE_KM] & 255) + ((data[6] & 255) << 8);
            int time = ((((data[7] & 255) | (MotionEventCompat.ACTION_POINTER_INDEX_MASK & (data[8] << 8))) | (16711680 & (data[9] << 16))) | (ViewCompat.MEASURED_STATE_MASK & (data[10] << 24))) & -1;
            int h = (time >> 15) & 31;
            int m = (time >> 20) & 63;
            int s = (time >> 26) & 63;
            veloRecord.WorkoutTime = new StringBuilder(String.valueOf((time & 63) + 2015)).append("-").append((time >> 6) & 15).append("-").append((time >> 10) & 31).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(h).append(":").append(m).append(":").append(s).toString();
            int itemIndex = 11;
            while (true) {
                if (itemIndex >= data.length - 1) {
                    break;
                }
                boolean speed;
                boolean cadence;
                boolean power;
                VeloRowData vrd = new VeloRowData();
                if ((data[itemIndex] & 8) == 8) {
                    speed = true;
                } else {
                    speed = false;
                }
                if ((data[itemIndex] & 16) == 16) {
                    cadence = true;
                } else {
                    cadence = false;
                }
                if ((data[itemIndex] & 32) == 32) {
                    power = true;
                } else {
                    power = false;
                }
                boolean hrv = (data[itemIndex] & 64) == 64;
                int i = data[itemIndex] & TransportMediator.FLAG_KEY_MEDIA_NEXT;
                boolean hr = true;
                int r0 =128;
                itemIndex++;
                if (speed || cadence || power || hrv || hr) {
                    if (speed) {
                        if (itemIndex < data.length - 1) {
                            vrd.SPEED = 0.1f * ((float) (((data[itemIndex - 1] & 255) + ((data[itemIndex] & 255) << 8)) >> 6));
                            itemIndex++;
                        }
                    }
                    if (cadence) {
                        i = data.length;
                        if (itemIndex < r0) {
                            vrd.CADENCE = data[itemIndex] & 255;
                            itemIndex++;
                        }
                    }
                    if (power) {
                        if (itemIndex < data.length - 1) {
                            vrd.POWER = (data[itemIndex] & 255) + ((data[itemIndex + 1] & 255) << 8);
                            itemIndex += 2;
                        }
                    }
                    if (hrv) {
                        if (itemIndex < data.length - 1) {
                            int value = (data[itemIndex] & 255) + ((data[itemIndex + 1] & 255) << 8);
                            int size = value >> 12;
                            for (int i2 = 0; i2 < size && itemIndex + 1 < data.length; i2++) {
                                vrd.HRVS.add(Integer.valueOf(value & 4095));
                                itemIndex += 2;
                                if (itemIndex + 1 >= data.length) {
                                    break;
                                }
                                value = (data[itemIndex] & 255) + ((data[itemIndex + 1] & 255) << 8);
                            }
                        }
                    }
                    if (hr) {
                        i = data.length;
                        if (itemIndex < r0) {
                            vrd.HR = data[itemIndex] & 255;
                            itemIndex++;
                        }
                    }
                    veloRecord.rowDatas.add(vrd);
                }
            }
        }
        return veloRecord;
    }

    private static byte[] intToBytes2(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - (i * 8)));
        }
        return b;
    }

    private static String byteToBit(byte b) {
        return "" + ((byte) ((b >> 7) & 1)) + ((byte) ((b >> 6) & 1)) + ((byte) ((b >> 5) & 1)) + ((byte) ((b >> 4) & 1)) + ((byte) ((b >> 3) & 1)) + ((byte) ((b >> 2) & 1)) + ((byte) ((b >> 1) & 1)) + ((byte) ((b >> 0) & 1));
    }

    private static byte decodeBinaryString(String byteStr) {
        if (byteStr == null) {
            return (byte) 0;
        }
        int len = byteStr.length();
        if (len != 4 && len != 8) {
            return (byte) 0;
        }
        int re;
        if (len != 8) {
            re = Integer.parseInt(byteStr, 2);
        } else if (byteStr.charAt(0) == '0') {
            re = Integer.parseInt(byteStr, 2);
        } else {
            re = Integer.parseInt(byteStr, 2) + InputDeviceCompat.SOURCE_ANY;
        }
        return (byte) re;
    }

    public static WorkoutRecord parserWorkoutRecordData(byte[] data) {
        WorkoutRecord workoutRecord = new WorkoutRecord();
        if (data != null && data.length >= workoutSummarySize) {
            WorkoutRecordSummary wData = new WorkoutRecordSummary();
            wData.time = new TimeData();
            wData.time.second = (byte) (data[0] & 255);
            wData.time.minute = (byte) (data[1] & 255);
            wData.time.hour = (byte) (data[2] & 255);
            wData.time.day = (byte) (data[3] & 255);
            wData.time.month = (byte) (data[4] & 255);
            wData.time.year = (short) (data[RUN_MIN_RUNNING_PACE_KM] & 255);
            if (wData.time.year < (short) 114) {
                TimeData timeData = wData.time;
                timeData.year = (short) (timeData.year + 100);
            }
            wData.exerciseTime = new ExerciseTimeData();
            wData.exerciseTime.second = (byte) (data[6] & 255);
            wData.exerciseTime.minute = (byte) (data[7] & 255);
            wData.exerciseTime.hour = (byte) (data[8] & 255);
            wData.step = (((data[9] & 255) + ((data[10] & 255) << 8)) + ((data[11] & 255) << 16)) + ((data[12] & 255) << 24);
            wData.dist = (((data[13] & 255) + ((data[14] & 255) << 8)) + ((data[15] & 255) << 16)) + ((data[16] & 255) << 24);
            wData.calorie = (short) ((data[17] & 255) + ((data[18] & 255) << 8));
            wData.maxSpeed = (short) ((data[19] & 255) + ((data[20] & 255) << 8));
            wData.timeInZone = (short) ((data[21] & 255) + ((data[22] & 255) << 8));
            wData.timeInZone1 = (short) ((data[23] & 255) + ((data[24] & 255) << 8));
            wData.timeInZone2 = (short) ((data[25] & 255) + ((data[26] & 255) << 8));
            wData.timeInZone3 = (short) ((data[27] & 255) + ((data[28] & 255) << 8));
            wData.timeInZone4 = (short) ((data[29] & 255) + ((data[30] & 255) << 8));
            wData.timeInZone5 = (short) ((data[31] & 255) + ((data[32] & 255) << 8));
            wData.aHR = (short) (data[33] & 255);
            workoutRecord.workoutRecordSummary = wData;
        }
        if (data.length > workoutSummarySize) {
            workoutRecord.logData = new RecordLogData();
            parserRecordLogData(data, workoutSummarySize, data.length - 1, workoutRecord.logData);
        }
        return workoutRecord;
    }

    private static OneHourSleepTracking parserCurHourSleepRecordData(byte[] data) {
        TimeData timeData;
        List<OneMinuteSleepTracking> records = new ArrayList();
        OneHourSleepTracking oneHourSleepTracking = null;
        if (data != null) {
            int i;
            OneMinuteSleepTracking oneData;
            int tempIndex = 0;
            short lastYear = (short) 0;
            byte lastMonth = (byte) 0;
            byte lastDay = (byte) 0;
            byte lastHour = (byte) 0;
            while (true) {
                try {
                    if (tempIndex >= data.length) {
                        break;
                    }
                    int dataLength = data[tempIndex] & 255;
                    i = 2;
                    while (i < dataLength && tempIndex + i < data.length) {
                        boolean z;
                        OneMinuteSleepTracking record = new OneMinuteSleepTracking();
                        records.add(record);
                        if ((data[tempIndex + i] & 1) == 1) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.sleepMode = z;
                        if ((data[tempIndex + i] & 2) == 2) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.dateStampPresent = z;
                        if ((data[tempIndex + i] & 4) == 4) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.timeStampPresent = z;
                        if ((data[tempIndex + i] & 8) == 8) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.stepDataPresent = z;
                        if ((data[tempIndex + i] & 16) == 16) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.hrDataPresent = z;
                        if ((data[tempIndex + i] & 32) == 32) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.calorieDataPresent = z;
                        if ((data[tempIndex + i] & 64) == 64) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.blankMarker = z;
                        int i2 = data[tempIndex + i] & TransportMediator.FLAG_KEY_MEDIA_NEXT;
                        // i believe it is i2?
                        if (i2 == 128) {
                            z = true;
                        } else {
                            z = false;
                        }
                        record.extFlagsPresent = z;
                        if (record.extFlagsPresent) {
                            i++;
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                        }
                        try {
                            if (record.dateStampPresent) {
                                record.recordTime = new TimeData();
                                if ((tempIndex + i) + 1 < data.length) {
                                    timeData = record.recordTime;
                                    timeData.day = data[(tempIndex + i) + 1];
                                    if ((tempIndex + i) + 2 < data.length) {
                                        timeData = record.recordTime;
                                        timeData.month = data[(tempIndex + i) + 2];
                                        if ((tempIndex + i) + 3 < data.length) {
                                            timeData = record.recordTime;
                                            timeData.year = (short) (data[(tempIndex + i) + 3] + 1900);
                                            lastYear = record.recordTime.year;
                                            lastMonth = record.recordTime.month;
                                            lastDay = record.recordTime.day;
                                            i += 3;
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (record.timeStampPresent) {
                                if (record.recordTime == null) {
                                    record.recordTime = new TimeData();
                                    record.recordTime.day = lastDay;
                                    record.recordTime.month = lastMonth;
                                    record.recordTime.year = lastYear;
                                }
                                if ((tempIndex + i) + 1 < data.length) {
                                    timeData = record.recordTime;
                                    timeData.minute = data[(tempIndex + i) + 1];
                                    if (record.recordTime.minute > 0) {
                                        timeData = record.recordTime;
                                        timeData.minute = (byte) (timeData.minute - 1);
                                    }
                                    if ((tempIndex + i) + 2 < data.length) {
                                        timeData = record.recordTime;
                                        timeData.hour = data[(tempIndex + i) + 2];
                                        lastHour = record.recordTime.hour;
                                        i += 2;
                                        if (tempIndex + i >= data.length) {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                        }
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.ActivityIndex = data[tempIndex + i] & 255;
                        if (record.stepDataPresent) {
                            i++;
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                            record.StepData = data[tempIndex + i] & 255;
                        }
                        if (record.hrDataPresent) {
                            i++;
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                            record.HRData = data[tempIndex + i] & 255;
                        }
                        if (record.calorieDataPresent) {
                            i++;
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                            record.CalorieData = data[tempIndex + i] & 255;
                        }
                        i++;
                    }
                    tempIndex += dataLength;
                } catch (Exception e2) {
                    return oneHourSleepTracking;
                }
            }
            i = records.size() - 1;
            OneHourSleepTracking retRecords = null;
            while (i >= 0) {
                try {
                    oneData = (OneMinuteSleepTracking) records.get(i);
                    if (retRecords == null) {
                        oneHourSleepTracking = new OneHourSleepTracking();
                        oneHourSleepTracking.log = printRaws(DiffResult.OBJECTS_SAME_STRING, data);
                        oneHourSleepTracking.recordTime = new TimeData();
                        oneHourSleepTracking.recordTime.year = lastYear;
                        oneHourSleepTracking.recordTime.month = lastMonth;
                        oneHourSleepTracking.recordTime.day = lastDay;
                        oneHourSleepTracking.recordTime.hour = lastHour;
                    } else {
                        oneHourSleepTracking = retRecords;
                    }
                    oneData.recordTime = null;
                    oneHourSleepTracking.allRecords.add(0, oneData);
                    i--;
                    retRecords = oneHourSleepTracking;
                } catch (Exception e3) {
                    return retRecords;
                }
            }
            int m = 0;
            while (true) {
                if (m >= retRecords.allRecords.size()) {
                    break;
                }
                oneData = (OneMinuteSleepTracking) retRecords.allRecords.get(m);
                oneData.HRLogData = oneData.HRData;
                if (oneData.HRData > 0) {
                    int tempHr = oneData.HRData;
                } else {
                    oneData.HRData = 0;
                }
                m++;
            }
            oneHourSleepTracking = retRecords;
        }
        if (oneHourSleepTracking == null) {
            return oneHourSleepTracking;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(new StringBuilder(String.valueOf(oneHourSleepTracking.recordTime.year)).append("-").append(oneHourSleepTracking.recordTime.month).append("-").append(oneHourSleepTracking.recordTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(oneHourSleepTracking.recordTime.hour).append(":").append(oneHourSleepTracking.recordTime.minute).append(":00").toString());
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        long time1 = date.getTime() - System.currentTimeMillis();
        if (time1 <= -1702967296 && time1 >= 1702967296) {
            return oneHourSleepTracking;
        }
        Date dt = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        timeData = oneHourSleepTracking.recordTime;
        timeData.year = (short) calendar.get(Calendar.YEAR);
        byte b = (byte) (calendar.get(Calendar.MONTH) + 1);
        oneHourSleepTracking.recordTime.month = b;
        timeData = oneHourSleepTracking.recordTime;
        timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
        timeData = oneHourSleepTracking.recordTime;
        timeData.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        return oneHourSleepTracking;
    }

    private static List<OneHourSleepTracking> parserSleepRecordDatas(byte[] data) {
        List<OneHourSleepTracking> retRecords = new ArrayList();
        List<OneMinuteSleepTracking> records = new ArrayList();
        String[] log = printRaws(DiffResult.OBJECTS_SAME_STRING, data);
        if (data != null) {
            int i;
            TimeData timeData;
            int m;
            int tempIndex = 0;
            short lastYear = 0;
            byte lastMonth = (byte) 0;
            byte lastDay = 0;
            byte lastHour = 0;
            int lastHr = 0;
            while (tempIndex < data.length) {
                int dataLength = data[tempIndex] & 255;
                i = 2;
                while (i < dataLength && tempIndex + i < data.length) {
                    boolean z;
                    OneMinuteSleepTracking record = new OneMinuteSleepTracking();
                    records.add(record);
                    if ((data[tempIndex + i] & 1) == 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.sleepMode = z;
                    if ((data[tempIndex + i] & 2) == 2) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.dateStampPresent = z;
                    if ((data[tempIndex + i] & 4) == 4) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.timeStampPresent = z;
                    if ((data[tempIndex + i] & 8) == 8) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.stepDataPresent = z;
                    if ((data[tempIndex + i] & 16) == 16) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.hrDataPresent = z;
                    if ((data[tempIndex + i] & 32) == 32) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.calorieDataPresent = z;
                    if ((data[tempIndex + i] & 64) == 64) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.blankMarker = z;
                    int i2 = data[tempIndex + i] & TransportMediator.FLAG_KEY_MEDIA_NEXT;
                    if (i2 == 128) {
                        z = true;
                    } else {
                        z = false;
                    }
                    record.extFlagsPresent = z;
                    if (record.extFlagsPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                    }
                    try {
                        if (record.dateStampPresent) {
                            record.recordTime = new TimeData();
                            if ((tempIndex + i) + 1 >= data.length) {
                                break;
                            }
                            timeData = record.recordTime;
                            timeData.day = data[(tempIndex + i) + 1];
                            if ((tempIndex + i) + 2 >= data.length) {
                                break;
                            }
                            timeData = record.recordTime;
                            timeData.month = data[(tempIndex + i) + 2];
                            if ((tempIndex + i) + 3 >= data.length) {
                                break;
                            }
                            timeData = record.recordTime;
                            timeData.year = (short) (data[(tempIndex + i) + 3] + 1900);
                            lastYear = record.recordTime.year;
                            lastMonth = record.recordTime.month;
                            lastDay = record.recordTime.day;
                            i += 3;
                        }
                        if (record.timeStampPresent) {
                            if (record.recordTime == null) {
                                record.recordTime = new TimeData();
                                record.recordTime.day = lastDay;
                                record.recordTime.month = lastMonth;
                                record.recordTime.year = lastYear;
                            }
                            if ((tempIndex + i) + 1 >= data.length) {
                                break;
                            }
                            timeData = record.recordTime;
                            timeData.minute = data[(tempIndex + i) + 1];
                            if (record.recordTime.minute > 0) {
                                timeData = record.recordTime;
                                timeData.minute = (byte) (timeData.minute - 1);
                            }
                            if ((tempIndex + i) + 2 >= data.length) {
                                break;
                            }
                            timeData = record.recordTime;
                            timeData.hour = data[(tempIndex + i) + 2];
                            lastHour = record.recordTime.hour;
                            i += 2;
                            if (tempIndex + i >= data.length) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                    if (tempIndex + i >= data.length) {
                        break;
                    }
                    record.ActivityIndex = data[tempIndex + i] & 255;
                    if (record.stepDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.StepData = data[tempIndex + i] & 255;
                    }
                    if (record.hrDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.HRData = data[tempIndex + i] & 255;
                        if (record.HRData > 0) {
                            lastHr = record.HRData;
                        } else {
                            record.HRData = lastHr;
                        }
                    } else {
                        record.HRData = lastHr;
                    }
                    if (record.calorieDataPresent) {
                        i++;
                        if (tempIndex + i >= data.length) {
                            break;
                        }
                        record.CalorieData = data[tempIndex + i] & 255;
                    }
                    i++;
                }
                tempIndex += dataLength;
            }
            OneHourSleepTracking tempData = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = format.parse(new StringBuilder(String.valueOf(lastYear)).append("-").append(lastMonth).append("-").append(lastDay).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(lastHour).append(":01:00").toString());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            long curTime = date.getTime();
            int tNum = 60;
            for (i = records.size() - 1; i >= 0; i--) {
                byte b;
                if (tNum <= 0) {
                    curTime -= DateUtils.MILLIS_PER_HOUR;
                    tempData = null;
                    tNum = 60;
                }
                OneMinuteSleepTracking oneData = (OneMinuteSleepTracking) records.get(i);
                if (oneData.blankMarker) {
                    Log.e("blankMarker", oneData.recordTime.toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    TimeData tempTime = null;
                    m = i - 1;
                    while (m >= 0 && m >= 0) {
                        OneMinuteSleepTracking toneData = (OneMinuteSleepTracking) records.get(m);
                        if (toneData.recordTime != null) {
                            Log.e("blankMarker", toneData.recordTime.toString());
                            tempTime = toneData.recordTime;
                            timeData = tempData.recordTime;
                            timeData.minute = (byte) (59 - tempData.allRecords.size());
                            break;
                        }
                        m--;
                    }
                    if (tempTime != null) {
                        Date tDate = null;
                        try {
                            tDate = format.parse(new StringBuilder(String.valueOf(tempTime.year)).append("-").append(tempTime.month).append("-").append(tempTime.day).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(tempTime.hour).append(":01:00").toString());
                        } catch (Exception e22) {
                            e22.printStackTrace();
                        }
                        curTime = tDate.getTime() + DateUtils.MILLIS_PER_HOUR;
                        tNum = 0;
                    }
                }
                if (tempData == null) {
                    tempData = new OneHourSleepTracking();
                    retRecords.add(0, tempData);
                    tempData.recordTime = new TimeData();
                    Date dt = new Date(curTime);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dt);
                    timeData = tempData.recordTime;
                    timeData.year = (short) calendar.get(1);
                    b = (byte) (calendar.get(2) + 1);
                    tempData.recordTime.month = b;
                    timeData = tempData.recordTime;
                    timeData.day = (byte) calendar.get(RUN_MIN_RUNNING_PACE_KM);
                    timeData = tempData.recordTime;
                    timeData.hour = (byte) calendar.get(11);
                    if (oneData.blankMarker && tNum == 0) {
                        tempData.recordTime.minute = (byte) 59;
                    }
                }
                oneData.recordTime = null;
                tempData.allRecords.add(0, oneData);
                tNum--;
            }
            if (tempData != null) {
                timeData = tempData.recordTime;
                timeData.minute = (byte) (60 - tempData.allRecords.size());
                if (tempData.recordTime.minute < 0) {
                    tempData.recordTime.minute = (byte) 0;
                }
            }
            records.clear();
            if (retRecords.size() > 0) {
                ((OneHourSleepTracking) retRecords.get(0)).log = log;
            }
            for (m = 0; m < retRecords.size(); m++) {
                OneHourSleepTracking oneData2 = (OneHourSleepTracking) retRecords.get(m);
                if (oneData2.allRecords.size() > 0) {
                    int tempHr = 0;
                    i = 0;
                    while (true) {
                        if (i >= oneData2.allRecords.size()) {
                            break;
                        }
                        OneMinuteSleepTracking temp = (OneMinuteSleepTracking) oneData2.allRecords.get(i);
                        temp.HRLogData = temp.HRData;
                        if (temp.HRData > 0) {
                            tempHr = temp.HRData;
                        } else {
                            temp.HRData = tempHr;
                        }
                        i++;
                    }
                }
            }
        }
        return retRecords;
    }

    public static ADLDailyData parserADLDailyData(byte[] data) {
        ADLDailyData dData = new ADLDailyData();
        if (data != null && data.length >= 26) {
            dData.day = (byte) (data[0] & 255);
            dData.month = (byte) (data[1] & 255);
            dData.year = (short) (data[2] & 255);
            if (dData.year < (short) 114) {
                dData.year = (short) (dData.year + 100);
            }
            dData.rmr = (data[3] & 255) + ((data[4] & 255) << 8);
            dData.step = (((data[RUN_MIN_RUNNING_PACE_KM] & 255) + ((data[6] & 255) << 8)) + ((data[7] & 255) << 16)) + ((data[8] & 255) << 24);
            dData.dist = (((data[9] & 255) + ((data[10] & 255) << 8)) + ((data[11] & 255) << 16)) + ((data[12] & 255) << 24);
            dData.calorie = (short) ((data[13] & 255) + ((data[14] & 255) << 8));
            dData.actTimeOfWalk = (data[15] & 255) + ((data[16] & 255) << 8);
            dData.actTimeOfRun = (data[17] & 255) + ((data[18] & 255) << 8);
            if ((data[19] & 1) == 1) {
                dData.actTimeOfWalk += InternalZipConstants.MIN_SPLIT_LENGTH;
            }
            if ((data[19] & 2) == 2) {
                dData.actTimeOfRun += InternalZipConstants.MIN_SPLIT_LENGTH;
            }
            dData.dailyGoalValue = (((data[21] & 255) + ((data[22] & 255) << 8)) + ((data[23] & 255) << 16)) + ((data[24] & 255) << 24);
            dData.dailyGoalType = data[25];
        }
        return dData;
    }

    public static void parserExerciseRecordData(byte[] data, MioDeviceCallBack callBack, int numberOfRecord, int recordIndex, int logDataPos) {
        new Thread(new C02873(data, logDataPos, callBack, numberOfRecord, recordIndex)).start();
    }

    public static ExerciseRecord parserExerciseRecordData(byte[] data, int logDataPos) {
        ExerciseRecord exerciseRecord = new ExerciseRecord();
        exerciseRecord.exerciseSummary = new ExerciseOrLapSummary();
        parserExerciseOrLapSummary(data, 0, exerciseRecord.exerciseSummary);
        int startPos = logDataPos;
        int lapNum = (startPos - 48) / exerciseOrLapSummarySize;
        exerciseRecord.LapSummaryArray = new ArrayList();
        for (int i = 0; i < lapNum; i++) {
            ExerciseOrLapSummary item = new ExerciseOrLapSummary();
            parserExerciseOrLapSummary(data, (i + 1) * exerciseOrLapSummarySize, item);
            exerciseRecord.LapSummaryArray.add(item);
        }
        if (startPos >= exerciseOrLapSummarySize) {
            exerciseRecord.logData = new RecordLogData();
            parserRecordLogData(data, startPos, data.length - 1, exerciseRecord.logData);
        }
        return exerciseRecord;
    }

    private static void parserRecordLogData(byte[] data, int startPos, int endPos, RecordLogData logData) {
        if (startPos >= 0 && endPos < data.length) {
            for (int i = startPos; i <= endPos; i++) {
                int value = data[i] & 255;
                if (value > 0) {
                    int index = i - startPos;
                    if ((index + 1) % 76 == 0) {
                        logData.stepArray.add(Integer.valueOf(value));
                    } else {
                        int cc = (index % 76) % 15;
                        if (cc <= 11) {
                            logData.hrArray.add(Integer.valueOf(value));
                        } else if (cc <= 13) {
                            logData.distArray.add(Integer.valueOf(value));
                            float speed = (((float) value) * 3.6f) / 12.0f;
                            logData.speedArray.add(Float.valueOf(speed));
                            int pace = (int) (((float) 36000) / (10.0f * speed));
                            if (pace < RUN_MIN_RUNNING_PACE_KM) {
                                pace = RUN_MIN_RUNNING_PACE_KM;
                            } else if (pace > RUN_MAX_RUNNING_PACE) {
                                pace = RUN_MAX_RUNNING_PACE;
                            }
                            logData.paceArray.add(Integer.valueOf(pace));
                        } else {
                            logData.calorieArray.add(Integer.valueOf(value));
                        }
                    }
                }
            }
        }
    }

    private static void parserExerciseOrLapSummary(byte[] data, int startPos, ExerciseOrLapSummary wData) {
        if (data != null && data.length >= startPos + exerciseOrLapSummarySize) {
            wData.startTime = new TimeData();
            wData.startTime.year = (short) (data[startPos + 0] & 255);
            if (wData.startTime.year < (short) 114) {
                TimeData timeData = wData.startTime;
                timeData.year = (short) (timeData.year + 100);
            }
            wData.startTime.month = (byte) (data[startPos + 1] & 255);
            wData.startTime.day = (byte) (data[startPos + 2] & 255);
            wData.startTime.hour = (byte) (data[startPos + 3] & 255);
            wData.startTime.minute = (byte) (data[startPos + 4] & 255);
            wData.startTime.second = (byte) (data[startPos + RUN_MIN_RUNNING_PACE_KM] & 255);
            wData.workoutType = (byte) (data[startPos + 6] & 255);
            int value = data[startPos + 7] & 255;
            if (value == 255) {
                wData.recordType = (byte) 2;
            } else if (value > PullerInternal.MAX_PENDING_DOCS || value < 100) {
                wData.recordType = (byte) 0;
                wData.lapIndex = (short) value;
            } else {
                wData.recordType = (byte) 1;
                wData.lapIndex = (short) (value - 100);
            }
            wData.workoutTime = new ExerciseTimeData();
            wData.workoutTime.second = (byte) (data[startPos + 10] & 255);
            wData.workoutTime.minute = (byte) (data[startPos + 9] & 255);
            wData.workoutTime.hour = (byte) (data[startPos + 8] & 255);
            wData.dist = (data[startPos + 11] & 255) + ((data[startPos + 12] & 255) << 8);
            wData.step = ((data[startPos + 13] & 255) + ((data[startPos + 14] & 255) << 8)) + ((data[startPos + 15] & 255) << 16);
            wData.calorie = (short) ((data[startPos + 16] & 255) + ((data[startPos + 17] & 255) << 8));
            wData.averageHR = (short) (data[startPos + 18] & 255);
            wData.maxHR = (short) (data[startPos + 19] & 255);
            wData.minHR = (short) (data[startPos + 20] & 255);
            wData.averageSpeed = ((float) ((data[startPos + 21] & 255) + ((data[startPos + 22] & 255) << 8))) / 100.0f;
            wData.maxSpeed = ((float) ((data[startPos + 23] & 255) + ((data[startPos + 24] & 255) << 8))) / 100.0f;
            wData.averagePace = new PaceTime();
            wData.averagePace.minutes = (short) (data[startPos + 25] & 255);
            wData.averagePace.seconds = (short) (data[startPos + 26] & 255);
            wData.maxPace = new PaceTime();
            wData.maxPace.minutes = (short) (data[startPos + 27] & 255);
            wData.maxPace.seconds = (short) (data[startPos + 28] & 255);
            wData.timeInUpLowZone = (short) (((data[startPos + 29] & 255) + ((data[startPos + 30] & 255) << 8)) + ((data[startPos + 45] & 1) << 16));
            wData.timeInZone1 = (short) (((data[startPos + 31] & 255) + ((data[startPos + 32] & 255) << 8)) + ((data[startPos + 45] & 2) << 16));
            wData.timeInZone2 = (short) (((data[startPos + 33] & 255) + ((data[startPos + workoutSummarySize] & 255) << 8)) + ((data[startPos + 45] & 4) << 16));
            wData.timeInZone3 = (short) (((data[startPos + 35] & 255) + ((data[startPos + 36] & 255) << 8)) + ((data[startPos + 45] & 8) << 16));
            wData.timeInZone4 = (short) (((data[startPos + 37] & 255) + ((data[startPos + 38] & 255) << 8)) + ((data[startPos + 45] & 16) << 16));
            wData.timeInZone5 = (short) (((data[startPos + 39] & 255) + ((data[startPos + 40] & 255) << 8)) + ((data[startPos + 45] & 32) << 16));
            wData.blockStartIndex = (data[startPos + 41] & 255) + ((data[startPos + 42] & 255) << 8);
            wData.blockEndIndex = (data[startPos + 43] & 255) + ((data[startPos + 44] & 255) << 8);
        }
    }

    public static void doCommonMsgResponseResult(byte[] raw, MioDeviceCallBack mioDeviceCallBack) {
        byte msgCode;
        byte flags;
        if (raw[2] == MioHelper.MSG_USER_SETTING_GET) {
            msgCode = raw[3];
            UserInfo userInfo = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                flags = raw[4];
                userInfo = new UserInfo();
                userInfo.genderType = (byte) (flags & 1);
                userInfo.unitType = (byte) ((flags & 2) >> 1);
                userInfo.HMRDisplayType = (byte) ((flags & 4) >> 2);
                userInfo.displayOrien = (byte) ((flags & 8) >> 3);
                userInfo.woDispMode = (byte) ((flags & 16) >> 4);
                userInfo.ADLCalorieGoalOpt = (byte) ((flags & 32) >> RUN_MIN_RUNNING_PACE_KM);
                userInfo.WORecording = (byte) ((flags & 64) >> 6);
                userInfo.MHRAutoAdj = (byte) ((flags & TransportMediator.FLAG_KEY_MEDIA_NEXT) >> 7);
                userInfo.birthDay = raw[RUN_MIN_RUNNING_PACE_KM];
                userInfo.birthMonth = raw[6];
                userInfo.birthYear = (short) (raw[7] & 255);
                userInfo.bodyWeight = (short) (raw[8] & 255);
                userInfo.bodyHeight = (short) (raw[9] & 255);
                userInfo.resetHR = (short) (raw[10] & 255);
                userInfo.maxHR = (short) (raw[11] & 255);
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetUserInfo(userInfo, msgCode);
            }
        } else if (raw[2] == MioHelper.MSG_DEVICE_NAME_GET) {
            String deviceName = null;
            msgCode = raw[3];
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                byte len = (byte) (raw[0] - 2);
                if (raw.length >= len + 2) {
                    deviceName = DiffResult.OBJECTS_SAME_STRING;
                    for (byte i = (byte) 0; i < len; i++) {
                        if (raw[i + 4] > 0) {
                            deviceName = new StringBuilder(String.valueOf(deviceName)).append((char) raw[i + 4]).toString();
                        }
                    }
                }
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetDeviceName(deviceName, msgCode);
            }
        } else if (raw[2] == MioHelper.MSG_RTC_GET || raw[2] == MioHelper.MSG_RTCTIME_GET) {
            msgCode = raw[3];
            RTCSetting rtcSetting = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                rtcSetting = new RTCSetting();
                if (raw[2] == MioHelper.MSG_RTC_GET) {
                    flags = raw[4];
                    if ((flags & 1) == 1) {
                        rtcSetting.timeFormat = TIMEFORMAT.TIMEFORMAT_24H;
                    }
                    if ((flags & 2) == 2) {
                        rtcSetting.dateFormat = DATEFORMAT.DATEFORMAT_DDMM;
                    }
                    rtcSetting.timeData = new TimeData();
                    rtcSetting.timeData.second = raw[RUN_MIN_RUNNING_PACE_KM];
                    rtcSetting.timeData.minute = raw[6];
                    rtcSetting.timeData.hour = raw[7];
                    rtcSetting.timeData.day = raw[8];
                    rtcSetting.timeData.month = raw[9];
                    rtcSetting.timeData.year = (short) (raw[10] & 255);
                } else {
                    rtcSetting.timeData = new TimeData();
                    rtcSetting.timeData.second = raw[4];
                    rtcSetting.timeData.minute = raw[RUN_MIN_RUNNING_PACE_KM];
                    rtcSetting.timeData.hour = raw[6];
                }
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetRTCTime(rtcSetting, msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_EXER_SETTINGS_GET) {
            msgCode = raw[3];
            ExerciseSetting exerciseSetting = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR && raw.length >= 9) {
                boolean z;
                exerciseSetting = new ExerciseSetting();
                if ((raw[4] & 1) == 1) {
                    z = false;
                } else {
                    z = true;
                }
                exerciseSetting.recoveryTimeIntoTotalTime = z;
                if ((raw[4] & 2) == 2) {
                    z = true;
                } else {
                    z = false;
                }
                exerciseSetting.repeatFlag = z;
                exerciseSetting.timeHour = raw[RUN_MIN_RUNNING_PACE_KM];
                exerciseSetting.timeMinute = raw[6];
                exerciseSetting.recoveryTimeMinute = raw[7];
                exerciseSetting.recoveryTimeSecond = raw[8];
                if (raw[RUN_MIN_RUNNING_PACE_KM] == 0 && raw[6] == 0) {
                    z = true;
                } else {
                    z = false;
                }
                exerciseSetting.is_up_timer = z;
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetExerciseSetting(exerciseSetting, msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_USER_SCREEN_GET) {
            msgCode = raw[3];
            UserScreenData usd = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR && raw.length >= 14) {
                usd = new UserScreenData();
                usd.us1_1 = getTypeUS1(raw[4]);
                usd.us1_2 = getTypeUS1(raw[RUN_MIN_RUNNING_PACE_KM]);
                usd.us1_3 = getTypeUS1(raw[6]);
                usd.us1_4 = getTypeUS1(raw[7]);
                usd.us2_1 = getTypeUS2(raw[8]);
                usd.us2_2 = getTypeUS2(raw[9]);
                usd.us2_3 = getTypeUS2(raw[10]);
                usd.us2_4 = getTypeUS2(raw[11]);
                usd.us2_5 = getTypeUS2(raw[12]);
                usd.us2_6 = getTypeUS2(raw[13]);
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetUserScreen(usd, msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_STRIDE_CALI_GET) {
            msgCode = raw[3];
            StridCaliData usd2 = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                usd2 = new StridCaliData();
                usd2.strideCaliMode = (raw[4] & 1) == 1;
                usd2.caliWalkFactor = raw[RUN_MIN_RUNNING_PACE_KM] & 255;
                usd2.caliRunFactor = raw[6] & 255;
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetStrideCali(msgCode, usd2);
            }
        } else if (raw[2] == MioHelper.LINK_CUST_DEVICE_OPTION_GET) {
            msgCode = raw[3];
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.DidGetDeviceOption_MIO(msgCode, raw[4]);
            }
        } else if (raw[2] == MioHelper.MSG_USER_SETTING_SET || raw[2] == MioHelper.MSG_DEVICE_NAME_SET || raw[2] == MioHelper.MSG_RTC_SET || raw[2] == MioHelper.MSG_RTCTIME_SET || raw[2] == MioHelper.MSG_SENSOR_DATA || raw[2] == MioHelper.LINK_EXER_SETTINGS_SET || raw[2] == MioHelper.LINK_USER_SCREEN_SET || raw[2] == MioHelper.LINK_STRIDE_CALI_SET || raw[2] == MioHelper.LINK_CUST_DEVICE_OPTION_SET) {
            msgCode = raw[3];
            if (mioDeviceCallBack == null) {
                return;
            }
            if (raw[2] == MioHelper.MSG_USER_SETTING_SET) {
                mioDeviceCallBack.onSetUserInfo(msgCode);
            } else if (raw[2] == MioHelper.MSG_DEVICE_NAME_SET) {
                mioDeviceCallBack.onSetDeviceName(msgCode);
            } else if (raw[2] == MioHelper.MSG_RTC_SET || raw[2] == MioHelper.MSG_RTCTIME_SET) {
                mioDeviceCallBack.onSetRTCTime(msgCode);
            } else if (raw[2] == MioHelper.MSG_SENSOR_DATA) {
                mioDeviceCallBack.onSendGpsData(msgCode);
            } else if (raw[2] == MioHelper.LINK_EXER_SETTINGS_SET) {
                mioDeviceCallBack.onSetExerciseSetting(msgCode);
            } else if (raw[2] == MioHelper.LINK_USER_SCREEN_SET) {
                mioDeviceCallBack.onSetUserScreen(msgCode);
            } else if (raw[2] == MioHelper.LINK_STRIDE_CALI_SET) {
                mioDeviceCallBack.onSetStrideCali(msgCode);
            } else if (raw[2] == MioHelper.LINK_CUST_DEVICE_OPTION_SET) {
                mioDeviceCallBack.DidSetDeviceOption_MIO(msgCode);
            }
        }
    }

    public static ExerciseTimerSyncData parserTimerSyncDataMsgResponseResult(byte[] raw) {
        boolean z = true;
        ExerciseTimerSyncData timerData;
        boolean z2;
        int index;
        if (raw[0] == MioHelper.TIMER_SYNC_DATA) {
            timerData = new ExerciseTimerSyncData();
            byte cflags = raw[1];
            if ((raw[1] & 1) == 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.lapDataFlag = z2;
            if ((raw[1] & 2) == 2) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.timerStatusPresent = z2;
            if ((raw[1] & 4) == 4) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.lapNumPresent = z2;
            if ((raw[1] & 8) == 8) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.splitTimePresent = z2;
            if ((raw[1] & 16) == 16) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.lapTimePresent = z2;
            if ((raw[1] & 32) == 32) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.stepDataPresent = z2;
            if ((raw[1] & 64) == 64) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.distDataPresent = z2;
            if ((raw[1] & TransportMediator.FLAG_KEY_MEDIA_NEXT) == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.calorieDataPresent = z2;
            if ((raw[2] & 1) == 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.speedDataPresent = z2;
            if ((raw[3] & 1) == 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.running = z2;
            if ((raw[3] & 2) == 2) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.reset = z2;
            if ((raw[3] & 4) == 4) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.runout = z2;
            if ((raw[3] & 8) != 8) {
                z = false;
            }
            timerData.lapTaken = z;
            index = 3;
            if (timerData.lapNumPresent) {
                timerData.lapNum = raw[4] & 255;
                index = 3 + 1;
            }
            if (timerData.splitTimePresent) {
                timerData.splitTime = new ExerciseTimeData();
                timerData.splitTime.second = (byte) (raw[index + 1] & 255);
                timerData.splitTime.minute = (byte) (raw[index + 2] & 255);
                timerData.splitTime.hour = (byte) (raw[index + 3] & 255);
                index += 3;
            }
            if (timerData.lapTimePresent) {
                timerData.lapTime = new ExerciseTimeData();
                timerData.lapTime.second = (byte) (raw[index + 1] & 255);
                timerData.lapTime.minute = (byte) (raw[index + 2] & 255);
                timerData.lapTime.hour = (byte) (raw[index + 3] & 255);
                index += 3;
            }
            if (timerData.stepDataPresent) {
                timerData.stepData = ((raw[index + 1] & 255) + ((raw[index + 2] & 255) << 8)) + ((raw[index + 3] & 255) << 16);
                index += 3;
            }
            if (timerData.distDataPresent) {
                timerData.distData = ((raw[index + 1] & 255) + ((raw[index + 2] & 255) << 8)) + ((raw[index + 3] & 255) << 16);
                index += 3;
            }
            if (timerData.calorieDataPresent) {
                timerData.calorieData = (raw[index + 1] & 255) + ((raw[index + 2] & 255) << 8);
                index += 2;
            }
            if (!timerData.speedDataPresent) {
                return timerData;
            }
            timerData.speedData = (raw[index + 1] & 255) + ((raw[index + 2] & 255) << 8);
            index += 2;
            return timerData;
        } else if (raw[0] != MioHelper.STEP_DATA) {
            return null;
        } else {
            timerData = new ExerciseTimerSyncData();
            if ((raw[1] & 1) == 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            timerData.stepDataPresent = z2;
            if ((raw[1] & 2) != 2) {
                z = false;
            }
            timerData.rhrDataPresent = z;
            index = 2;
            if (timerData.stepDataPresent) {
                timerData.stepData = (raw[2] & 255) + ((raw[3] & 255) << 8);
                index = 2 + 2;
            }
            if (!timerData.rhrDataPresent) {
                return timerData;
            }
            timerData.rhrData = raw[index] & 255;
            return timerData;
        }
    }

    public static void doFuseMsgResponseResult(byte[] raw, MioDeviceCallBack mioDeviceCallBack, String deviceName) {
        byte msgCode;
        if (raw[2] == MioHelper.LINK_DISP_GET) {
            msgCode = raw[3];
            FuseDisplay fuseDisplay = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                boolean z;
                fuseDisplay = new FuseDisplay();
                fuseDisplay.adlDisplay = new ADLDisPlay();
                fuseDisplay.woDisplay = new WODisplay();
                byte adlDisp = raw[4];
                byte woDisp = raw[RUN_MIN_RUNNING_PACE_KM];
                ADLDisPlay aDLDisPlay = fuseDisplay.adlDisplay;
                if ((adlDisp & 1) == 1) {
                    z = true;
                } else {
                    z = false;
                }
                aDLDisPlay.dataCalorieEnable = z;
                aDLDisPlay = fuseDisplay.adlDisplay;
                if ((adlDisp & 2) == 2) {
                    z = true;
                } else {
                    z = false;
                }
                aDLDisPlay.dataStepEnable = z;
                aDLDisPlay = fuseDisplay.adlDisplay;
                if ((adlDisp & 4) == 4) {
                    z = true;
                } else {
                    z = false;
                }
                aDLDisPlay.dataDistanceEnable = z;
                aDLDisPlay = fuseDisplay.adlDisplay;
                if ((adlDisp & 8) == 8) {
                    z = true;
                } else {
                    z = false;
                }
                aDLDisPlay.dataGoalEnable = z;
                WODisplay wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 1) == 1) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataCalorieEnable = z;
                wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 2) == 2) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataStepEnable = z;
                wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 4) == 4) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataDistanceEnable = z;
                wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 8) == 8) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataPaceEnable = z;
                wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 16) == 16) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataSpeedEnable = z;
                wODisplay = fuseDisplay.woDisplay;
                if ((woDisp & 32) == 32) {
                    z = true;
                } else {
                    z = false;
                }
                wODisplay.dataTimeEnable = z;
                if (deviceName.endsWith("FUSE")) {
                    fuseDisplay.adlKeyLockTime = raw[6];
                }
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetDisplay(fuseDisplay, msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_DAILY_GOAL_GET) {
            msgCode = raw[3];
            GoalSetting goalSetting = null;
            if (msgCode == MioHelper.MSG_RESPONSE_CODE_NO_ERROR && raw.length >= 15) {
                goalSetting = new GoalSetting();
                goalSetting.dailyGoal = new GoalStruct();
                goalSetting.todayGoal = new GoalStruct();
                goalSetting.dailyGoal.goalType = raw[8];
                goalSetting.dailyGoal.goalValue = (((raw[4] & 255) + ((raw[RUN_MIN_RUNNING_PACE_KM] & 255) << 8)) + ((raw[6] & 255) << 16)) + ((raw[7] & 255) << 24);
                goalSetting.todayGoal.goalType = raw[13];
                goalSetting.todayGoal.goalValue = (((raw[9] & 255) + ((raw[10] & 255) << 8)) + ((raw[11] & 255) << 16)) + ((raw[12] & 255) << 24);
                goalSetting.todayGoalProgress = raw[14];
            }
            if (mioDeviceCallBack != null) {
                mioDeviceCallBack.onGetDailyGoal(goalSetting, msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_DISP_SET || raw[2] == MioHelper.LINK_DISP_SET2 || raw[2] == MioHelper.LINK_DAILY_GOAL_SET) {
            msgCode = raw[3];
            if (mioDeviceCallBack == null) {
                return;
            }
            if (raw[2] == MioHelper.LINK_DISP_SET || raw[2] == MioHelper.LINK_DISP_SET2) {
                mioDeviceCallBack.onSetDisplay(msgCode);
            } else if (raw[2] == MioHelper.LINK_DAILY_GOAL_SET) {
                mioDeviceCallBack.onSetDailyGoal(msgCode);
            }
        } else if (raw[2] == MioHelper.LINK_MISC1_GET || raw[2] == MioHelper.LINK_MISC1_SET) {
            msgCode = raw[3];
            if (mioDeviceCallBack == null) {
                return;
            }
            if (raw[2] == MioHelper.LINK_MISC1_GET) {
                if (msgCode != MioHelper.MSG_RESPONSE_CODE_NO_ERROR) {
                    mioDeviceCallBack.DidGetMisc1_MIO(null, msgCode);
                    return;
                }
                MIOMisc1Data sad = new MIOMisc1Data();
                byte flags = raw[4];
                sad.SwingArmDetection = (byte) (flags & 1);
                sad.SwingArmSelect_ADL = (byte) ((flags & 2) >> 1);
                sad.SwingArmSelect_WO = (byte) ((flags & 4) >> 2);
                sad.RHRMeasCtrl_SLP = (byte) ((flags & 8) >> 3);
                sad.RHRMeasCtrl_ADL_WO = (byte) ((flags & 16) >> 4);
                sad.MobileNotification = (byte) ((flags & 32) >> RUN_MIN_RUNNING_PACE_KM);
                sad.SAItemType_ADL = raw[6];
                sad.SAItemType_WO = raw[7];
                sad.RHRMeasCtrl_SLP_NUM = raw[8];
                sad.RHRMeasCtrl_ADL_WO_NUM = raw[9];
                mioDeviceCallBack.DidGetMisc1_MIO(sad, msgCode);
            } else if (raw[2] == MioHelper.LINK_MISC1_SET) {
                mioDeviceCallBack.DidSetMisc1_MIO(msgCode);
            }
        }
    }

    public static void writeLogtoFile(String mylogtype, String tag, String text) {
        if (isDebug) {
            Date nowtime = new Date();
            String needWriteFiel = logfile.format(nowtime);
            String needWriteMessage = new StringBuilder(String.valueOf(myLogSdf.format(nowtime))).append("    ").append(mylogtype).append("    ").append(tag).append("    ").append(text).toString();
            String name = "mio_sdk_log.txt";
            if (mylogtype.contains("App")) {
                name = "mio_sdk_log_app_connect.txt";
            } else if (mylogtype.contains("LeScan")) {
                name = "mio_sdk_log_LeScan.txt";
            }
            try {
                FileWriter filerWriter = new FileWriter(new File(SDCARD_PATH, new StringBuilder(String.valueOf(needWriteFiel)).append(name).toString()), true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void checkUserInfo(UserInfo userInfo) {
        if (userInfo.birthDay < (byte) 1) {
            userInfo.birthDay = (byte) 1;
        }
        if (userInfo.birthDay > (byte) 31) {
            userInfo.birthDay = (byte) 31;
        }
        if (userInfo.birthMonth < (byte) 1) {
            userInfo.birthMonth = (byte) 1;
        }
        if (userInfo.birthMonth > (byte) 12) {
            userInfo.birthMonth = (byte) 12;
        }
        if (userInfo.birthYear > (short) 1900) {
            userInfo.birthYear = (short) (userInfo.birthYear - 1900);
        }
        if (userInfo.birthYear < (short) 0) {
            userInfo.birthYear = (short) 0;
        }
        if (userInfo.birthYear > (short) 199) {
            userInfo.birthYear = (short) 199;
        }
        if (userInfo.bodyWeight < (short) 20) {
            userInfo.bodyWeight = (short) 20;
        }
        if (userInfo.bodyWeight > (short) 200) {
            userInfo.bodyWeight = (short) 200;
        }
        if (userInfo.bodyHeight < (short) 69) {
            userInfo.bodyHeight = (short) 69;
        }
        if (userInfo.bodyHeight > (short) 231) {
            userInfo.bodyHeight = (short) 231;
        }
        if (userInfo.resetHR < (short) 30) {
            userInfo.resetHR = (short) 30;
        }
        if (userInfo.resetHR > (short) 140) {
            userInfo.resetHR = (short) 140;
        }
        if (userInfo.maxHR < (short) 80) {
            userInfo.maxHR = (short) 80;
        }
        if (userInfo.maxHR > (short) 220) {
            userInfo.maxHR = (short) 220;
        }
        if (userInfo.resetHR >= userInfo.maxHR) {
            userInfo.resetHR = (short) (userInfo.maxHR - 1);
        }
    }

    public static void checkTimeData(TimeData timeData, TIMEFORMAT timeFormat) {
        if (timeData.second < 0) {
            timeData.second = (byte) 0;
        }
        if (timeData.second > (byte) 59) {
            timeData.second = (byte) 59;
        }
        if (timeData.minute < 0) {
            timeData.minute = (byte) 0;
        }
        if (timeData.minute > (byte) 59) {
            timeData.minute = (byte) 59;
        }
        if (timeData.hour < 0) {
            timeData.hour = (byte) 0;
        }
        if (timeData.hour > (byte) 23) {
            timeData.hour = (byte) 23;
        }
        if (timeData.day < (byte) 1) {
            timeData.day = (byte) 1;
        }
        if (timeData.day > 31) {
            timeData.day = (byte) 31;
        }
        if (timeData.month < (byte) 1) {
            timeData.month = (byte) 1;
        }
        if (timeData.month > (byte) 12) {
            timeData.month = (byte) 12;
        }
        if (timeData.year > (short) 1900) {
            timeData.year = (short) (timeData.year - 1900);
        }
        if (timeData.year < (short) 114) {
            timeData.year = (short) 114;
        }
        if (timeData.year > (short) 199) {
            timeData.year = (short) 199;
        }
    }

    public static void checkTimeData(TimeData timeData) {
        if (timeData.second < 0) {
            timeData.second = (byte) 0;
        }
        if (timeData.second > (byte) 59) {
            timeData.second = (byte) 59;
        }
        if (timeData.minute < 0) {
            timeData.minute = (byte) 0;
        }
        if (timeData.minute > (byte) 59) {
            timeData.minute = (byte) 59;
        }
        if (timeData.hour < 0) {
            timeData.hour = (byte) 0;
        }
        if (timeData.hour > (byte) 23) {
            timeData.hour = (byte) 23;
        }
    }

    public static void checkGoalData(GoalData goalData) {
        if (goalData.flags < (byte) 1 || goalData.flags > (byte) 2) {
            goalData.flags = (byte) 1;
        }
        if (goalData.goalData.goalType < 0) {
            goalData.goalData.goalType = (byte) 0;
        }
        if (goalData.goalData.goalType > (byte) 3) {
            goalData.goalData.goalType = (byte) 3;
        }
        if (goalData.goalData.goalType == (byte) 1) {
            if (goalData.goalData.goalValue < 100) {
                goalData.goalData.goalValue = 100;
            }
            if (goalData.goalData.goalValue > 99999) {
                goalData.goalData.goalValue = 99999;
            }
        } else if (goalData.goalData.goalType == (byte) 2) {
            if (goalData.goalData.goalValue < 100) {
                goalData.goalData.goalValue = 100;
            }
            if (goalData.goalData.goalValue > 99999) {
                goalData.goalData.goalValue = 99999;
            }
        } else if (goalData.goalData.goalType == (byte) 3) {
            if (goalData.goalData.goalValue < 100) {
                goalData.goalData.goalValue = 100;
            }
            if (goalData.goalData.goalValue > 9999) {
                goalData.goalData.goalValue = 9999;
            }
        }
    }

    public static boolean checkHRZoneSetting(MioDeviceHRZoneSetting settings) {
        if (settings == null) {
            return false;
        }
        int lowLimit;
        if (settings.GetHRZoneType_MIO() == 1) {
            int limit1 = settings.GetHR5ZoneLimit0_MIO();
            int limit2 = settings.GetHR5ZoneLimit1_MIO();
            int limit3 = settings.GetHR5ZoneLimit2_MIO();
            int limit4 = settings.GetHR5ZoneLimit3_MIO();
            int limit5 = settings.GetHR5ZoneLimit4_MIO();
            lowLimit = settings.GetHR3ZoneLowLimit_MIO();
            int upperLimit = settings.GetHR3ZoneUpperLimit_MIO();
            if (lowLimit > 215 || lowLimit < 30 || upperLimit > 220 || upperLimit < 35 || upperLimit < lowLimit + RUN_MIN_RUNNING_PACE_KM || limit1 > 220 || limit1 < 30 || limit2 > 220 || limit2 < 30 || limit3 > 220 || limit3 < 30 || limit4 > 220 || limit4 < 30 || limit5 > 220 || limit5 < 30 || limit2 <= limit1 || limit3 <= limit2 || limit4 <= limit3 || limit5 <= limit4) {
                return false;
            }
        }
        int limit1 = settings.GetHR5ZoneLimit0_MIO();
        int limit2 = settings.GetHR5ZoneLimit1_MIO();
        int limit3 = settings.GetHR5ZoneLimit2_MIO();
        int limit4 = settings.GetHR5ZoneLimit3_MIO();
        int limit5 = settings.GetHR5ZoneLimit4_MIO();
        lowLimit = settings.GetHR3ZoneLowLimit_MIO();
        int upperLimit = settings.GetHR3ZoneUpperLimit_MIO();
        if (limit1 > 220 || limit1 < 30 || limit2 > 220 || limit2 < 30 || limit3 > 220 || limit3 < 30 || limit4 > 220 || limit4 < 30 || limit5 > 220 || limit5 < 30 || limit2 <= limit1 || limit3 <= limit2 || limit4 <= limit3 || limit5 <= limit4 || lowLimit > 215 || lowLimit < 30 || upperLimit > 220 || upperLimit < 35) {
            return false;
        }
        if (upperLimit < lowLimit + RUN_MIN_RUNNING_PACE_KM) {
            return false;
        }
        return true;
    }

    public static UserScreen1Type getTypeUS1(byte code) {
        UserScreen1Type type = UserScreen1Type.SCREEN1_EMPTY;
        if (code == 1) {
            return UserScreen1Type.CLOCK;
        }
        if (code == 2) {
            return UserScreen1Type.RUNNING_TOTAL_STEPS;
        }
        if (code == 3) {
            return UserScreen1Type.RUNNING_PACE;
        }
        if (code == 4) {
            return UserScreen1Type.RUNNING_TOTAL_KCAL;
        }
        if (code == RUN_MIN_RUNNING_PACE_KM) {
            return UserScreen1Type.RUNNING_TOTAL_DISTANCE;
        }
        if (code == 6) {
            return UserScreen1Type.RUNNING_SPEED;
        }
        if (code == 7) {
            return UserScreen1Type.LAP_STEPS;
        }
        if (code == 8) {
            return UserScreen1Type.LAP_KCAL;
        }
        if (code == 9) {
            return UserScreen1Type.LAP_DISTANCE;
        }
        if (code == 10) {
            return UserScreen1Type.LAP_TIME;
        }
        if (code == 11) {
            return UserScreen1Type.TOTAL_LAPS;
        }
        return type;
    }

    public static UserScreen2Type getTypeUS2(byte code) {
        UserScreen2Type type = UserScreen2Type.SCREEN2_EMPTY;
        if (code == 1) {
            return UserScreen2Type.AVERAGE_HR;
        }
        if (code == 2) {
            return UserScreen2Type.AVERAGE_PACE;
        }
        if (code == 3) {
            return UserScreen2Type.AVERAGE_SPEED;
        }
        if (code == 4) {
            return UserScreen2Type.TOTAL_TIME;
        }
        if (code == RUN_MIN_RUNNING_PACE_KM) {
            return UserScreen2Type.TOTAL_STEPS;
        }
        if (code == 6) {
            return UserScreen2Type.TOTAL_KCAL;
        }
        if (code == 7) {
            return UserScreen2Type.TOTAL_DISTANCE;
        }
        if (code == 8) {
            return UserScreen2Type.TOTAL_LAP_CYCLE;
        }
        if (code == 9) {
            return UserScreen2Type.BEST_LAP_TIME;
        }
        if (code == 10) {
            return UserScreen2Type.BEST_PACE;
        }
        if (code == 11) {
            return UserScreen2Type.BEST_SPEED;
        }
        if (code == 12) {
            return UserScreen2Type.IN_ZONE_TIME;
        }
        return type;
    }

    public static byte getCodeUS1(UserScreen1Type us1) {
        if (us1 == UserScreen1Type.CLOCK) {
            return (byte) 1;
        }
        if (us1 == UserScreen1Type.RUNNING_TOTAL_STEPS) {
            return (byte) 2;
        }
        if (us1 == UserScreen1Type.RUNNING_PACE) {
            return (byte) 3;
        }
        if (us1 == UserScreen1Type.RUNNING_TOTAL_KCAL) {
            return (byte) 4;
        }
        if (us1 == UserScreen1Type.RUNNING_TOTAL_DISTANCE) {
            return (byte) 5;
        }
        if (us1 == UserScreen1Type.RUNNING_SPEED) {
            return (byte) 6;
        }
        if (us1 == UserScreen1Type.LAP_STEPS) {
            return (byte) 7;
        }
        if (us1 == UserScreen1Type.LAP_KCAL) {
            return (byte) 8;
        }
        if (us1 == UserScreen1Type.LAP_DISTANCE) {
            return (byte) 9;
        }
        if (us1 == UserScreen1Type.LAP_TIME) {
            return (byte) 10;
        }
        if (us1 == UserScreen1Type.TOTAL_LAPS) {
            return (byte) 11;
        }
        return (byte) 0;
    }

    public static byte getCodeUS2(UserScreen2Type us2) {
        if (us2 == UserScreen2Type.AVERAGE_HR) {
            return (byte) 1;
        }
        if (us2 == UserScreen2Type.AVERAGE_PACE) {
            return (byte) 2;
        }
        if (us2 == UserScreen2Type.AVERAGE_SPEED) {
            return (byte) 3;
        }
        if (us2 == UserScreen2Type.TOTAL_TIME) {
            return (byte) 4;
        }
        if (us2 == UserScreen2Type.TOTAL_STEPS) {
            return (byte) 5;
        }
        if (us2 == UserScreen2Type.TOTAL_KCAL) {
            return (byte) 6;
        }
        if (us2 == UserScreen2Type.TOTAL_DISTANCE) {
            return (byte) 7;
        }
        if (us2 == UserScreen2Type.TOTAL_LAP_CYCLE) {
            return (byte) 8;
        }
        if (us2 == UserScreen2Type.BEST_LAP_TIME) {
            return (byte) 9;
        }
        if (us2 == UserScreen2Type.BEST_PACE) {
            return (byte) 10;
        }
        if (us2 == UserScreen2Type.BEST_SPEED) {
            return (byte) 11;
        }
        if (us2 == UserScreen2Type.IN_ZONE_TIME) {
            return (byte) 12;
        }
        return (byte) 0;
    }
}
