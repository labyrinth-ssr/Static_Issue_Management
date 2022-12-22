package org.example.Query.Value;

import java.math.BigDecimal;

public class Int1String1Time2 {
    Long intValue;
    String stringValue;
    String time1;

    String time2;

    public Long getIntValue() {
        return intValue== null ? 0 : intValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getTime1() {
        return time1;
    }
    public void setTime1(BigDecimal time) {
        long time_duration = time.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuilder s = new StringBuilder();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.time1 = s.toString();
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(BigDecimal time) {
        long time_duration = time.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuilder s = new StringBuilder();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.time2 = s.toString();
    }
}
