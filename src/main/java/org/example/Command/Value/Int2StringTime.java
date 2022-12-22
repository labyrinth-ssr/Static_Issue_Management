package org.example.Command.Value;

import java.math.BigDecimal;

public class Int2StringTime {
    Long intValue1;

    Long intValue2;

    String stringValue;

    String time;

    public Long getIntValue1() {
        return intValue1== null ? 0 : intValue1;
    }

    public void setIntValue1(Long intValue1) {
        this.intValue1 = intValue1;
    }

    public Long getIntValue2() {
        return intValue2== null ? 0 : intValue2;
    }

    public void setIntValue2(Long intValue2) {
        this.intValue2 = intValue2;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
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
