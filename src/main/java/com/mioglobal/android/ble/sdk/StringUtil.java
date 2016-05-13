package com.mioglobal.android.ble.sdk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.regex.Pattern;

public class StringUtil {
    public static String toHexCode(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        int length = data.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(String.format("%02X", new Object[]{Byte.valueOf(data[i])}));
        }
        return stringBuilder.toString();
    }

    public static String toHexCode(byte[] data, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        for (int i = offset; i < offset + length; i++) {
            stringBuilder.append(String.format("%02X", new Object[]{Byte.valueOf(data[i])}));
        }
        return stringBuilder.toString();
    }

    public static byte getByte(int time) {
        return (byte) ((time & 255) + (((time & 255) / 10) * 6));
    }

    public static String getUserNameByPhone(Context context, String phone) {
        if (phone == null || phone.length() == 0) {
            return null;
        }
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"display_name"}, null, null, null);
        if (!cursor.moveToFirst()) {
            return null;
        }
        Log.e("Contacts", "name=" + cursor.getString(0));
        return cursor.getString(0);
    }

    public static boolean isLetterDigitOrNumber(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        if (isContainChinese(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContainChinese(String str) {
        if (str != null && Pattern.compile("[\u4e00-\u9fa5]").matcher(str).find()) {
            return true;
        }
        return false;
    }

    public static boolean hasSpecialCharacter(String str) {
        if (str != null && Pattern.compile("[~!@#$%^&*<>]").matcher(str).find()) {
            return true;
        }
        return false;
    }
}
