package com.mioglobal.android.ble.sdk.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.gsm.SmsMessage;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.mioglobal.android.ble.sdk.MioDeviceConnection;
import com.mioglobal.android.ble.sdk.MioDeviceManager;
import com.mioglobal.android.ble.sdk.MioHelper;
import com.mioglobal.android.ble.sdk.StringUtil;
//import io.fabric.sdk.android.services.events.EventsFilesManager;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.builder.DiffResult;

public class MIOSMSReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String TAG = "ImiChatSMSReceiver";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            String mobile = null;
            for (SmsMessage message : getMessagesFromIntent(intent)) {
                mobile = message.getOriginatingAddress();
            }
            MioDeviceConnection conn = MioDeviceManager.GetMioDeviceManager_MIO().curConnection;
            if (conn != null && conn.isSupportMoblieEvent() && mobile != null && mobile.length() > 0) {
                mobile.replace("+", DiffResult.OBJECTS_SAME_STRING);
                String str = StringUtil.getUserNameByPhone(context, mobile);
                if (str != null && str.length() != 0) {
                    String[] rep = new String[]{"[", "~", "!", "@", "#", "$", "%", "^", "&", "*", "<", ">", "(", ")", "_", "-", "+", "=", ",", ".", InternalZipConstants.ZIP_FILE_SEPARATOR, "?", ";", ":", "'", "\\", "|", "{", "}", "]", "\"", "`"};
                    for (CharSequence replace : rep) {
                        str = str.replace(replace, DiffResult.OBJECTS_SAME_STRING);
                    }
                    while (str.indexOf("  ") > -1) {
                        str = str.replace("  ", MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                    }
                    if (StringUtil.isLetterDigitOrNumber(str)) {
                        conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, new StringBuilder(String.valueOf(str)).toString());
                    } else if (StringUtil.isLetterDigitOrNumber(str.replace(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR, DiffResult.OBJECTS_SAME_STRING))) {
                        conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, new StringBuilder(String.valueOf(str)).toString());
                    } else {
                        conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, new StringBuilder(String.valueOf(mobile)).toString());
                    }
                } else if (mobile == null || mobile.length() == 0 || !StringUtil.isLetterDigitOrNumber(str)) {
                    conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, DiffResult.OBJECTS_SAME_STRING);
                } else {
                    conn.SendMobileMsg(MioHelper.MOBILE_MSG_ALERT, new StringBuilder(String.valueOf(mobile)).toString());
                }
            }
        }
    }

    public final SmsMessage[] getMessagesFromIntent(Intent intent) {
        int i;
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}
