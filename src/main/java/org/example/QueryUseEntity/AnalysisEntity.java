package org.example.QueryUseEntity;

import java.math.BigDecimal;

public class AnalysisEntity {
    Long total;
    Long done;
    BigDecimal percentage;
    String type;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getDone() {
        return done;
    }

    public void setDone(Long done) {
        this.done = done;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AnalysisEntity{" +
                "total=" + total +
                ", done=" + done +
                ", percentage=" + percentage +
                ", type=" + type +
                '}';
    }

    public String toString_total() {
        return "all:\n\t" +
                " total=" + total +
                ", done=" + done +
                ", percentage=" + percentage;
    }


    public String toString_defect() {
        return "\t" +
                " type=" + type +
                ", total=" + total +
                ", done=" + done +
                ", percentage=" + percentage;
    }

    public String toString_type() {
        return "\t" +
                "type_id=" + type +
                ", total=" + total +
                ", done=" + done +
                ", percentage=" + percentage;
    }
}
