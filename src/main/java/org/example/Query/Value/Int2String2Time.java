package org.example.Query.Value;

import java.math.BigDecimal;

public class Int2String2Time {
    Long intValue1;
    Long intValue2;
    String stringValue1;
    String stringValue2;
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

    public void setTime(String time) {
        this.time = time;
    }
}
