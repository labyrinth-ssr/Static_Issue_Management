package org.example.Utils;

import java.math.BigDecimal;

public class TimeUtil {

    public static Long time;
    public static String getTimeDuration(BigDecimal duration){
            long time_duration = duration.longValue();
            long diffSeconds = time_duration % 60;
            long diffMinutes = time_duration / (60) % 60;
            long diffHours = time_duration / (60 * 60) % 24;
            long diffDays = time_duration / (24 * 60 * 60);
            StringBuffer s = new StringBuffer();
            if(diffDays > 0) {
                s.append(diffDays).append("天");
                s.append(diffHours).append("时");
            }else if(diffHours > 0) s.append(diffHours).append("时");
            s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
            return s.toString();
    }

    public static String getTimeDuration(Long duration){
        long time_duration = duration.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuffer s = new StringBuffer();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        return s.toString();
    }

    public static void begin(){
        time = System.currentTimeMillis();
    }

    public static String end(){
        Long duration = (System.currentTimeMillis() - time)/1000;
        return getTimeDuration(duration);
    }
}
