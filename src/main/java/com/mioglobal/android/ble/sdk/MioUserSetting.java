package com.mioglobal.android.ble.sdk;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.DiffResult;

public class MioUserSetting {

    public static class ADLDailyData {
        public int actTimeOfRun;
        public int actTimeOfWalk;
        public short calorie;
        public byte dailyGoalType;
        public int dailyGoalValue;
        public byte day;
        public int dist;
        public byte month;
        public int rmr;
        public int step;
        public short year;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "year=" + (this.year + 1900) + "\r\nmonth=" + this.month + "\r\nday=" + this.day + "\r\nrmr=" + this.rmr + "\r\nstep=" + this.step + "\r\ndist=" + this.dist + "\r\ncalorie=" + this.calorie + "\r\nactTimeOfWalk=" + this.actTimeOfWalk + "\r\nactTimeOfRun=" + this.actTimeOfRun + "\r\ndailyGoalValue=" + this.dailyGoalValue + "\r\ndailyGoalType=" + this.dailyGoalType;
        }
    }

    public static class ADLDisPlay {
        public boolean dataCalorieEnable;
        public boolean dataDistanceEnable;
        public boolean dataGoalEnable;
        public boolean dataStepEnable;

        public ADLDisPlay() {
            this.dataCalorieEnable = false;
            this.dataStepEnable = false;
            this.dataDistanceEnable = false;
            this.dataGoalEnable = false;
        }

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "dataCalorieEnable=" + this.dataCalorieEnable + " dataStepEnable=" + this.dataStepEnable + " dataDistanceEnable=" + this.dataDistanceEnable + " dataGoalEnable=" + this.dataGoalEnable;
        }
    }

    public static class ADLTodayData {
        public int actTimeOfRun;
        public int actTimeOfWalk;
        public short calorie;
        public int dist;
        public int rmr;
        public int step;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "step=" + this.step + "\r\ndist=" + this.dist + "\r\ncalorie=" + this.calorie + "\r\nrmr=" + this.rmr + "\r\nactTimeOfWalk=" + this.actTimeOfWalk + "\r\nactTimeOfRun=" + this.actTimeOfRun;
        }
    }

    public enum DATEFORMAT {
        DATEFORMAT_MMDD,
        DATEFORMAT_DDMM
    }

    public enum DelOPType {
        DELETE_OLDEST_RECORD,
        DELETE_ALL_RECORD
    }

    public static class ExerciseOrLapSummary {
        public short averageHR;
        public PaceTime averagePace;
        public float averageSpeed;
        public int blockEndIndex;
        public int blockStartIndex;
        public short calorie;
        public int dist;
        public short lapIndex;
        public short maxHR;
        public PaceTime maxPace;
        public float maxSpeed;
        public short minHR;
        public byte recordType;
        public TimeData startTime;
        public int step;
        public int timeInUpLowZone;
        public int timeInZone1;
        public int timeInZone2;
        public int timeInZone3;
        public int timeInZone4;
        public int timeInZone5;
        public short totalLapAmount;
        public ExerciseTimeData workoutTime;
        public byte workoutType;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            if (this.recordType == 0) {
                str = "\r\nExerciseSummary [\r\ntotalLapAmount=" + this.totalLapAmount;
            } else {
                str = "\r\nLapSummary [\r\nlapIndex=" + this.lapIndex;
            }
            return new StringBuilder(String.valueOf(str)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(this.startTime.toString()).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).append(this.workoutTime.toString()).append(" workoutType=").append(this.workoutType).append(" recordType=").append(this.recordType).append(" step=").append(this.step).append(" dist=").append(this.dist).append(" calorie=").append(this.calorie).append(" averageHR=").append(this.averageHR).append(" maxHR=").append(this.maxHR).append(" minHR=").append(this.minHR).append(" averageSpeed=").append(this.averageSpeed).append(" maxSpeed=").append(this.maxSpeed).append(" averagePace=").append(this.averagePace.toString()).append(" maxPace=").append(this.maxPace.toString()).append(" timeInUpLowZone=").append(this.timeInUpLowZone).append(" timeInZone1=").append(this.timeInZone1).append(" timeInZone2=").append(this.timeInZone2).append(" timeInZone3=").append(this.timeInZone3).append(" timeInZone4=").append(this.timeInZone4).append(" timeInZone5=").append(this.timeInZone5).append("]").toString();
        }
    }

    public static class ExerciseRecord {
        public ArrayList<ExerciseOrLapSummary> LapSummaryArray;
        public ExerciseOrLapSummary exerciseSummary;
        public RecordLogData logData;

        public String toString() {
            String str = "\r\nExerciseRecord [\r\n";
            if (this.exerciseSummary != null) {
                str = new StringBuilder(String.valueOf(str)).append(this.exerciseSummary.toString()).toString();
            }
            if (this.LapSummaryArray != null) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n LapSummaryData:").toString();
                for (int i = 0; i < this.LapSummaryArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(((ExerciseOrLapSummary) this.LapSummaryArray.get(i)).toString()).toString();
                }
            }
            if (this.logData != null) {
                str = new StringBuilder(String.valueOf(str)).append(this.logData.toString()).toString();
            }
            return new StringBuilder(String.valueOf(str)).append("]").toString();
        }
    }

    public static class ExerciseSetting {
        public boolean is_up_timer;
        public boolean recoveryTimeIntoTotalTime;
        public byte recoveryTimeMinute;
        public byte recoveryTimeSecond;
        public boolean repeatFlag;
        public byte timeHour;
        public byte timeMinute;

        public ExerciseSetting() {
            this.recoveryTimeIntoTotalTime = false;
            this.repeatFlag = false;
            this.is_up_timer = false;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "ExerciseSettingData: recoveryTimeIntoTotalTime=" + this.recoveryTimeIntoTotalTime + " repeatFlag=" + this.repeatFlag + " timeHour=" + this.timeHour + " timeMinute=" + this.timeMinute + " recoveryTimeMinute=" + this.recoveryTimeMinute + " recoveryTimeSecond=" + this.recoveryTimeSecond + " is_up_timer=" + this.is_up_timer;
        }
    }

    public static class ExerciseTimeData {
        public byte hour;
        public byte minute;
        public byte second;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + this.hour + ":" + this.minute + ":" + this.second;
        }
    }

    public static class ExerciseTimerSyncData {
        public int calorieData;
        public boolean calorieDataPresent;
        public int distData;
        public boolean distDataPresent;
        public boolean lapDataFlag;
        public int lapNum;
        public boolean lapNumPresent;
        public boolean lapTaken;
        public ExerciseTimeData lapTime;
        public boolean lapTimePresent;
        public String rawLog;
        public boolean reset;
        public int rhrData;
        public boolean rhrDataPresent;
        public boolean running;
        public boolean runout;
        public int speedData;
        public boolean speedDataPresent;
        public ExerciseTimeData splitTime;
        public boolean splitTimePresent;
        public int stepData;
        public boolean stepDataPresent;
        public boolean timerStatusPresent;

        public ExerciseTimerSyncData() {
            this.rawLog = DiffResult.OBJECTS_SAME_STRING;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING + "ExerciseTimerSyncData:\r\nlapDataFlag=" + this.lapDataFlag + "\r\ntimerStatusPresent=" + this.timerStatusPresent + "\r\nlapNumPresent=" + this.lapNumPresent + " \r\nsplitTimePresent=" + this.splitTimePresent + "\r\nlapTimePresent=" + this.lapTimePresent + "\r\nstepDataPresent=" + this.stepDataPresent + "\r\nrhrDataPresent=" + this.rhrDataPresent + "\r\ndistDataPresent=" + this.distDataPresent + "\r\ncalorieDataPresent=" + this.calorieDataPresent + "\r\nspeedDataPresent=" + this.speedDataPresent + "\r\nrunning=" + this.running + "\r\nreset=" + this.reset + "\r\nrunout=" + this.runout + "\r\nlapTaken=" + this.lapTaken + " \r\nlapNum=" + this.lapNum + "\r\nstepData=" + this.stepData + "\r\nrhrData=" + this.rhrData + "\r\ndistData=" + this.distData + "\r\ncalorieData=" + this.calorieData + "\r\nspeedData=" + this.speedData;
            if (this.splitTime != null) {
                str = new StringBuilder(String.valueOf(str)).append("\r\nsplitTime=").append(this.splitTime.toString()).toString();
            }
            if (this.lapTime != null) {
                return new StringBuilder(String.valueOf(str)).append("\r\nlapTime=").append(this.lapTime.toString()).toString();
            }
            return str;
        }
    }

    public static class FuseDeviceStatus {
        public boolean ADLModeStatus;
        public boolean ExeTimerSyncStatus;
        public boolean GPSModeStatus;
        public boolean SleepModeStatus;
        public boolean StepDataNotificationStatus;
        public boolean WOModeStatus;
        public boolean actDataMemFull;
        public boolean fuseHasRecord;
        public boolean fuseHasWORecord;
        public boolean streamModeStatus;
        public boolean woLogMemFull;
        public boolean woLogMemLow;
        public boolean woRecordMemFull;

        public FuseDeviceStatus() {
            this.streamModeStatus = false;
            this.GPSModeStatus = false;
            this.StepDataNotificationStatus = false;
            this.ExeTimerSyncStatus = false;
            this.ADLModeStatus = false;
            this.WOModeStatus = false;
            this.SleepModeStatus = false;
            this.woRecordMemFull = false;
            this.woLogMemFull = false;
            this.woLogMemLow = false;
            this.fuseHasRecord = false;
            this.fuseHasWORecord = false;
            this.actDataMemFull = false;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "streamModeStatus=" + this.streamModeStatus + " \r\nGPSModeStatus=" + this.GPSModeStatus + " \r\nStepDataNotificationStatus=" + this.StepDataNotificationStatus + " \r\nExeTimerSyncStatus=" + this.ExeTimerSyncStatus + " \r\nADLModeStatus=" + this.ADLModeStatus + " \r\nWOModeStatus=" + this.WOModeStatus + " \r\nSleepModeStatus=" + this.SleepModeStatus + " \r\nwoRecordMemFull=" + this.woRecordMemFull + " \r\nwoLogMemFull=" + this.woLogMemFull + " \r\nwoLogMemLow=" + this.woLogMemLow + " \r\nfuseHasRecord=" + this.fuseHasRecord + " \r\nfuseHasWORecord=" + this.fuseHasWORecord + " \r\nactDataMemFull=" + this.actDataMemFull;
        }
    }

    public static class FuseDisplay {
        public ADLDisPlay adlDisplay;
        public int adlKeyLockTime;
        public WODisplay woDisplay;
        public int woKeyLockTime;

        public String toString() {
            return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(DiffResult.OBJECTS_SAME_STRING + this.adlDisplay.toString())).append(this.woDisplay.toString()).toString())).append("  adlKeyLockTime=").append(this.adlKeyLockTime).toString())).append("  woKeyLockTime=").append(this.woKeyLockTime).toString();
        }
    }

    public enum GPSDATAFLAG {
        GPSDATAFLAG_SERCHING,
        GPSDATAFLAG_2D,
        GPSDATAFLAG_3D
    }

    public static class GPSData {
        public GPSDATAFLAG gpsDataFlag;
        public int odometer;
        public short speed;
    }

    public static class GoalData {
        public byte flags;
        public GoalStruct goalData;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "flags=" + this.flags + this.goalData.toString();
        }
    }

    public static class GoalSetting {
        public GoalStruct dailyGoal;
        public GoalStruct todayGoal;
        public byte todayGoalProgress;

        public String toString() {
            return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(DiffResult.OBJECTS_SAME_STRING + "dailyGoal=" + this.dailyGoal.toString())).append(" todayGoal=").append(this.todayGoal.toString()).toString())).append(" todayGoalProgress=").append(this.todayGoalProgress).toString();
        }
    }

    public static class GoalStruct {
        public byte goalType;
        public int goalValue;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "goalType=" + this.goalType + " goalValue=" + this.goalValue;
        }
    }

    public static class MIOMisc1Data {
        public byte MobileNotification;
        public byte RHRMeasCtrl_ADL_WO;
        public byte RHRMeasCtrl_ADL_WO_NUM;
        public byte RHRMeasCtrl_SLP;
        public byte RHRMeasCtrl_SLP_NUM;
        public int SAItemType_ADL;
        public int SAItemType_WO;
        public byte SwingArmDetection;
        public byte SwingArmSelect_ADL;
        public byte SwingArmSelect_WO;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "SwingArmDetection=" + this.SwingArmDetection + "\r\nSwingArmSelect_ADL=" + this.SwingArmSelect_ADL + "\r\nSwingArmSelect_WO=" + this.SwingArmSelect_WO + "\r\nSAItemType_ADL=" + this.SAItemType_ADL + "\r\nSAItemType_WO=" + this.SAItemType_WO + "\r\nRHRMeasCtrl_SLP=" + this.RHRMeasCtrl_SLP + "\r\nRHRMeasCtrl_ADL_WO=" + this.RHRMeasCtrl_ADL_WO + "\r\nRHRMeasCtrl_SLP_NUM=" + this.RHRMeasCtrl_SLP_NUM + "\r\nRHRMeasCtrl_ADL_WO_NUM=" + this.RHRMeasCtrl_ADL_WO_NUM + "\r\nMobileNotification=" + this.MobileNotification;
        }
    }

    public static class OneHourSleepTracking {
        public List<OneMinuteSleepTracking> allRecords;
        public String[] log;
        public TimeData recordTime;

        public OneHourSleepTracking() {
            this.allRecords = new ArrayList();
            this.log = null;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            for (int i = 0; i < this.allRecords.size(); i++) {
                str = new StringBuilder(String.valueOf(str)).append(((OneMinuteSleepTracking) this.allRecords.get(i)).toString()).toString();
                if (i == 0) {
                    str = new StringBuilder(String.valueOf(str)).append("  ").append(this.recordTime.toExString()).toString();
                }
                str = new StringBuilder(String.valueOf(str)).append(IOUtils.LINE_SEPARATOR_WINDOWS).toString();
            }
            return str;
        }
    }

    public static class OneMinuteSleepTracking {
        public int ActivityIndex;
        public int CalorieData;
        public int HRData;
        public int HRLogData;
        public int StepData;
        public boolean blankMarker;
        public boolean calorieDataPresent;
        public boolean dateStampPresent;
        public boolean extFlagsPresent;
        public boolean hrDataPresent;
        public String[] log;
        public TimeData recordTime;
        public boolean sleepMode;
        public int sleepStatus;
        public boolean stepDataPresent;
        public boolean timeStampPresent;

        public OneMinuteSleepTracking() {
            this.log = null;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            if (this.sleepMode) {
                str = new StringBuilder(String.valueOf(str)).append("Sleep Mode: On, A_Index:").append(this.ActivityIndex).append(", ").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append("Sleep Mode: off, A_Index:").append(this.ActivityIndex).append(", ").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("Sleep_pattern:").append(this.sleepStatus).append(", ").toString();
            if (this.stepDataPresent) {
                str = new StringBuilder(String.valueOf(str)).append("Steps:").append(this.StepData).append(", ").toString();
            }
            if (this.hrDataPresent) {
                str = new StringBuilder(String.valueOf(str)).append("HR:").append(this.HRLogData).append(", ").toString();
            }
            if (this.calorieDataPresent) {
                str = new StringBuilder(String.valueOf(str)).append("KCAL:").append(this.CalorieData).append(", ").toString();
            }
            if (this.blankMarker) {
                str = new StringBuilder(String.valueOf(str)).append("BlankMarker:").append(this.blankMarker).append(", ").toString();
            }
            return new StringBuilder(String.valueOf(str)).toString();
        }
    }

    public static class PaceTime {
        public short minutes;
        public short seconds;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return this.minutes + "'" + this.seconds + "\"";
        }
    }

    public static class RTCSetting {
        public DATEFORMAT dateFormat;
        public TimeData timeData;
        public TIMEFORMAT timeFormat;

        public RTCSetting() {
            this.timeFormat = TIMEFORMAT.TIMEFORMAT_12H;
            this.dateFormat = DATEFORMAT.DATEFORMAT_MMDD;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING + "timeFormat=";
            if (this.timeFormat == TIMEFORMAT.TIMEFORMAT_12H) {
                str = new StringBuilder(String.valueOf(str)).append("12H").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append("24H").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append(" dateFormat=").toString();
            if (this.dateFormat == DATEFORMAT.DATEFORMAT_MMDD) {
                str = new StringBuilder(String.valueOf(str)).append("MMDD").toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append("DDMM").toString();
            }
            return new StringBuilder(String.valueOf(str)).append(this.timeData.toString()).toString();
        }
    }

    public enum RType {
        TYPE_WORKOUT_EXERCISE,
        TYPE_ADL_DAILY,
        TYPE_ADL_TODAY,
        TYPE_ACTIVITY
    }

    public static class RecordLogData {
        public ArrayList<Integer> calorieArray;
        public ArrayList<Integer> distArray;
        public ArrayList<Integer> hrArray;
        public ArrayList<Integer> paceArray;
        public ArrayList<Float> speedArray;
        public ArrayList<Integer> stepArray;

        public RecordLogData() {
            this.hrArray = new ArrayList();
            this.distArray = new ArrayList();
            this.calorieArray = new ArrayList();
            this.stepArray = new ArrayList();
            this.speedArray = new ArrayList();
            this.paceArray = new ArrayList();
        }

        public String toString() {
            int i;
            String str = "\r\nLogData[";
            if (this.hrArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n HR DATA:").toString();
                for (i = 0; i < this.hrArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.hrArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            if (this.distArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n DIST DATA:").toString();
                for (i = 0; i < this.distArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.distArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            if (this.calorieArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n CAL DATA:").toString();
                for (i = 0; i < this.calorieArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.calorieArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            if (this.stepArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n STEP DATA:").toString();
                for (i = 0; i < this.stepArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.stepArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            if (this.speedArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n SPEED DATA:").toString();
                for (i = 0; i < this.speedArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.speedArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            if (this.paceArray.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).append("\r\n PACE DATA:").toString();
                for (i = 0; i < this.paceArray.size(); i++) {
                    str = new StringBuilder(String.valueOf(str)).append(this.paceArray.get(i)).append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR).toString();
                }
            }
            return new StringBuilder(String.valueOf(str)).append("]").toString();
        }
    }

    public static class StridCaliData {
        public int caliRunFactor;
        public int caliWalkFactor;
        public boolean strideCaliDataClr;
        public boolean strideCaliMode;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "strideCaliMode=" + this.strideCaliMode + "\r\nstrideCaliDataClr=" + this.strideCaliDataClr + "\r\ncaliWalkFactor=" + this.caliWalkFactor + "\r\ncaliRunFactor=" + this.caliRunFactor;
        }
    }

    public enum TIMEFORMAT {
        TIMEFORMAT_12H,
        TIMEFORMAT_24H
    }

    public static class TimeData {
        public byte day;
        public byte hour;
        public byte minute;
        public byte month;
        public byte second;
        public short year;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "year=" + this.year + " month=" + this.month + " day=" + this.day + " hour=" + this.hour + " minute=" + this.minute + " second=" + this.second;
        }

        public String toExString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return this.year + "-" + this.month + "-" + this.day + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + this.hour + ":" + this.minute;
        }
    }

    public static class UserInfo {
        public byte ADLCalorieGoalOpt;
        public byte HMRDisplayType;
        public byte MHRAutoAdj;
        public byte WORecording;
        public byte birthDay;
        public byte birthMonth;
        public short birthYear;
        public short bodyHeight;
        public short bodyWeight;
        public byte displayOrien;
        public byte genderType;
        public short maxHR;
        public short resetHR;
        public byte unitType;
        public byte woDispMode;

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "gender=" + this.genderType + " unit=" + this.unitType + " HMRDisplayType=" + this.HMRDisplayType + " displayOrien=" + this.displayOrien + " woDispMode=" + this.woDispMode + " MHRAutoAdj=" + this.MHRAutoAdj + " birthYear=" + (this.birthYear + 1900) + " birthMonth=" + this.birthMonth + " birthDay=" + this.birthDay + " bodyWeight=" + this.bodyWeight + " bodyHeight=" + this.bodyHeight + " resetHR=" + this.resetHR + " maxHR=" + this.maxHR + " WORecording=" + this.WORecording;
        }
    }

    public enum UserScreen1Type {
        SCREEN1_EMPTY,
        CLOCK,
        RUNNING_TOTAL_STEPS,
        RUNNING_PACE,
        RUNNING_TOTAL_KCAL,
        RUNNING_TOTAL_DISTANCE,
        RUNNING_SPEED,
        LAP_STEPS,
        LAP_KCAL,
        LAP_DISTANCE,
        LAP_TIME,
        TOTAL_LAPS
    }

    public enum UserScreen2Type {
        SCREEN2_EMPTY,
        AVERAGE_HR,
        AVERAGE_PACE,
        AVERAGE_SPEED,
        TOTAL_TIME,
        TOTAL_STEPS,
        TOTAL_KCAL,
        TOTAL_DISTANCE,
        TOTAL_LAP_CYCLE,
        BEST_LAP_TIME,
        BEST_PACE,
        BEST_SPEED,
        IN_ZONE_TIME
    }

    public static class UserScreenData {
        public UserScreen1Type us1_1;
        public UserScreen1Type us1_2;
        public UserScreen1Type us1_3;
        public UserScreen1Type us1_4;
        public UserScreen2Type us2_1;
        public UserScreen2Type us2_2;
        public UserScreen2Type us2_3;
        public UserScreen2Type us2_4;
        public UserScreen2Type us2_5;
        public UserScreen2Type us2_6;

        public UserScreenData() {
            this.us1_1 = UserScreen1Type.SCREEN1_EMPTY;
            this.us1_2 = UserScreen1Type.SCREEN1_EMPTY;
            this.us1_3 = UserScreen1Type.SCREEN1_EMPTY;
            this.us1_4 = UserScreen1Type.SCREEN1_EMPTY;
            this.us2_1 = UserScreen2Type.SCREEN2_EMPTY;
            this.us2_2 = UserScreen2Type.SCREEN2_EMPTY;
            this.us2_3 = UserScreen2Type.SCREEN2_EMPTY;
            this.us2_4 = UserScreen2Type.SCREEN2_EMPTY;
            this.us2_5 = UserScreen2Type.SCREEN2_EMPTY;
            this.us2_6 = UserScreen2Type.SCREEN2_EMPTY;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "UserScreenData: us1_1=" + MioSportMsgParserUtil.getCodeUS1(this.us1_1) + " us1_2=" + MioSportMsgParserUtil.getCodeUS1(this.us1_2) + " us1_3=" + MioSportMsgParserUtil.getCodeUS1(this.us1_3) + " us1_4=" + MioSportMsgParserUtil.getCodeUS1(this.us1_4) + " us2_1=" + MioSportMsgParserUtil.getCodeUS2(this.us2_1) + " us2_2=" + MioSportMsgParserUtil.getCodeUS2(this.us2_2) + " us2_3=" + MioSportMsgParserUtil.getCodeUS2(this.us2_3) + " us2_4=" + MioSportMsgParserUtil.getCodeUS2(this.us2_4) + " us2_5=" + MioSportMsgParserUtil.getCodeUS2(this.us2_5) + " us2_6=" + MioSportMsgParserUtil.getCodeUS2(this.us2_6);
        }
    }

    public static class VeloDeviceStatus {
        public boolean RawDataOverwrite;
        public boolean SummaryOverwrite;
        public boolean VeloHasRecord;
        public boolean streamModeStatus;

        public VeloDeviceStatus() {
            this.streamModeStatus = false;
            this.SummaryOverwrite = false;
            this.RawDataOverwrite = false;
            this.VeloHasRecord = false;
        }

        public String toString() {
            String str = DiffResult.OBJECTS_SAME_STRING;
            return "streamModeStatus=" + this.streamModeStatus + " SummaryOverwrite=" + this.SummaryOverwrite + " RawDataOverwrite=" + this.RawDataOverwrite + " VeloHasRecord=" + this.VeloHasRecord;
        }
    }

    public static class VeloMemoryState {
        public byte Cadence;
        public byte HR;
        public byte HRV;
        public byte Power;
        public byte Speed;

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "HR=" + this.HR + "\r\nHRV=" + this.HRV + "\r\nSpeed=" + this.Speed + "\r\nCadence=" + this.Cadence + "\r\nPower=" + this.Power;
        }
    }

    public static class VeloRecordData {
        public int Ctrlinfo;
        public int Startbyte;
        public int Startpage;
        public String WorkoutTime;
        public int Workoutsecs;
        public int Workoutsize;
        public String log;
        public ArrayList<VeloRowData> rowDatas;

        public VeloRecordData() {
            this.WorkoutTime = DiffResult.OBJECTS_SAME_STRING;
            this.rowDatas = new ArrayList();
            this.log = DiffResult.OBJECTS_SAME_STRING;
        }

        public String toString() {
            String str = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(IOUtils.LINE_SEPARATOR_WINDOWS + "Time:" + this.WorkoutTime + IOUtils.LINE_SEPARATOR_WINDOWS)).append("startPage:").append(this.Startpage).append(" startByte:").append(this.Startbyte).append(" Ctrlinfo:").append(this.Ctrlinfo).append(" workoutsecs:").append(this.Workoutsecs).append(" workoutsize:").append(this.Workoutsize).append(IOUtils.LINE_SEPARATOR_WINDOWS).toString())).append("-------------------------\r\n").toString())).append("HR  |  SPEEP |  CADENCE |  POWER  | HRV\r\n").toString();
            for (int i = 0; i < this.rowDatas.size(); i++) {
                str = new StringBuilder(String.valueOf(str)).append(((VeloRowData) this.rowDatas.get(i)).toString()).toString();
            }
            return new StringBuilder(String.valueOf(str)).append(IOUtils.LINE_SEPARATOR_WINDOWS).toString();
        }
    }

    public static class VeloRowData {
        public int CADENCE;
        public int HR;
        public ArrayList<Integer> HRVS;
        public int POWER;
        public float SPEED;
        public String log;

        public VeloRowData() {
            this.SPEED = GroundOverlayOptions.NO_DIMENSION;
            this.CADENCE = -1;
            this.POWER = -1;
            this.HRVS = new ArrayList();
            this.HR = -1;
            this.log = DiffResult.OBJECTS_SAME_STRING;
        }

        public String toString() {
            String str = IOUtils.LINE_SEPARATOR_WINDOWS + this.HR + " |  " + this.SPEED + "    |    " + this.CADENCE + "    |    " + this.POWER + "    |    ";
            if (this.HRVS.size() > 0) {
                str = new StringBuilder(String.valueOf(str)).toString();
                for (int i = 0; i < this.HRVS.size(); i++) {
                    int hrv = ((Integer) this.HRVS.get(i)).intValue();
                    if (i > 0) {
                        str = new StringBuilder(String.valueOf(str)).append(",").toString();
                    }
                    str = new StringBuilder(String.valueOf(str)).append(hrv).toString();
                }
                str = new StringBuilder(String.valueOf(str)).toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append(" -1").toString();
            }
            return str.replace("-1", "  ").replace(".0", "  ");
        }
    }

    public static class WODisplay {
        public boolean dataCalorieEnable;
        public boolean dataDistanceEnable;
        public boolean dataPaceEnable;
        public boolean dataSpeedEnable;
        public boolean dataStepEnable;
        public boolean dataTimeEnable;

        public WODisplay() {
            this.dataCalorieEnable = false;
            this.dataStepEnable = false;
            this.dataDistanceEnable = false;
            this.dataPaceEnable = false;
            this.dataSpeedEnable = false;
            this.dataTimeEnable = false;
        }

        public String toString() {
            return DiffResult.OBJECTS_SAME_STRING + "dataCalorieEnable=" + this.dataCalorieEnable + " dataStepEnable=" + this.dataStepEnable + " dataDistanceEnable=" + this.dataDistanceEnable + " dataPaceEnable=" + this.dataPaceEnable + " dataSpeedEnable=" + this.dataSpeedEnable + " dataTimeEnable=" + this.dataTimeEnable;
        }
    }

    public static class WorkoutRecord {
        public RecordLogData logData;
        public WorkoutRecordSummary workoutRecordSummary;

        public String toString() {
            String str = "\r\n WorkoutRecord [\r\n";
            if (this.workoutRecordSummary != null) {
                str = new StringBuilder(String.valueOf(str)).append(this.workoutRecordSummary.toString()).toString();
            }
            if (this.logData != null) {
                str = new StringBuilder(String.valueOf(str)).append(this.logData.toString()).toString();
            }
            return new StringBuilder(String.valueOf(str)).append("]").toString();
        }
    }

    public static class WorkoutRecordSummary {
        public short aHR;
        public short calorie;
        public int dist;
        public ExerciseTimeData exerciseTime;
        public short maxSpeed;
        public int step;
        public TimeData time;
        public short timeInZone;
        public short timeInZone1;
        public short timeInZone2;
        public short timeInZone3;
        public short timeInZone4;
        public short timeInZone5;

        public String toString() {
            return "\r\nWorkoutRecordSummary [" + this.time.toString() + this.exerciseTime.toString() + " step=" + this.step + " dist=" + this.dist + " calorie=" + this.calorie + " maxSpeed=" + this.maxSpeed + " timeInZone=" + this.timeInZone + " timeInZone1=" + this.timeInZone1 + " timeInZone2=" + this.timeInZone2 + " timeInZone3=" + this.timeInZone3 + " timeInZone4=" + this.timeInZone4 + " timeInZone5=" + this.timeInZone5 + " aHR=" + this.aHR + "]";
        }
    }
}
