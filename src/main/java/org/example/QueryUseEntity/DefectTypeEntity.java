package org.example.QueryUseEntity;

import java.math.BigDecimal;

public class DefectTypeEntity {
    String type_id;
    String description;
    String average_exist_duration;

    Long count;

    @Override
    public String toString() {
        return "\t\033[32m" +
                "type_id='" + type_id + '\'' +
                ", description='" + description + '\'' +
                ", average_exist_duration='" + average_exist_duration + '\''+
                ", 缺陷数:" + count+"\033[m";
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAverage_exist_duration() {
        return average_exist_duration;
    }

    public void setAverage_exist_duration(BigDecimal average_exist_duration) {
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
