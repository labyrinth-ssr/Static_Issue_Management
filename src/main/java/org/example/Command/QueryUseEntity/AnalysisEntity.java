package org.example.Command.QueryUseEntity;

import java.math.BigDecimal;

public class AnalysisEntity {
    Long total;
    Long done;
    String percentage;
    String type;

    String average_exist_duration;

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

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
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
                ", average_exist_duration="+average_exist_duration+
                '}';
    }

    public String toString_total() {
        return "all:\n\t" +
                " total=" + total +
                ", done=" + done +
                ", percentage=" + percentage +
                ", average_exist_duration="+average_exist_duration;
    }


    public String toString_defect() {
        return "\t" +
                " type=" + type +
                ", total=" + total +
                ", done=" + done +
                ", percentage=" + percentage+
                ", average_exist_duration="+average_exist_duration;
    }

    public String toString_type() {
        return "\t" +
                "type_id=" + type +
                ", total=" + total +
                ", done=" + done +
                ", percentage=" + percentage+
                ", average_exist_duration="+average_exist_duration;
    }

    public String getAverage_exist_duration() {
        return average_exist_duration;
    }

    public void setAverage_exist_duration(BigDecimal average_exist_duration) {
        if(average_exist_duration == null) {
            this.average_exist_duration = null;
            return;
        }
        long time_duration = average_exist_duration.longValue();
        long diffSeconds = time_duration % 60;
        long diffMinutes = time_duration / (60) % 60;
        long diffHours = time_duration / (60 * 60) % 24;
        long diffDays = time_duration / (24 * 60 * 60);
        StringBuffer s = new StringBuffer();
        if(diffDays > 0) {
            s.append(diffDays).append("天");
            s.append(diffHours).append("时");
        }else if(diffHours > 0) s.append(diffHours).append("时");
        s.append(diffMinutes).append("分").append(diffSeconds).append("秒");
        this.average_exist_duration = s.toString();
    }
}
