package org.example.Query.Value;

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

    public void setTime(String time) {
        this.time = time;
    }
}
