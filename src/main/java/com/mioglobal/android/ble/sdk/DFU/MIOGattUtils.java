package com.mioglobal.android.ble.sdk.DFU;

import android.support.v4.media.session.PlaybackStateCompat;
//import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.UUID;

public class MIOGattUtils {
    public static final int EIGTH_BITMASK = 128;
    public static final int FIFTH_BITMASK = 16;
    public static final int FIRST_BITMASK = 1;
    public static final int FORMAT_FLOAT = 52;
    public static final int FORMAT_SFLOAT = 50;
    public static final int FORMAT_SINT16 = 34;
    public static final int FORMAT_SINT32 = 36;
    public static final int FORMAT_SINT8 = 33;
    public static final int FORMAT_UINT16 = 18;
    public static final int FORMAT_UINT32 = 20;
    public static final int FORMAT_UINT8 = 17;
    public static final int FOURTH_BITMASK = 8;
    public static final int SECOND_BITMASK = 2;
    public static final int SEVENTH_BITMASK = 64;
    public static final int SIXTH_BITMASK = 32;
    public static final int THIRD_BITMASK = 4;
    public static final long leastSigBits = -9223371485494954757L;

    public static UUID toUuid(String uuidString) {
        return UUID.fromString(uuidString);
    }

    public static UUID toUuid(long assignedNumber) {
        return new UUID((assignedNumber << SIXTH_BITMASK) | PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM, leastSigBits);
    }

    public static String toUuid128(long assignedNumber) {
        return toUuid(assignedNumber).toString();
    }

    public static String toUuid16(int assignedNumber) {
        return Integer.toHexString(assignedNumber);
    }

    public static Integer getIntValue(byte[] value, int format, int position) {
        if (value == null || (format & 15) + position > value.length) {
            return null;
        }
        switch (format) {
            case FORMAT_UINT8 /*17*/:
                return Integer.valueOf(value[position] & 255);
            case FORMAT_UINT16 /*18*/:
                return Integer.valueOf(add(value[position], value[position + FIRST_BITMASK]));
            case FORMAT_UINT32 /*20*/:
                return Integer.valueOf(add(value[position], value[position + FIRST_BITMASK], value[position + SECOND_BITMASK], value[position + 3]));
            case FORMAT_SINT8 /*33*/:
                return Integer.valueOf(signed(value[position] & 255, FOURTH_BITMASK));
            case FORMAT_SINT16 /*34*/:
                return Integer.valueOf(signed(add(value[position], value[position + FIRST_BITMASK]), FIFTH_BITMASK));
            case FORMAT_SINT32 /*36*/:
                return Integer.valueOf(signed(add(value[position], value[position + FIRST_BITMASK], value[position + SECOND_BITMASK], value[position + 3]), SIXTH_BITMASK));
            default:
                return null;
        }
    }

    public static Float getFloatValue(byte[] value, int format, int position) {
        if (value == null || (format & 15) + position > value.length) {
            return null;
        }
        switch (format) {
            case FORMAT_SFLOAT /*50*/:
                int i = value[position + FIRST_BITMASK];
                return Float.valueOf((float) (((double) signed((value[position] & 255) + (((i & 255) & 15) << FOURTH_BITMASK), 12)) * Math.pow(10.0d, (double) signed((i & 255) >> THIRD_BITMASK, THIRD_BITMASK))));
            case FORMAT_FLOAT /*52*/:
                int exponent = value[position + 3];
                int mantissa = value[position + SECOND_BITMASK];
                return Float.valueOf((float) (((double) signed(((value[position] & 255) + ((value[position + FIRST_BITMASK] & 255) << FOURTH_BITMASK)) + ((mantissa & 255) << FIFTH_BITMASK), 24)) * Math.pow(10.0d, (double) exponent)));
            default:
                return null;
        }
    }

    public static String getStringValue(byte[] value, int position) {
        if (value == null || position > value.length) {
            return null;
        }
        byte[] arrayOfByte = new byte[(value.length - position)];
        for (int i = 0; i != value.length - position; i += FIRST_BITMASK) {
            arrayOfByte[i] = value[position + i];
        }
        return new String(arrayOfByte);
    }

    private static int add(byte byte1, byte byte2) {
        return (byte1 & 255) + ((byte2 & 255) << FOURTH_BITMASK);
    }

    private static int add(byte byte1, byte byte2, byte byte3, byte byte4) {
        return (((byte1 & 255) + ((byte2 & 255) << FOURTH_BITMASK)) + ((byte3 & 255) << FIFTH_BITMASK)) + ((byte4 & 255) << 24);
    }

    private static int signed(int value, int length) {
        if (((FIRST_BITMASK << (length - 1)) & value) != 0) {
            return ((FIRST_BITMASK << (length - 1)) - (((FIRST_BITMASK << (length - 1)) - 1) & value)) * -1;
        }
        return value;
    }

    public static byte[] hexByteArrayToByteArray(byte[] hexByteArray) {
        return hexStringToByteArray(new String(hexByteArray));
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[(len / SECOND_BITMASK)];
        for (int i = 0; i < len; i += SECOND_BITMASK) {
            data[i / SECOND_BITMASK] = (byte) ((Character.digit(hexString.charAt(i), FIFTH_BITMASK) << THIRD_BITMASK) + Character.digit(hexString.charAt(i + FIRST_BITMASK), FIFTH_BITMASK));
        }
        return data;
    }
}
