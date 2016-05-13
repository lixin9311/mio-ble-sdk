package com.mioglobal.android.ble.sdk.DFU;

import android.os.Parcel;
import com.mioglobal.android.ble.sdk.StringUtil;
import java.util.UUID;

public class UUIDUtils {
    public static void writeToParcel(UUID uuid, Parcel parcel) {
        if (uuid == null) {
            parcel.writeLong(0);
            parcel.writeLong(0);
            return;
        }
        parcel.writeLong(uuid.getLeastSignificantBits());
        parcel.writeLong(uuid.getMostSignificantBits());
    }

    public static UUID readFromParcel(Parcel parcel) {
        long lsb = parcel.readLong();
        long msb = parcel.readLong();
        if (lsb == 0 && msb == 0) {
            return null;
        }
        return new UUID(msb, lsb);
    }

    public static UUID fromByteArray(byte[] byteArray, int offset) {
        String p1 = StringUtil.toHexCode(byteArray, offset + 0, 4);
        String p2 = StringUtil.toHexCode(byteArray, offset + 4, 2);
        String p3 = StringUtil.toHexCode(byteArray, offset + 6, 2);
        String p4 = StringUtil.toHexCode(byteArray, offset + 8, 2);
        String p5 = StringUtil.toHexCode(byteArray, offset + 10, 6);
        return UUID.fromString(String.format("%s-%s-%s-%s-%s", new Object[]{p1, p2, p3, p4, p5}));
    }

    public static byte[] reverse(byte[] byteArray) {
        byte[] rev = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            rev[byteArray.length - (i + 1)] = byteArray[i];
        }
        return rev;
    }
}
