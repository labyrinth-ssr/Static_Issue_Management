package org.example.Query.Value;

import java.math.BigDecimal;

public class Int4Time4String1Value {
    Long intValue1;
    Long intValue2;
    Long intValue3;
    Long intValue4;

    String time1;
    String time2;
    String time3;
    String time4;

    String stringValue;

    public Long getIntValue1() {
        return intValue1 == null ? 0 : intValue1;
    }

    public void setIntValue1(Long intValue1) {
        this.intValue1 = intValue1;
    }

    public Long getIntValue2() {
        return intValue2 == null ? 0 : intValue2;
    }

    public void setIntValue2(Long intValue2) {
        this.intValue2 = intValue2;
    }

    public Long getIntValue3() {
        return intValue3 == null ? 0 : intValue3;
    }

    public void setIntValue3(Long intValue3) {
        this.intValue3 = intValue3;
    }

    public Long getIntValue4() {
        return intValue4 == null ? 0 : intValue4;
    }

    public void setIntValue4(Long intValue4) {
        this.intValue4 = intValue4;
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

    public String getTime3() {
        return time3;
    }

    public void setTime3(BigDecimal time) {
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
        this.time3 = s.toString();
    }

    public String getTime4() {
        return time4;
    }

    public void setTime4(BigDecimal time) {
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
        this.time4 = s.toString();
    }
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
