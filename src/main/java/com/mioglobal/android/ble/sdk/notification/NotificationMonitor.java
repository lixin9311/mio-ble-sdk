package com.mioglobal.android.ble.sdk.notification;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.mioglobal.android.ble.sdk.MioDeviceConnection;
import com.mioglobal.android.ble.sdk.MioDeviceManager;
import com.mioglobal.android.ble.sdk.MioHelper;
import com.mioglobal.android.ble.sdk.StringUtil;
//import io.fabric.sdk.android.services.events.EventsFilesManager;
import java.util.ArrayList;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.builder.DiffResult;

public class NotificationMonitor extends NotificationListenerService {
    private static final int EVENT_UPDATE_CURRENT_NOS = 0;
    private static final String TAG = "SevenNLS";
    private static final String TAG_PRE;
    public static List<StatusBarNotification[]> mCurrentNotifications;
    public static int mCurrentNotificationsCounts;
    public static StatusBarNotification mPostedNotification;
    public static StatusBarNotification mRemovedNotification;
    private Handler mMonitorHandler;

    /* renamed from: com.mioglobal.android.ble.sdk.notification.NotificationMonitor.1 */
    class C02891 extends Handler {
        C02891() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NotificationMonitor.EVENT_UPDATE_CURRENT_NOS /*0*/:
                    NotificationMonitor.this.updateCurrentNotifications();
                default:
            }
        }
    }

    public NotificationMonitor() {
        this.mMonitorHandler = new C02891();
    }

    static {
        TAG_PRE = "[" + NotificationMonitor.class.getSimpleName() + "] ";
        mCurrentNotifications = new ArrayList();
        mCurrentNotificationsCounts = EVENT_UPDATE_CURRENT_NOS;
    }

    public void onCreate() {
        super.onCreate();
        logNLS("onCreate...");
        this.mMonitorHandler.sendMessage(this.mMonitorHandler.obtainMessage(EVENT_UPDATE_CURRENT_NOS));
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        logNLS("onBind...");
        return super.onBind(intent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        logNLS("onNotificationPosted...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mPostedNotification = sbn;
        String pkgName = sbn.getPackageName();
        logNLS(pkgName);
        if (pkgName != null) {
            MioDeviceConnection conn = MioDeviceManager.GetMioDeviceManager_MIO().curConnection;
            if (conn != null && conn.isSupportMoblieEvent()) {
                String apkName = MioDeviceManager.GetMioDeviceManager_MIO().getNeedNotificationName(pkgName);
                if (apkName != null && apkName.length() > 0) {
                    if (apkName.equals("GMAIL")) {
                        conn.SendMobileMsg(MioHelper.MOBILE_EMAIL_ALERT, "EMAIL");
                        return;
                    }
                    String[] rep = new String[]{"[", "~", "!", "@", "#", "$", "%", "^", "&", "*", "<", ">", "(", ")", "_", "-", "+", "=", ",", ".", InternalZipConstants.ZIP_FILE_SEPARATOR, "?", ";", ":", "'", "\\", "|", "{", "}", "]", "\"", "`", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR};
                    for (int i = EVENT_UPDATE_CURRENT_NOS; i < rep.length; i++) {
                        apkName = apkName.replace(rep[i], DiffResult.OBJECTS_SAME_STRING);
                    }
                    if (StringUtil.isLetterDigitOrNumber(apkName)) {
                        conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, apkName);
                    } else {
                        conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, "ONE MESSAGE");
                    }
                }
            }
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        logNLS("removed...");
        logNLS("have " + mCurrentNotificationsCounts + " active notifications");
        mRemovedNotification = sbn;
    }

    private void updateCurrentNotifications() {
        try {
            StatusBarNotification[] activeNos = getActiveNotifications();
            if (mCurrentNotifications.size() == 0) {
                mCurrentNotifications.add(null);
            }
            mCurrentNotifications.set(EVENT_UPDATE_CURRENT_NOS, activeNos);
            mCurrentNotificationsCounts = activeNos.length;
        } catch (Exception e) {
            logNLS("Should not be here!!");
            e.printStackTrace();
        }
    }

    public static StatusBarNotification[] getCurrentNotifications() {
        if (mCurrentNotifications.size() != 0) {
            return (StatusBarNotification[]) mCurrentNotifications.get(EVENT_UPDATE_CURRENT_NOS);
        }
        logNLS("mCurrentNotifications size is ZERO!!");
        return null;
    }

    private static void logNLS(Object object) {
        Log.e(TAG, TAG_PRE + object);
    }
}
