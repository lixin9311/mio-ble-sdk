package com.mioglobal.android.ble.sdk;

import android.support.v4.media.TransportMediator;
import com.couchbase.lite.replicator.PullerInternal;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.mioglobal.android.ble.sdk.MioUserSetting.TimeData;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.MutableDateTime;
import org.joda.time.chrono.EthiopicChronology;

public class SleepCal {
    private static String getTimeString(TimeData timeData) {
        if (timeData != null) {
            return timeData.year + "-" + timeData.month + "-" + timeData.day + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + timeData.hour + ":" + timeData.minute + ":" + timeData.second;
        }
        return null;
    }

    private static long getTwoHours(String hour1, String hour2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date sDate = format.parse(hour1);
            return (format.parse(hour2).getTime() / DateUtils.MILLIS_PER_HOUR) - (sDate.getTime() / DateUtils.MILLIS_PER_HOUR);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int[] SleepTracking_MIO(int[] activity, int slen, int s) {
        if (activity == null || slen < 1) {
            return null;
        }
        SleepFActor[] factor = new SleepFActor[]{new SleepFActor(), new SleepFActor()};
        int[] sleeppattern = new int[slen];
        int sleepstatus = 0;
        int n = 0;
        int m = 0;
        int p = 0;
        int temp = 0;
        int temp_cross = 0;
        int Totalsleeptime = 0;
        factor[0].time_fallsleep = 5;
        factor[0].thd1 = 10;
        factor[0].thd2 = 10;
        factor[0].thd3 = 10;
        factor[0].thd4 = 100;
        factor[0].thd5 = PullerInternal.MAX_PENDING_DOCS;
        factor[0].p1 = 3;
        factor[0].p2 = 5;
        factor[0].m1 = 3;
        factor[0].c1 = 3;
        factor[0].n1 = 5;
        factor[0].n2a = 3;
        factor[0].n2b = 10;
        factor[0].m2a = 4;
        factor[0].m2b = 2;
        factor[0].c2a = 3;
        factor[0].c2b = 3;
        factor[1].time_fallsleep = 5;
        factor[1].thd1 = 10;
        factor[1].thd2 = 10;
        factor[1].thd3 = 10;
        factor[1].thd4 = 100;
        factor[1].thd5 = PullerInternal.MAX_PENDING_DOCS;
        factor[1].p1 = 2;
        factor[1].p2 = 5;
        factor[1].m1 = 5;
        factor[1].c1 = 2;
        factor[1].n1 = 10;
        factor[1].n2a = 3;
        factor[1].n2b = 15;
        factor[1].m2a = 5;
        factor[1].m2b = 3;
        factor[1].c2a = 2;
        factor[1].c2b = 2;
        if (s > 1) {
            s = 1;
        }
        int i = 0;
        while (i < slen) {
            int i2;
            int i3;
            int tempa = activity[i];
            if (tempa > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                tempa -= 128;
                temp_cross++;
            }
            if (tempa < 2) {
                tempa = 0;
            }
            temp += tempa;
            if (tempa == 0) {
                m++;
                p = 0;
            } else {
                p++;
                m = 0;
                if (temp == 0) {
                    n = 0;
                }
            }
            n++;
            int makeupCount = 0;
            switch (sleepstatus) {
                case MutableDateTime.ROUND_NONE /*0*/:
                    if (m >= factor[s].time_fallsleep) {
                        sleepstatus = 2;
                        makeupCount = 4;
                        n = 0;
                        m = 0;
                        p = 0;
                        temp = 0;
                        temp_cross = 0;
                        break;
                    }
                    break;
                case EthiopicChronology.EE /*1*/:
                case MutableDateTime.ROUND_CEILING /*2*/:
                    Totalsleeptime++;
                    if (p >= factor[s].p1) {
                        i2 = (temp * 10) / n;
                        i3 = factor[s].thd1;
                        if (i2 > i3) {
                            if (sleepstatus == 2) {
                                sleepstatus = 1;
                                makeupCount = p - 1;
                            }
                            if ((p >= factor[s].p2 && temp > factor[s].thd4) || temp > factor[s].thd5) {
                                sleepstatus = 0;
                                makeupCount = 5;
                                if (Totalsleeptime < 30) {
                                    makeupCount = Totalsleeptime + 10;
                                    Totalsleeptime = 0;
                                }
                            }
                            n = 0;
                            m = 0;
                            temp = 0;
                            temp_cross = 0;
                            break;
                        }
                    }
                    if (sleepstatus != 1) {
                        if (sleepstatus == 2) {
                            if (m > factor[s].m2a) {
                                n = 0;
                                temp_cross = 0;
                                temp = 0;
                            }
                            if (n < factor[s].n2a) {
                                if (n >= factor[s].n2b) {
                                    i2 = (temp * 10) / n;
                                    i3 = factor[s].thd3;
                                    if (i2 > i3 || temp_cross >= factor[s].c2b) {
                                        sleepstatus = 1;
                                        makeupCount = n - 1;
                                        n = 0;
                                        m = 0;
                                        p = 0;
                                        temp = 0;
                                        temp_cross = 0;
                                        break;
                                    }
                                }
                            } else if (m < factor[s].m2b) {
                                i2 = (temp * 10) / n;
                                i3 = factor[s].thd2;
                                if (i2 > i3 || temp_cross >= factor[s].c2a) {
                                    sleepstatus = 1;
                                    makeupCount = n - 1;
                                    n = 0;
                                    m = 0;
                                    p = 0;
                                    temp = 0;
                                    temp_cross = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if (m > factor[s].m1) {
                        temp_cross = 0;
                        p = 0;
                        temp = 0;
                    }
                    if (n >= factor[s].n1) {
                        i2 = (temp * 10) / n;
                        i3 = factor[s].thd1;
                        if (i2 < i3 && m > factor[s].m1 + 1 && temp_cross < factor[s].c1) {
                            sleepstatus = 2;
                            makeupCount = m - 1;
                            n = 0;
                            m = 0;
                            p = 0;
                            temp = 0;
                            temp_cross = 0;
                            break;
                        }
                    }
                    break;
            }
            sleeppattern[i] = sleepstatus;
            if (makeupCount > 0) {
                int ii = 1;
                while (ii <= makeupCount) {
                    i2 = i - ii;
                    i3 = sleeppattern.length;
                    if (i2 < i3 && i - ii >= 0) {
                        sleeppattern[i - ii] = sleepstatus;
                        ii++;
                    }
                }
            }
            i++;
        }
        return sleeppattern;
    }
}
