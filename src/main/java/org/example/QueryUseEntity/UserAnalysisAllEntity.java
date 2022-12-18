package org.example.QueryUseEntity;

import java.math.BigDecimal;

public class UserAnalysisAllEntity {
    String type;
    String user_in;
    String user_out;
    String status;
    long total;
    String average_exist_duration;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_in() {
        return user_in;
    }

    public void setUser_in(String user_in) {
        this.user_in = user_in;
    }

    public String getUser_out() {
        return user_out;
    }

    public void setUser_out(String user_out) {
        this.user_out = user_out;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
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
}
