package com.mioglobal.android.ble.sdk.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.mioglobal.android.ble.sdk.MioDeviceConnection;
import com.mioglobal.android.ble.sdk.MioDeviceManager;
import com.mioglobal.android.ble.sdk.MioHelper;
import com.mioglobal.android.ble.sdk.StringUtil;
//import io.fabric.sdk.android.services.events.EventsFilesManager;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.builder.DiffResult;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.EthiopicChronology;

public class MIOPhoneReceiver extends BroadcastReceiver {
    Context context;
    int index;
    private int lastState;
    PhoneStateListener listener;

    /* renamed from: com.mioglobal.android.ble.sdk.notification.MIOPhoneReceiver.1 */
    class C02881 extends PhoneStateListener {
        C02881() {
        }

        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            MioDeviceConnection conn;
            switch (state) {
                case MutableDateTime.ROUND_NONE /*0*/:
                    MIOPhoneReceiver.this.index = 0;
                    Log.e("onCallStateChanged", "CALL_STATE_IDLE" + MIOPhoneReceiver.this.lastState + state);
                    if (MIOPhoneReceiver.this.lastState == 1) {
                        conn = MioDeviceManager.GetMioDeviceManager_MIO().curConnection;
                        if (conn != null && conn.isSupportMoblieEvent()) {
                            conn.SendMobileEvent(0, (byte) 2, 2, 1, 0);
                            break;
                        }
                    }
                    break;
                case EthiopicChronology.EE /*1*/:
                    if (MIOPhoneReceiver.this.index <= 0) {
                        MIOPhoneReceiver mIOPhoneReceiver = MIOPhoneReceiver.this;
                        mIOPhoneReceiver.index++;
                        Log.e("onCallStateChanged", "CALL_STATE_RINGING=" + incomingNumber);
                        conn = MioDeviceManager.GetMioDeviceManager_MIO().curConnection;
                        if (conn != null && conn.isSupportMoblieEvent() && MIOPhoneReceiver.this.index == 1) {
                            String str = StringUtil.getUserNameByPhone(MIOPhoneReceiver.this.context, incomingNumber);
                            if (str == null || str.length() == 0) {
                                if (incomingNumber != null && incomingNumber.length() != 0 && StringUtil.isLetterDigitOrNumber(str)) {
                                    conn.SendMobileMsg(MioHelper.MOBILE_PHONE_ALERT, new StringBuilder(String.valueOf(incomingNumber)).toString());
                                    break;
                                } else {
                                    conn.SendMobileMsg(MioHelper.MOBILE_PHONE_ALERT, DiffResult.OBJECTS_SAME_STRING);
                                    break;
                                }
                            }
                            String[] rep = new String[]{"[", "~", "!", "@", "#", "$", "%", "^", "&", "*", "<", ">", "(", ")", "_", "-", "+", "=", ",", ".", InternalZipConstants.ZIP_FILE_SEPARATOR, "?", ";", ":", "'", "\\", "|", "{", "}", "]", "\"", "`"};
                            for (CharSequence replace : rep) {
                                str = str.replace(replace, DiffResult.OBJECTS_SAME_STRING);
                            }
                            while (str.indexOf("  ") > -1) {
                                str = str.replace("  ", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                            }
                            if (!StringUtil.isLetterDigitOrNumber(str)) {
                                if (!StringUtil.isLetterDigitOrNumber(str.replace(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, DiffResult.OBJECTS_SAME_STRING))) {
                                    conn.SendMobileMsg(MioHelper.MOBILE_PHONE_ALERT, new StringBuilder(String.valueOf(incomingNumber)).toString());
                                    break;
                                } else {
                                    conn.SendMobileMsg(MioHelper.MOBILE_PHONE_ALERT, new StringBuilder(String.valueOf(str)).toString());
                                    break;
                                }
                            }
                            conn.SendMobileMsg(MioHelper.MOBILE_PHONE_ALERT, new StringBuilder(String.valueOf(str)).toString());
                            break;
                        }
                    }
                    MIOPhoneReceiver.this.lastState = state;
                    return;
//                    break;
                case MutableDateTime.ROUND_CEILING /*2*/:
                    MIOPhoneReceiver.this.index = 0;
                    Log.e("onCallStateChanged", "CALL_STATE_OFFHOOK");
                    break;
            }
            MIOPhoneReceiver.this.lastState = state;
        }
    }

    public MIOPhoneReceiver() {
        this.context = null;
        this.index = 0;
        this.lastState = 0;
        this.listener = new C02881();
    }

    public void onReceive(Context context, Intent intent) {
        this.context = context;
        System.out.println("action" + intent.getAction());
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            Log.e("MIOPhoneReceiver", "call OUT:" + intent.getStringExtra("android.intent.extra.PHONE_NUMBER"));
            return;
        }
        ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE)).listen(this.listener, 32);
    }
}
