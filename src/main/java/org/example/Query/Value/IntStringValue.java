package org.example.Query.Value;

public class IntStringValue {
    Long intValue;

    String stringValue;

    public Long getIntValue() {
        return intValue  == null ? 0 : intValue;
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
}
