package org.example.Query.Value;

public class IntValue {
    Long intValue;

    public Long getIntValue() {
        return intValue == null ? 0 : intValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }
}
