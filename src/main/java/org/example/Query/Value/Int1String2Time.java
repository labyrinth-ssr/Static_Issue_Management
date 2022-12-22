package org.example.Query.Value;

import java.math.BigDecimal;

public class Int1String2Time {
    Long intValue;
    String stringValue1;
    String stringValue2;
    String time;

    public Long getIntValue() {
        return intValue  == null ? 0 : intValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }

    public String getStringValue1() {
        return stringValue1;
    }

    public void setStringValue1(String stringValue1) {
        this.stringValue1 = stringValue1;
    }

    public String getStringValue2() {
        return stringValue2;
    }

    public void setStringValue2(String stringValue2) {
        this.stringValue2 = stringValue2;
    }

    public String getTime() {
        return time;
    }
    public void setTime(BigDecimal time) {
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
        this.time = s.toString();
    }
}
