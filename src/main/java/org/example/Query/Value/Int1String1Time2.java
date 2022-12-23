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

    public void setTime1(String time1) {
        this.time1 = time1;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }
}
